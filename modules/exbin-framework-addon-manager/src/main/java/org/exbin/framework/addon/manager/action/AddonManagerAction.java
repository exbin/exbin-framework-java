/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.addon.manager.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.gui.AddonManagerPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
import org.exbin.framework.addon.manager.gui.AddonsControlPanel;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.operation.AddonUpdateOperation;
import org.exbin.framework.addon.manager.operation.ApplicationModulesUsage;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.UpdateAvailabilityOperation;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationDownloadPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationLicensePanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationOverviewPanel;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.addon.manager.preferences.AddonManagerPreferences;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.service.impl.AddonCatalogServiceImpl;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleFileLocation;
import org.exbin.framework.basic.ModuleRecord;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.gui.MultiStepControlPanel;
import org.exbin.framework.window.api.handler.MultiStepControlHandler;

/**
 * Addon manager action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerAction extends AbstractAction {

    public static final String ACTION_ID = "addonManagerAction";

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManagerAction.class);

    private AddonCatalogService addonCatalogService;
    private ApplicationModulesUsage applicationModulesUsage;
    private AvailableModuleUpdates availableModuleUpdates;
    private int serviceStatus = -1;

    public AddonManagerAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);

        addonCatalogService = new AddonCatalogServiceImpl();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Extract to separate class to not share fields
        AddonsControlPanel controlPanel = new AddonsControlPanel();
        availableModuleUpdates = new AvailableModuleUpdates();
        availableModuleUpdates.readConfigFile();
        final List<AddonsPanel.ItemChangedListener> itemChangedListeners = new ArrayList<>();

        AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
        addonUpdateChanges.readConfigFile();
        List<ItemRecord> installedAddons = new ArrayList<>();
        ModuleProvider moduleProvider = App.getModuleProvider();
        if (moduleProvider instanceof BasicModuleProvider) {
            List<ModuleRecord> basicModulesList = ((BasicModuleProvider) moduleProvider).getModulesList();
            for (ModuleRecord moduleRecord : basicModulesList) {
                AddonRecord itemRecord = new AddonRecord(moduleRecord.getModuleId(), moduleRecord.getName());
                itemRecord.setInstalled(true);
                itemRecord.setAddon(moduleRecord.getFileLocation() == ModuleFileLocation.ADDON);
                itemRecord.setVersion(moduleRecord.getVersion());
                itemRecord.setProvider(moduleRecord.getProvider().orElse(null));
                itemRecord.setHomepage(moduleRecord.getHomepage().orElse(null));
                itemRecord.setDescription(moduleRecord.getDescription().orElse(null));
                itemRecord.setIcon(moduleRecord.getIcon().orElse(null));
                List<DependencyRecord> dependencyRecords = new ArrayList<>();
                for (String dependencyModuleId : moduleRecord.getDependencyModuleIds()) {
                    dependencyRecords.add(new DependencyRecord(dependencyModuleId));
                }
                for (String dependencyLibraryId : moduleRecord.getDependencyLibraries()) {
                    dependencyRecords.add(new DependencyRecord(DependencyRecord.Type.JAR_LIBRARY, dependencyLibraryId));
                }
                itemRecord.setDependencies(dependencyRecords);
                installedAddons.add(itemRecord);
                /*System.out.println(moduleRecord.getModuleId() + "," + moduleRecord.getName() + "," + moduleRecord.getDescription().orElse("") + "," + moduleRecord.getVersion() + "," + moduleRecord.getHomepage().orElse(""));
                for (DependencyRecord dependency : dependencyRecords) {
                    System.out.println("- " + dependency.getType().name() + ", " + dependency.getId());
                } */
            }
            applicationModulesUsage = new ApplicationModulesUsage() {
                @Override
                public boolean hasModule(String moduleId) {
                    return ((BasicModuleProvider) moduleProvider).hasModule(moduleId);
                }

                @Override
                public boolean hasLibrary(String libraryFileName) {
                    return ((BasicModuleProvider) moduleProvider).hasLibrary(libraryFileName);
                }
            };
        }

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        AddonManagerPanel addonManagerPanel = new AddonManagerPanel();
        addonManagerPanel.setPreferredSize(new Dimension(800, 500));
        addonManagerPanel.setController(new AddonManagerPanel.Controller() {

            @Nonnull
            @Override
            public AddonsPanel.Controller getInstalledItemsController() {
                return new AddonsPanel.Controller() {
                    @Override
                    public int getItemsCount() {
                        return installedAddons.size();
                    }

                    @Nonnull
                    @Override
                    public ItemRecord getItem(int index) {
                        return installedAddons.get(index);
                    }

                    @Override
                    public boolean isAlreadyInstalled(String moduleId) {
                        return addonUpdateChanges.hasInstallAddon(moduleId) && !addonUpdateChanges.hasRemoveAddon(moduleId);
                    }

                    @Override
                    public boolean isAlreadyRemoved(String moduleId) {
                        return addonUpdateChanges.hasRemoveAddon(moduleId) && !addonUpdateChanges.hasInstallAddon(moduleId);
                    }

                    @Override
                    public void install(ItemRecord item) {
                        throw new IllegalStateException("Already installed");
                    }

                    @Override
                    public void update(ItemRecord item) {
                        updateItem(item);
                    }

                    @Override
                    public void remove(ItemRecord item) {
                        removeItem(item);
                    }

                    @Override
                    public void changeSelection(ItemRecord item) {
                        String moduleId = item.getId();
                        Set<String> toUpdate = addonManagerPanel.getToUpdate();
                        if (toUpdate.contains(moduleId)) {
                            toUpdate.remove(moduleId);
                        } else {
                            toUpdate.add(moduleId);
                        }
                        updateSelectionChanged(addonManagerPanel.getToUpdateCount());
                    }

                    @Override
                    public boolean isItemSelectedForOperation(ItemRecord item) {
                        Set<String> toUpdate = addonManagerPanel.getToUpdate();
                        return toUpdate.contains(item.getId());
                    }

                    @Override
                    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
                        availableModuleUpdates.addChangeListener(listener);
                    }

                    @Override
                    public void addItemChangedListener(AddonsPanel.ItemChangedListener listener) {
                        itemChangedListeners.add(listener);
                    }

                    @Nonnull
                    @Override
                    public String getModuleDetails(ItemRecord itemRecord) {
                        if (itemRecord.isAddon()) {
                            try {
                                return addonCatalogService.getModuleDetails(itemRecord.getId());
                            } catch (AddonCatalogServiceException ex) {
                                Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        return "";
                    }
                };
            }

            @Nonnull
            @Override
            public AddonsPanel.Controller getAddonsCatalogController() {
                return new AddonsPanel.Controller() {

                    private List<AddonRecord> searchResult;

                    @Override
                    public int getItemsCount() {
                        if (searchResult == null) {
                            searchForAddons();
                        }
                        return searchResult.size();
                    }

                    @Nonnull
                    @Override
                    public ItemRecord getItem(int index) {
                        if (searchResult == null) {
                            searchForAddons();
                        }
                        return searchResult.get(index);
                    }

                    @Override
                    public boolean isAlreadyInstalled(String moduleId) {
                        return addonUpdateChanges.hasInstallAddon(moduleId) && !addonUpdateChanges.hasRemoveAddon(moduleId);
                    }

                    @Override
                    public boolean isAlreadyRemoved(String moduleId) {
                        return addonUpdateChanges.hasRemoveAddon(moduleId) && !addonUpdateChanges.hasInstallAddon(moduleId);
                    }

                    @Override
                    public void install(ItemRecord item) {
                        installItem(item);
                    }

                    @Override
                    public void update(ItemRecord item) {
                        updateItem(item);
                    }

                    @Override
                    public void remove(ItemRecord item) {
                        removeItem(item);
                    }

                    @Override
                    public void changeSelection(ItemRecord item) {
                        String moduleId = item.getId();
                        Set<String> toInstall = addonManagerPanel.getToInstall();
                        if (toInstall.contains(moduleId)) {
                            toInstall.remove(moduleId);
                        } else {
                            toInstall.add(moduleId);
                        }
                        installSelectionChanged(addonManagerPanel.getToInstallCount());
                    }

                    private void searchForAddons() {
                        if (serviceStatus == -1) {
                            searchResult = new ArrayList<>();
                            return;
                        }

                        try {
                            searchResult = addonCatalogService.searchForAddons("");
                            for (int i = searchResult.size() - 1; i >= 0; i--) {
                                AddonRecord record = searchResult.get(i);
                                if (((BasicModuleProvider) moduleProvider).hasModule(record.getId()) && !addonUpdateChanges.hasRemoveAddon(record.getId())) {
                                    searchResult.remove(i);
                                } else {
                                    if (availableModuleUpdates.getStatus() != -1) {
                                        record.setUpdateAvailable(availableModuleUpdates.isUpdateAvailable(record.getId(), record.getVersion()));
                                    }
                                }
                            }
                        } catch (AddonCatalogServiceException ex) {
                            Logger.getLogger(AddonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(addonManagerPanel, "API request failed", "Addon Service Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    @Override
                    public boolean isItemSelectedForOperation(ItemRecord item) {
                        Set<String> toInstall = addonManagerPanel.getToInstall();
                        return toInstall.contains(item.getId());
                    }

                    @Override
                    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
                        if (searchResult != null) {
                            availableModuleUpdates.addChangeListener(listener);
                        }
                    }

                    @Override
                    public void addItemChangedListener(AddonsPanel.ItemChangedListener listener) {
                        itemChangedListeners.add(listener);
                    }

                    @Override
                    public String getModuleDetails(ItemRecord itemRecord) {
                        if (itemRecord.isAddon()) {
                            try {
                                return addonCatalogService.getModuleDetails(itemRecord.getId());
                            } catch (AddonCatalogServiceException ex) {
                                Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        return "";
                    }
                };
            }

            @Override
            public void installItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
                addonUpdateOperation.installItem(item);
                performAddonsOperation(addonUpdateOperation, addonManagerPanel, itemChangedListeners);
            }

            @Override
            public void updateItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
                AddonRecord addonRecord;
                try {
                    addonRecord = addonCatalogService.getAddonDependency(item.getId());
                    addonUpdateOperation.updateItem(addonRecord);
                } catch (AddonCatalogServiceException ex) {
                    Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                performAddonsOperation(addonUpdateOperation, addonManagerPanel, itemChangedListeners);
            }

            @Override
            public void removeItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
                addonUpdateOperation.removeItem(item);
                performAddonsOperation(addonUpdateOperation, addonManagerPanel, itemChangedListeners);
            }

            @Override
            public void installSelectionChanged(int toInstall) {
                controlPanel.setOperationState(AddonsControlPanel.OperationVariant.INSTALL, toInstall);
            }

            @Override
            public void updateSelectionChanged(int toUpdate) {
                controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE, toUpdate);
            }

            @Override
            public void tabSwitched(AddonManagerPanel.Tab tab) {
                switch (tab) {
                    case PACKS:
                        throw new UnsupportedOperationException("Not supported yet.");
                    case ADDONS:
                        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.INSTALL, addonManagerPanel.getToInstallCount());
                        break;
                    case INSTALLED:
                        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE, addonManagerPanel.getToUpdateCount());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        });

        controlPanel.setController(new AddonsControlPanel.Controller() {
            @Override
            public void performOperation() {
                AddonManagerPanel.Tab activeTab = addonManagerPanel.getActiveTab();

                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
                switch (activeTab) {
                    case ADDONS:
                        Set<String> toInstall = addonManagerPanel.getToInstall();
                        if (toInstall.isEmpty()) {
                            for (ItemRecord addon : installedAddons) {
                                if (addon.isUpdateAvailable()) {
                                    AddonRecord addonRecord;
                                    try {
                                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                                        addonUpdateOperation.updateItem(addonRecord);
                                    } catch (AddonCatalogServiceException ex) {
                                        Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        } else {
                            for (String addonId : toInstall) {
                                AddonRecord addonRecord;
                                try {
                                    addonRecord = addonCatalogService.getAddonDependency(addonId);
                                    addonUpdateOperation.installItem(addonRecord);
                                } catch (AddonCatalogServiceException ex) {
                                    Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        break;
                    case INSTALLED:
                        Set<String> toUpdate = addonManagerPanel.getToUpdate();
                        if (toUpdate.isEmpty()) {
                            for (ItemRecord addon : installedAddons) {
                                if (addon.isUpdateAvailable()) {
                                    AddonRecord addonRecord;
                                    try {
                                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                                        addonUpdateOperation.updateItem(addonRecord);
                                    } catch (AddonCatalogServiceException ex) {
                                        Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        } else {
                            for (ItemRecord addon : installedAddons) {
                                if (toUpdate.contains(addon.getId())) {
                                    AddonRecord addonRecord;
                                    try {
                                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                                        addonUpdateOperation.updateItem(addonRecord);
                                    } catch (AddonCatalogServiceException ex) {
                                        Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        break;
                }
                performAddonsOperation(addonUpdateOperation, addonManagerPanel, itemChangedListeners);
            }
        });

        AvailableModuleUpdates.AvailableModulesChangeListener availableModulesChangeListener = (AvailableModuleUpdates checker) -> {
            int availableUpdates = 0;
            for (ItemRecord installedAddon : installedAddons) {
                if (checker.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                    availableUpdates++;
                }
            }
            controlPanel.setAvailableUpdates(availableUpdates);
        };

        availableModuleUpdates.addChangeListener(availableModulesChangeListener);
        availableModuleUpdates.notifyChanged();
        try {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle appBundle = languageModule.getAppBundle();
            String releaseString = appBundle.getString(ApplicationInfoKeys.APPLICATION_RELEASE);
            serviceStatus = addonCatalogService.checkStatus(releaseString);
            // TODO
            // controlPanel.showLegacyWarning();
            if (serviceStatus == -1) {
                controlPanel.showManualOnlyWarning();
            } else {
                PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                AddonManagerPreferences addonPreferences = new AddonManagerPreferences(preferencesModule.getAppPreferences());
                String activatedVersion = addonPreferences.getActivatedVersion();
                // Version 0.3.0-SNAPSHOT is bugged to read only single record. Ignore service status
                boolean buggedVersion = "0.3.0-SNAPSHOT".equals(activatedVersion);
                if (serviceStatus > availableModuleUpdates.getStatus() || buggedVersion) {
                    UpdateAvailabilityOperation availabilityOperation = new UpdateAvailabilityOperation(addonCatalogService);
                    Thread thread = new Thread(() -> {
                        availabilityOperation.run();
                        availableModuleUpdates.setLatestVersion(serviceStatus, availabilityOperation.getLatestVersions());
                        availableModuleUpdates.writeConfigFile();
                        if (buggedVersion) {
                            if (addonUpdateChanges.hasRemoveAddon("org.exbin.framework.addon.manager.AddonManagerModule")) {
                                addonPreferences.setActivatedVersion("0.3.0-SNAPSHOT.1");
                            }
                        }
                    });
                    thread.start();
                }
            }
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, "Status check failed", ex);
        }
        final WindowHandler dialog = windowModule.createDialog(addonManagerPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), addonManagerPanel.getClass(), addonManagerPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, addonManagerPanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }

    public void performAddonsOperation(AddonUpdateOperation addonUpdateOperation, JComponent parentComponent, List<AddonsPanel.ItemChangedListener> finishListeners) {
        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
        AddonOperationPanel operationPanel = new AddonOperationPanel();
        operationPanel.setPreferredSize(new Dimension(600, 300));
        operationPanel.goToStep(AddonOperationPanel.Step.OVERVIEW);
        AddonOperationOverviewPanel panel = (AddonOperationOverviewPanel) operationPanel.getActiveComponent();
        for (String operation : addonUpdateOperation.getOperations()) {
            panel.addOperation(operation);
        }

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(operationPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), operationPanel.getClass(), operationPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, operationPanel.getResourceBundle());
        MultiStepControlHandler.MultiStepControlEnablementListener enablementListener = controlPanel.createEnablementListener();
        controlPanel.setHandler(new MultiStepControlHandler() {

            private AddonOperationPanel.Step step = AddonOperationPanel.Step.OVERVIEW;
            private DownloadOperation downloadOperation = null;

            @Override
            public void controlActionPerformed(MultiStepControlHandler.ControlActionType actionType) {
                switch (actionType) {
                    case CANCEL:
                        if (downloadOperation != null) {
                            downloadOperation.cancelOperation();
                        }
                        dialog.close();
                        break;
                    case NEXT:
                        switch (step) {
                            case OVERVIEW:
                                List<LicenseItemRecord> licenseRecords = addonUpdateOperation.getLicenseRecords();
                                if (!licenseRecords.isEmpty()) {
                                    step = AddonOperationPanel.Step.LICENSE;
                                    operationPanel.goToStep(step);
                                    AddonOperationLicensePanel panel = (AddonOperationLicensePanel) operationPanel.getActiveComponent();
                                    panel.setController(new AddonOperationLicensePanel.Controller() {
                                        @Override
                                        public void approvalStateChanged(int toApprove) {
                                            enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, toApprove == 0);
                                        }
                                    });
                                    panel.setLicenseRecords(licenseRecords);
                                    enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, false);
                                    break;
                                }
                            // no break
                            case LICENSE:
                                List<DownloadItemRecord> downloadRecords = addonUpdateOperation.getDownloadRecords();
                                if (!downloadRecords.isEmpty()) {
                                    goToDownload(downloadRecords);
                                    break;
                                }
                            // no break
                            case DOWNLOAD:
                                step = AddonOperationPanel.Step.SUCCESS;
                                operationPanel.goToStep(step);
                                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, false);
                                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.CANCEL, false);
                                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.FINISH, true);
                                break;
                            default:
                                throw new AssertionError();
                        }
                        break;
                    case PREVIOUS:
                        // TODO
                        switch (step) {
                            case LICENSE:
                                step = AddonOperationPanel.Step.OVERVIEW;
                                operationPanel.goToStep(step);
                                break;
                            default:
                                throw new AssertionError();
                        }
                        break;
                    case FINISH:
                        addonUpdateOperation.finished();
                        for (AddonsPanel.ItemChangedListener listener : finishListeners) {
                            listener.itemChanged();
                        }
                        dialog.close();
                        break;
                    default:
                        throw new AssertionError();
                }
            }

            private void goToDownload(List<DownloadItemRecord> downloadRecords) {
                step = AddonOperationPanel.Step.DOWNLOAD;
                operationPanel.goToStep(step);
                AddonOperationDownloadPanel panel = (AddonOperationDownloadPanel) operationPanel.getActiveComponent();
                panel.setDownloadedItemRecords(downloadRecords);
                downloadOperation = addonCatalogService.createDownloadsOperation(downloadRecords);
                downloadOperation.setItemChangeListener(new DownloadOperation.ItemChangeListener() {
                    @Override
                    public void itemChanged(int itemIndex) {
                        panel.notifyDownloadedItemChanged(itemIndex);
                    }

                    @Override
                    public void progressChanged(int itemIndex) {
                        DownloadItemRecord record = downloadRecords.get(itemIndex);
                        panel.setProgress(record.getFileName(), downloadOperation.getOperationProgress(), false);
                    }

                });
                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, false);
                Thread thread = new Thread(() -> {
                    downloadOperation.run();
                    enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, true);
                });
                thread.start();
            }
        });
        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, true);
        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.FINISH, false);
        dialog.showCentered(parentComponent);
        dialog.dispose();
    }
}
