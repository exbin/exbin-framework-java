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
package org.exbin.framework.addon.manager;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
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
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.service.impl.AddonCatalogServiceImpl;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleFileLocation;
import org.exbin.framework.basic.ModuleRecord;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.window.api.gui.MultiStepControlPanel;
import org.exbin.framework.window.api.controller.MultiStepControlController;

/**
 * Addon manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManager {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManager.class);

    private AddonCatalogService addonCatalogService;
    private ApplicationModulesUsage applicationModulesUsage;
    private AvailableModuleUpdates availableModuleUpdates = new AvailableModuleUpdates();
    private AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
    private List<ItemRecord> installedAddons = new ArrayList<>();
    private int serviceStatus = -1;

    public AddonManager() {
        addonCatalogService = new AddonCatalogServiceImpl();
    }

    public void init() {
        availableModuleUpdates.readConfigFile();
        addonUpdateChanges.readConfigFile();

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

        AvailableModuleUpdates.AvailableModulesChangeListener availableModulesChangeListener = (AvailableModuleUpdates checker) -> {
            int availableUpdates = 0;
            for (ItemRecord installedAddon : installedAddons) {
                if (checker.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                    availableUpdates++;
                }
            }
            // TODO controlPanel.setAvailableUpdates(availableUpdates);
        };

        availableModuleUpdates.addChangeListener(availableModulesChangeListener);
        availableModuleUpdates.notifyChanged();

        Thread thread = new Thread(() -> {
            try {
                LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
                ResourceBundle appBundle = languageModule.getAppBundle();
                String releaseString = appBundle.getString(ApplicationInfoKeys.APPLICATION_RELEASE);
                serviceStatus = addonCatalogService.checkStatus(releaseString);
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, "Status check failed", ex);
                serviceStatus = -1;
            }
            // TODO
            // controlPanel.showLegacyWarning();
            if (serviceStatus == -1) {
                // TODO controlPanel.showManualOnlyWarning();
            } else {
                if (serviceStatus > availableModuleUpdates.getStatus()) {
                    UpdateAvailabilityOperation availabilityOperation = new UpdateAvailabilityOperation(addonCatalogService);
                    availabilityOperation.run();
                    availableModuleUpdates.setLatestVersion(serviceStatus, availabilityOperation.getLatestVersions());
                    availableModuleUpdates.writeConfigFile();
                }
            }
        });
        thread.start();
    }

    public boolean isAlreadyInstalled(String moduleId) {
        return addonUpdateChanges.hasInstallAddon(moduleId) && !addonUpdateChanges.hasRemoveAddon(moduleId);
    }

    public boolean isAlreadyRemoved(String moduleId) {
        return addonUpdateChanges.hasRemoveAddon(moduleId) && !addonUpdateChanges.hasInstallAddon(moduleId);
    }

    public void installItem(ItemRecord item, Component parentComponent, @Nullable Runnable finishListener) {
        AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
        addonUpdateOperation.installItem(item);
        performAddonsOperation(addonUpdateOperation, parentComponent, finishListener);
    }

    public void updateItem(ItemRecord item, Component parentComponent, @Nullable Runnable finishListener) {
        AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
        AddonRecord addonRecord;
        try {
            addonRecord = addonCatalogService.getAddonDependency(item.getId());
            addonUpdateOperation.updateItem(addonRecord, item);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        performAddonsOperation(addonUpdateOperation, parentComponent, finishListener);
    }

    public void removeItem(ItemRecord item, Component parentComponent, @Nullable Runnable finishListener) {
        AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
        addonUpdateOperation.removeItem(item);
        performAddonsOperation(addonUpdateOperation, parentComponent, finishListener);
    }

    public void installAddons(Set<String> toInstall, Component parentComponent, @Nullable Runnable finishListener) {
        AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
        if (toInstall.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        addonUpdateOperation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        performAddonsOperation(addonUpdateOperation, parentComponent, finishListener);
    }

    public void updateAddons(Set<String> toUpdate, Component parentComponent, @Nullable Runnable finishListener) {
        AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
        if (toUpdate.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        addonUpdateOperation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            for (ItemRecord addon : installedAddons) {
                if (toUpdate.contains(addon.getId())) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        addonUpdateOperation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        performAddonsOperation(addonUpdateOperation, parentComponent, finishListener);
    }

    @Nonnull
    public List<ItemRecord> getInstalledAddons() {
        return installedAddons;
    }

    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
        availableModuleUpdates.addChangeListener(listener);
    }

    @Nonnull
    public String getModuleDetails(ItemRecord itemRecord) {
        if (itemRecord.isAddon()) {
            try {
                return addonCatalogService.getModuleDetails(itemRecord.getId());
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "";
    }

    @Nonnull
    public List<AddonRecord> searchForAddons() throws AddonCatalogServiceException {
        if (serviceStatus == -1) {
            return new ArrayList<>();
        }

        List<AddonRecord> searchResult = addonCatalogService.searchForAddons("");
        for (int i = searchResult.size() - 1; i >= 0; i--) {
            AddonRecord record = searchResult.get(i);
            ModuleProvider moduleProvider = App.getModuleProvider();
            if (((BasicModuleProvider) moduleProvider).hasModule(record.getId()) && !addonUpdateChanges.hasRemoveAddon(record.getId())) {
                searchResult.remove(i);
            } else {
                if (availableModuleUpdates.getStatus() != -1) {
                    record.setUpdateAvailable(availableModuleUpdates.isUpdateAvailable(record.getId(), record.getVersion()));
                }
            }
        }
        return searchResult;
    }

    public void performAddonsOperation(AddonUpdateOperation addonUpdateOperation, Component parentComponent, @Nullable Runnable finishListener) {
        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
        AddonOperationPanel operationPanel = new AddonOperationPanel();
        operationPanel.setPreferredSize(new Dimension(600, 300));
        operationPanel.goToStep(AddonOperationPanel.Step.OVERVIEW);
        AddonOperationOverviewPanel panel = (AddonOperationOverviewPanel) operationPanel.getActiveComponent().get();
        for (String operation : addonUpdateOperation.getOperations()) {
            panel.addOperation(operation);
        }

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(operationPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), operationPanel.getClass(), operationPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, operationPanel.getResourceBundle());
        controlPanel.setController(new MultiStepControlController() {

            private AddonOperationPanel.Step step = AddonOperationPanel.Step.OVERVIEW;
            private DownloadOperation downloadOperation = null;

            @Override
            public void controlActionPerformed(MultiStepControlController.ControlActionType actionType) {
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
                                    AddonOperationLicensePanel panel = (AddonOperationLicensePanel) operationPanel.getActiveComponent().get();
                                    panel.setController(new AddonOperationLicensePanel.Controller() {
                                        @Override
                                        public void approvalStateChanged(int toApprove) {
                                            controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, toApprove == 0);
                                        }
                                    });
                                    panel.setLicenseRecords(licenseRecords);
                                    controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, false);
                                    break;
                                } // no break
                            case LICENSE:
                                List<DownloadItemRecord> downloadRecords = addonUpdateOperation.getDownloadRecords();
                                if (!downloadRecords.isEmpty()) {
                                    goToDownload(downloadRecords);
                                    break;
                                } // no break
                            case DOWNLOAD:
                                step = AddonOperationPanel.Step.SUCCESS;
                                operationPanel.goToStep(step);
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, false);
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.CANCEL, false);
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.FINISH, true);
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
                        if (finishListener != null) {
                            finishListener.run();
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
                AddonOperationDownloadPanel panel = (AddonOperationDownloadPanel) operationPanel.getActiveComponent().get();
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
                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, false);
                Thread thread = new Thread(() -> {
                    downloadOperation.run();
                    controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, true);
                });
                thread.start();
            }
        });
        controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, true);
        controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.FINISH, false);
        dialog.showCentered(parentComponent);
        dialog.dispose();
    }
}
