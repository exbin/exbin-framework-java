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
package org.exbin.framework.addon.manager.operation.service;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.operation.AddonModificationsOperation;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationDownloadPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationLicensePanel;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.AddonOperation;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.ApplicationModulesUsage;
import org.exbin.framework.addon.manager.operation.AddonModificationStep;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationOverviewPanel;
import org.exbin.framework.window.api.gui.MultiStepControlPanel;
import org.exbin.framework.window.api.controller.MultiStepControlController;

/**
 * Addon operation service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonOperationService {

    protected java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonOperationService.class);

    protected AddonManager addonManager;
    protected AddonCatalogService addonCatalogService;

    public AddonOperationService(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
    }

    public void performAddonOperations(List<AddonOperation> operations, Component parentComponent) {
        AddonModificationsOperation modifications = createOperation();
        for (AddonOperation operation : operations) {
            switch (operation.getVariant()) {
                case INSTALL:
                    modifications.installItem(operation.getItem());
                    break;
                case UPDATE:
                    ItemRecord item = operation.getItem();
                    modifications.updateItem(item, item);
                    break;
                case REMOVE:
                    modifications.removeItem(operation.getItem());
                    break;
            }
        }
        performAddonsOperation(modifications, parentComponent);
    }

    public void installItem(ItemRecord item, Component parentComponent) {
        AddonModificationsOperation operation = createOperation();
        operation.installItem(item);
        performAddonsOperation(operation, parentComponent);
    }

    public void updateItem(ItemRecord item, Component parentComponent) {
        AddonModificationsOperation operation = createOperation();
        AddonRecord addonRecord;
        try {
            addonRecord = addonCatalogService.getAddonDependency(item.getId());
            operation.updateItem(addonRecord, item);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        performAddonsOperation(operation, parentComponent);
    }

    public void removeItem(ItemRecord item, Component parentComponent) {
        AddonModificationsOperation operation = createOperation();
        operation.removeItem(item);
        performAddonsOperation(operation, parentComponent);
    }

    public void installAddons(Set<String> toInstall, Component parentComponent) {
        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        AddonModificationsOperation operation = createOperation();
        if (toInstall.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            for (String addonId : toInstall) {
                AddonRecord addonRecord;
                try {
                    addonRecord = addonCatalogService.getAddonDependency(addonId);
                    operation.installItem(addonRecord);
                } catch (AddonCatalogServiceException ex) {
                    Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        performAddonsOperation(operation, parentComponent);
    }

    public void updateAddons(Set<String> toUpdate, Component parentComponent) {
        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        AddonModificationsOperation operation = createOperation();
        if (toUpdate.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            for (ItemRecord addon : installedAddons) {
                if (toUpdate.contains(addon.getId())) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        performAddonsOperation(operation, parentComponent);
    }

    public void performAddonsOperation(Component parentComponent) {
        AddonModificationsOperation operation = createOperation();
        performAddonsOperation(operation, parentComponent);
    }

    @Nonnull
    private AddonModificationsOperation createOperation() {
        AddonUpdateChanges addonUpdateChanges = addonManager.getAddonUpdateChanges();
        ApplicationModulesUsage applicationModulesUsage = addonManager.getApplicationModulesUsage();
        return new AddonModificationsOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
    }

    public void performAddonsOperation(AddonModificationsOperation modificationsOperation, Component parentComponent) {
        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
        AddonOperationPanel operationPanel = new AddonOperationPanel();
        operationPanel.setPreferredSize(new Dimension(600, 300));

        AddonOperationOverviewPanel panel = (AddonOperationOverviewPanel) operationPanel.getActiveComponent().get();
        for (String operation : modificationsOperation.getOperations()) {
            panel.addOperation(operation);
        }

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(operationPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), operationPanel.getClass(), operationPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, operationPanel.getResourceBundle());
        controlPanel.setController(new MultiStepControlController() {

            private AddonModificationStep step = AddonModificationStep.OVERVIEW;
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
                                List<LicenseItemRecord> licenseRecords = modificationsOperation.getLicenseRecords();
                                if (!licenseRecords.isEmpty()) {
                                    step = AddonModificationStep.LICENSE;
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
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.PREVIOUS, true);
                                List<DownloadItemRecord> downloadRecords = modificationsOperation.getDownloadRecords();
                                if (!downloadRecords.isEmpty()) {
                                    goToDownload(downloadRecords);
                                    break;
                                } // no break
                            case DOWNLOAD:
                                step = AddonModificationStep.SUCCESS;
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
                                step = AddonModificationStep.OVERVIEW;
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.PREVIOUS, false);
                                operationPanel.goToStep(step);
                                break;
                            default:
                                throw new AssertionError();
                        }
                        break;
                    case FINISH:
                        modificationsOperation.finished();
//                        if (finishListener != null) {
//                            finishListener.run();
//                        }
                        dialog.close();
                        break;
                    default:
                        throw new AssertionError();
                }
            }

            private void goToDownload(List<DownloadItemRecord> downloadRecords) {
                step = AddonModificationStep.DOWNLOAD;
                operationPanel.goToStep(step);
                AddonOperationDownloadPanel panel = (AddonOperationDownloadPanel) operationPanel.getActiveComponent().get();
                panel.setDownloadedItemRecords(downloadRecords);
                downloadOperation = new DownloadOperation(downloadRecords);
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
