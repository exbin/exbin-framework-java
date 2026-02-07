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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.gui.AddonsCartPanel;
import org.exbin.framework.addon.manager.gui.AddonsManagerPanel;
import org.exbin.framework.addon.manager.api.AddonManagerPage;
import org.exbin.framework.addon.manager.operation.CatalogAvailableUpdatesOperation;
import org.exbin.framework.addon.manager.operation.CatalogCheckStatusOperation;
import org.exbin.framework.addon.manager.operation.CatalogSearchOperation;
import org.exbin.framework.addon.manager.operation.service.AddonOperationService;
import org.exbin.framework.operation.api.ProgressOperation;
import org.exbin.framework.operation.api.TitledOperation;

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
                AddonOperationService addonOperationService = new AddonOperationService(AddonManager.this);
                addonOperationService.setAddonCatalogService(addonCatalogService);
                addonOperationService.performAddonOperations(cartOperations, cartPanel);

                // TODO
//                if (success) {
//                    cartOperations.clear();
//                }
                notifyChanged();
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

        // TODO
        /* AvailableModuleUpdates.AvailableModulesChangeListener availableModulesChangeListener = (AvailableModuleUpdates checker) -> {
            int availableUpdates = 0;
            for (ItemRecord installedAddon : installedAddons) {
                if (checker.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                    availableUpdates++;
                }
            }
            controlPanel.setAvailableUpdates(availableUpdates);
        };

        AvailableModuleUpdates availableModuleUpdates = addonsState.getAvailableModuleUpdates();
        availableModuleUpdates.addChangeListener(availableModulesChangeListener);
        availableModuleUpdates.notifyChanged(); */
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

                runOperation(new CatalogCheckStatusOperation(addonCatalogService, (status) -> {
                    if (status >= 0) {
                        runOperation(new CatalogAvailableUpdatesOperation(addonCatalogService, this, status));
                        runOperation(new CatalogSearchOperation(addonCatalogService, this, "", ((AddonsCatalogPage) managerPage)::setAddonItems));
                    }
                }));
            }
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

    @ParametersAreNonnullByDefault
    public interface AddonManagerStatusListener {

        void setProgressStatus(String status);

        void setStatusLabel(String text);

        void clear();
    }
}
