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
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.gui.AddonManagerPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
import org.exbin.framework.addon.manager.gui.AddonsControlPanel;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.operation.AddonUpdateOperation;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationDownloadPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationLicensePanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationOverviewPanel;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.impl.AddonCatalogServiceImpl;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleFileLocation;
import org.exbin.framework.basic.ModuleRecord;
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

    private AddonCatalogService addonManagerService;

    public AddonManagerAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);

        addonManagerService = new AddonCatalogServiceImpl();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AddonsControlPanel controlPanel = new AddonsControlPanel();

        AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
        addonUpdateChanges.readConfigFile();
        List<ModuleRecord> basicModulesList = null;
        List<ItemRecord> installedAddons = new ArrayList<>();
        ModuleProvider moduleProvider = App.getModuleProvider();
        if (moduleProvider instanceof BasicModuleProvider) {
            basicModulesList = ((BasicModuleProvider) moduleProvider).getModulesList();
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
                // System.out.println(moduleRecord.getModuleId() + "," + moduleRecord.getName() + "," + moduleRecord.getDescription().orElse("") + "," + moduleRecord.getVersion() + "," + moduleRecord.getHomepage().orElse(""));
            }
        }
        final List<ModuleRecord> modulesList = basicModulesList == null ? new ArrayList<>() : basicModulesList;

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        AddonManagerPanel addonManagerPanel = new AddonManagerPanel();
        addonManagerPanel.setPreferredSize(new Dimension(800, 500));
        addonManagerPanel.setAddonCatalogService(addonManagerService);
        addonManagerPanel.setController(new AddonManagerPanel.Controller() {

            @Nonnull
            @Override
            public List<ItemRecord> getInstalledItems() {
                return installedAddons;
            }

            @Override
            public void installItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonManagerService, modulesList, addonUpdateChanges);
                addonUpdateOperation.installItem(item);
//                DownloadItemRecord downloadRecord = new DownloadItemRecord("Test", "exbin-framework-flatlaf-laf-fat-0.2.4-SNAPSHOT.jar");
//                try {
//                    downloadRecord.setUrl(new URL("https://bined.exbin.org/addon/download/exbin-framework-flatlaf-laf-fat-0.2.4-SNAPSHOT.jar"));
//                    operations.add("Download module file: " + downloadRecord.getFileName());
//                } catch (MalformedURLException ex) {
//                    Logger.getLogger(AddonManagerAction.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                downloadRecords.add(downloadRecord);
                performAddonsOperation(addonUpdateOperation, addonManagerPanel);
            }

            @Override
            public void updateItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonManagerService, modulesList, addonUpdateChanges);
                addonUpdateOperation.updateItem(item);
                performAddonsOperation(addonUpdateOperation, addonManagerPanel);
            }

            @Override
            public void removeItem(ItemRecord item) {
                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonManagerService, modulesList, addonUpdateChanges);
                addonUpdateOperation.removeItem(item);
                performAddonsOperation(addonUpdateOperation, addonManagerPanel);
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

                AddonUpdateOperation addonUpdateOperation = new AddonUpdateOperation(addonManagerService, modulesList, addonUpdateChanges);
                switch (activeTab) {
                    case ADDONS:
                        Set<String> toUpdate = addonManagerPanel.getToUpdate();
                        AddonCatalogService.AddonsListResult searchForAddons = addonManagerService.searchForAddons("");
                        for (int i = 0; i < searchForAddons.itemsCount(); i++) {
                            AddonRecord addon = searchForAddons.getLazyItem(i);
                            if (toUpdate.contains(addon.getId())) {
                                addonUpdateOperation.installItem(addon);
                            }
                        }
                        break;

                    case INSTALLED:
                        Set<String> toInstall = addonManagerPanel.getToInstall();
                        for (ItemRecord addon : installedAddons) {
                            if (toInstall.contains(addon.getId())) {
                                addonUpdateOperation.updateItem(addon);
                            }
                        }
                        break;

                }
                performAddonsOperation(addonUpdateOperation, addonManagerPanel);
            }
        });

        // TODO
        // controlPanel.showLegacyWarning();
        // controlPanel.showManualOnlyWarning();
        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE_ALL, 0);
        final WindowHandler dialog = windowModule.createDialog(addonManagerPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), addonManagerPanel.getClass(), addonManagerPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, addonManagerPanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }

    public void performAddonsOperation(AddonUpdateOperation addonUpdateOperation, JComponent parentComponent) {
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
                                if (addonUpdateOperation.hasLicenseRecords()) {
                                    step = AddonOperationPanel.Step.LICENSE;
                                    operationPanel.goToStep(step);
                                    AddonOperationLicensePanel panel = (AddonOperationLicensePanel) operationPanel.getActiveComponent();
                                    panel.setLicenseRecords(addonUpdateOperation.getLicenseRecords());
                                    // TODO panel.addChangeListener()
                                    enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, false);
                                } else if (addonUpdateOperation.hasDownloadRecords()) {
                                    goToDownload();
                                } else {
                                    // TODO Remove only operation
                                }
                                break;
                            case LICENSE:
                                if (addonUpdateOperation.hasDownloadRecords()) {
                                    goToDownload();
                                } else {
                                    // TODO Remove only operation
                                }
                                break;
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
                        dialog.close();
                        break;
                    default:
                        throw new AssertionError();
                }
            }

            private void goToDownload() {
                step = AddonOperationPanel.Step.DOWNLOAD;
                operationPanel.goToStep(step);
                List<DownloadItemRecord> downloadRecords = addonUpdateOperation.getDownloadRecords();
                AddonOperationDownloadPanel panel = (AddonOperationDownloadPanel) operationPanel.getActiveComponent();
                panel.setDownloadedItemRecords(downloadRecords);
                downloadOperation = addonManagerService.createDownloadsOperation(downloadRecords);
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
