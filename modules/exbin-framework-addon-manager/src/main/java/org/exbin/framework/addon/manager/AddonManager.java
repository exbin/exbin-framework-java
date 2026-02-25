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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.gui.AddonsCartPanel;
import org.exbin.framework.addon.manager.gui.AddonsManagerPanel;
import org.exbin.framework.addon.manager.api.AddonManagerPage;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.operation.AddonModificationStep;
import org.exbin.framework.addon.manager.operation.AddonModificationsOperation;
import org.exbin.framework.addon.manager.operation.CatalogAvailableUpdatesOperation;
import org.exbin.framework.addon.manager.operation.CatalogCheckStatusOperation;
import org.exbin.framework.addon.manager.operation.CatalogModuleDetailOperation;
import org.exbin.framework.addon.manager.operation.CatalogSearchOperation;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationDownloadPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationLicensePanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationOverviewPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.addon.manager.operation.service.AddonOperationService;
import org.exbin.framework.operation.api.ProgressOperation;
import org.exbin.framework.operation.api.TitledOperation;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.controller.MultiStepControlController;
import org.exbin.framework.window.api.gui.MultiStepControlPanel;

/**
 * Addon manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManager {

    protected java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManager.class);

    protected final AddonsManagerPanel managerPanel = new AddonsManagerPanel();
    protected final List<AddonManagerPage> managerPages = new ArrayList<>();
    protected final List<AddonOperation> cartOperations = new ArrayList<>();

    protected AddonCatalogService addonCatalogService;
    protected final AddonsState addonsState = new AddonsState();
    protected AddonManagerStatusListener statusListener;

    protected final ExecutorService operationsExecutor = Executors.newFixedThreadPool(1);

    public AddonManager() {
    }

    public void init() {
        addonsState.init();

        AddonsCartPanel cartPanel = new AddonsCartPanel();
        managerPanel.setPreferredSize(new Dimension(800, 500));
        managerPanel.setCartComponent(cartPanel);
        managerPanel.setController(new AddonsManagerPanel.Controller() {
            @Override
            public void tabSwitched() {
                notifyChanged();
            }

            @Override
            public void openCatalog() {
                notifyChanged();
            }

            @Override
            public void openCart() {
                cartPanel.setCartItems(getCartOperations());
            }

            @Override
            public void setFilter(String filter) {
                for (AddonManagerPage managerPage : managerPages) {
                    Runnable operation = managerPage.createFilterOperation(filter);
                    runOperation(operation);
                }
            }

            @Override
            public void setSearch(String search) {
                for (AddonManagerPage managerPage : managerPages) {
                    Runnable operation = managerPage.createSearchOperation(search);
                    runOperation(operation);
                }
            }
        });

        cartPanel.setController(new AddonsCartPanel.Controller() {
            @Override
            public void runOperations() {
                runCartModifications();
            }

            @Override
            public void performRemove(int[] indices) {
                cartPanel.removeIndices(indices);
                removeIndices(indices);
            }
        });

        AddonsCatalogPage catalogPage = new AddonsCatalogPage();
        catalogPage.setAddonManager(this);
        addManagerPage(catalogPage);

        AddonsInstalledPage installedPage = new AddonsInstalledPage();
        installedPage.setAddonManager(this);
        addManagerPage(installedPage);
    }

    public void setStatusListener(AddonManagerStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    private void removeIndices(int[] indices) {
        if (indices.length == 0) {
            return;
        }

        Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            cartOperations.remove(i);
        }
        managerPanel.setCartItemsCount(cartOperations.size());
    }

    private void runOperation(Runnable operation) {
        operationsExecutor.submit(() -> {
            if (operation instanceof TitledOperation) {
                if (operation instanceof ProgressOperation) {
                    statusListener.setProgressStatus(((TitledOperation) operation).getTitle());
                } else {
                    statusListener.setStatusLabel(((TitledOperation) operation).getTitle());
                }
            }

            operation.run();
            statusListener.clear();
        });
    }

    public void notifyChanged() {
        AddonManagerPage managerTab = managerPanel.getActiveTab();
        managerTab.notifyChanged();

//        if (managerTab instanceof AddonsCatalogPage) {
//            ((AddonsCatalogPage) managerTab).set
//        } else if (managerTab instanceof AddonsInstalledPage) {
//            ((AddonsInstalledPage) managerTab).set
//        }
    }

    public void addManagerPage(AddonManagerPage page) {
        managerPages.add(page);
        managerPanel.addManagerPage(page);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Nonnull
    public AddonsManagerPanel getManagerPanel() {
        return managerPanel;
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
        managerPanel.setCatalogUrl(addonCatalogService.getCatalogPageUrl());
    }

    public void refreshCatalog() {
        for (AddonManagerPage managerPage : managerPages) {
            if (managerPage instanceof AddonsCatalogPage) {
                ((AddonsCatalogPage) managerPage).setAddonCatalogService(addonCatalogService);

                runOperation(new CatalogCheckStatusOperation(this, addonCatalogService, (status) -> {
                    if (status >= 0) {
                        runOperation(new CatalogAvailableUpdatesOperation(addonCatalogService, this, status, this::updateLatestVersions));
                        runOperation(new CatalogSearchOperation(addonCatalogService, this, "", ((AddonsCatalogPage) managerPage)::setAddonItems));
                    }
                }));
            }
        }
    }

    public void updateAll() {
        List<AddonOperation> updateOperations = new ArrayList<>();
        List<ItemRecord> installedAddons = addonsState.getInstalledAddons();
        AvailableModuleUpdates availableModuleUpdates = addonsState.getAvailableModuleUpdates();
        for (ItemRecord installedAddon : installedAddons) {
            if (availableModuleUpdates.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                updateOperations.add(new AddonOperation(AddonOperationVariant.UPDATE, installedAddon));
            }
        }

        AddonOperationService addonOperationService = new AddonOperationService(AddonManager.this);
        addonOperationService.setAddonCatalogService(addonCatalogService);
        AddonModificationsOperation modificationsOperations = addonOperationService.performAddonOperations(updateOperations);
        if (performAddonsOperation(modificationsOperations, managerPanel)) {
            notifyChanged();
        }
    }

    @Nonnull
    public AvailableModuleUpdates getAvailableModuleUpdates() {
        return addonsState.getAvailableModuleUpdates();
    }

    @Nonnull
    public AddonUpdateChanges getAddonUpdateChanges() {
        return addonsState.getAddonUpdateChanges();
    }

    @Nonnull
    public ApplicationModulesUsage getApplicationModulesUsage() {
        return addonsState.getApplicationModulesUsage();
    }

    public boolean isModuleInstalled(String moduleId) {
        return addonsState.isModuleInstalled(moduleId);
    }

    public boolean isModuleRemoved(String moduleId) {
        return addonsState.isModuleRemoved(moduleId);
    }

    public void addCartOperation(AddonOperation operation) {
        cartOperations.add(operation);
        managerPanel.setCartItemsCount(cartOperations.size());

        if (cartOperations.size() == 1) {
            int result = JOptionPane.showOptionDialog(managerPanel,
                    resourceBundle.getString("runModificationsQuestion.message"),
                    resourceBundle.getString("runModificationsQuestion.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{
                        resourceBundle.getString("runModificationsQuestion.continue"),
                        resourceBundle.getString("runModificationsQuestion.run")
                    },
                    resourceBundle.getString("runModificationsQuestion.run"));
            if (result == 1) {
                runCartModifications();
            }
        }
    }

    public void runCartModifications() {
        AddonOperationService addonOperationService = new AddonOperationService(AddonManager.this);
        addonOperationService.setAddonCatalogService(addonCatalogService);
        AddonModificationsOperation modificationsOerations = addonOperationService.performAddonOperations(cartOperations);
        if (performAddonsOperation(modificationsOerations, managerPanel)) {
            cartOperations.clear();
            notifyChanged();
        }
    }

    public boolean performAddonsOperation(AddonModificationsOperation modificationsOperation, Component parentComponent) {
        boolean[] success = new boolean[]{false};
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
                                controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.PREVIOUS, false);
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
                        success[0] = true;
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
                    try {
                        downloadOperation.run();
                        controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, true);
                    } catch (Throwable tw) {
                        JOptionPane.showMessageDialog(parentComponent,
                                String.format(resourceBundle.getString("downloadFailedError.message"), tw.getLocalizedMessage()),
                                resourceBundle.getString("downloadFailedError.title"),
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
                thread.start();
            }
        });
        controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.NEXT, true);
        controlPanel.setActionEnabled(MultiStepControlController.ControlActionType.FINISH, false);
        dialog.showCentered(parentComponent);
        dialog.dispose();

        return success[0];
    }

    public boolean isInCart(String moduleId, AddonOperationVariant variant) {
        for (AddonOperation cartOperation : cartOperations) {
            if (moduleId.equals(cartOperation.getItem().getId()) && variant == cartOperation.getVariant()) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public List<ItemRecord> getInstalledAddons() {
        return addonsState.getInstalledAddons();
    }

    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
        addonsState.addUpdateAvailabilityListener(listener);
    }

    @Nonnull
    public List<AddonOperation> getCartOperations() {
        return cartOperations;
    }

    private void updateLatestVersions() {
        AvailableModuleUpdates availableModuleUpdates = getAvailableModuleUpdates();
        List<ItemRecord> installedAddons = addonsState.getInstalledAddons();
        int availableUpdates = 0;
        for (ItemRecord installedAddon : installedAddons) {
            if (availableModuleUpdates.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                availableUpdates++;
            }
        }
        statusListener.setAvailableUpdates(availableUpdates);
    }

    public void requestModuleDetail(ItemRecord itemRecord, AddonsPanel addonsPanel) {
        runOperation(new CatalogModuleDetailOperation(addonCatalogService, this, itemRecord, (details) -> addonsPanel.setModuleDetail(itemRecord, details)));
    }

    @ParametersAreNonnullByDefault
    public interface AddonManagerStatusListener {

        void setProgressStatus(String status);

        void setStatusLabel(String text);

        void clear();

        void setAvailableUpdates(int updatesCount);
    }
}
