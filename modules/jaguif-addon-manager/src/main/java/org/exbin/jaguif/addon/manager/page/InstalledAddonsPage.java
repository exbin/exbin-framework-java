/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.addon.manager.page;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.addon.manager.api.AddonOperation;
import org.exbin.jaguif.addon.manager.api.AddonManagerModuleApi;
import org.exbin.jaguif.addon.manager.api.ItemRecord;
import org.exbin.jaguif.addon.manager.api.AddonManagerPage;
import org.exbin.jaguif.addon.manager.api.AddonOperationVariant;
import org.exbin.jaguif.addon.manager.api.AddonsListComponent;
import org.exbin.jaguif.addon.manager.api.AddonsListComponentController;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;
import org.exbin.jaguif.addon.manager.api.AddonsManagementContext;
import org.exbin.jaguif.addon.manager.api.AddonsManagementCartController;
import org.exbin.jaguif.addon.manager.api.AddonsManagementLocalState;
import org.exbin.jaguif.addon.manager.api.UpdateAvailabilityContext;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.addon.manager.api.UpdateAvailabilityModules;

/**
 * Installed addons manager page.
 */
@ParametersAreNonnullByDefault
public class InstalledAddonsPage extends AbstractTabPagesComponent implements AddonManagerPage {

    public static final String PAGE_ID = "installedAddons";
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(InstalledAddonsPage.class);
    protected AddonsListComponent listComponent;
    protected List<ItemChangedListener> itemChangedListeners = new ArrayList<>();

    protected AddonsManagementContext addonsManagement;
    protected List<Integer> filterItems = null;

    public InstalledAddonsPage() {
        init();
    }

    private void init() {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        listComponent = addonManagerModule.createAddonsListComponent();
        listComponent.setController(new AddonsListComponentController() {

            @Override
            public int getItemsCount() {
                return InstalledAddonsPage.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return InstalledAddonsPage.this.getItem(index);
            }

            @Override
            public void addToCart(ItemRecord itemRecord, AddonOperationVariant variant) {
                ((AddonsManagementCartController) addonsManagement).addCartOperation(new AddonOperation(variant, itemRecord));
            }

            @Override
            public boolean isInCart(String moduleId, AddonOperationVariant variant) {
                return ((AddonsManagementCartController) addonsManagement).isInCart(moduleId, variant);
            }

            @Override
            public void requestModuleDetail(ItemRecord itemRecord) {
                // TODO addonManager.requestModuleDetail(itemRecord, addonsPanel);
            }
        });
        itemChangedListeners.add(listComponent::notifyItemChanged);
        putValue(KEY_NAME, resourceBundle.getString("page.name"));
        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(AddonsManagementContext.class, (instance) -> {
                    setAddonManager(instance);
                });
                registrar.registerChangeListener(UpdateAvailabilityContext.class, (instance) -> {
                    setAvailableModuleUpdates((UpdateAvailabilityModules) instance);
                });
            }
        });
    }

    private int getItemsCount() {
        if (addonsManagement == null) {
            return 0;
        }

        List<ItemRecord> installedAddons = ((AddonsManagementLocalState) addonsManagement).getInstalledAddons();
        if (filterItems != null) {
            return filterItems.size();
        }

        return installedAddons.size();
    }

    @Nonnull
    private ItemRecord getItem(int index) {
        List<ItemRecord> installedAddons = ((AddonsManagementLocalState) addonsManagement).getInstalledAddons();
        if (filterItems != null) {
            return installedAddons.get(filterItems.get(index));
        }

        return installedAddons.get(index);
    }

    public void setAddonManager(AddonsManagementContext addonsManagement) {
        this.addonsManagement = addonsManagement;
        notifyItemsChanged();
    }

    public void setAvailableModuleUpdates(UpdateAvailabilityModules availableModuleUpdates) {
        int itemsCount = getItemsCount();
        for (int i = 0; i < itemsCount; i++) {
            availableModuleUpdates.applyTo(getItem(i));
        }
        notifyItemsChanged();
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return listComponent.getComponent();
    }

    @Override
    public void notifyChanged() {
        notifyItemsChanged();
    }

    @Override
    public void setCatalogUrl(String addonCatalogUrl) {
        listComponent.setCatalogUrl(addonCatalogUrl);
    }

    @Override
    public void refreshContent() {
    }

    @Nonnull
    @Override
    public Runnable createFilterOperation(Object filter) {
        return () -> {
            // TODO
        };
    }

    @Nonnull
    @Override
    public Runnable createSearchOperation(String search) {
        return () -> {
            // TODO Implement as background thread
            List<ItemRecord> installedAddons = ((AddonsManagementLocalState) addonsManagement).getInstalledAddons();
            List<Integer> items = null;
            String searchCondition = search.trim().toLowerCase();
            if (!searchCondition.isEmpty()) {
                items = new ArrayList<>();
                for (int i = 0; i < installedAddons.size(); i++) {
                    ItemRecord record = installedAddons.get(i);
                    if (record.getName().toLowerCase().contains(searchCondition)) {
                        items.add(i);
                    }
                }
            }
            filterItems = items;
            notifyItemsChanged();
        };
    }

    private void notifyItemsChanged() {
        for (ItemChangedListener itemChangedListener : itemChangedListeners) {
            itemChangedListener.itemChanged();
        }
        listComponent.notifyItemsChanged();
    }

    public interface ItemChangedListener {

        void itemChanged();
    }

    public static class Contribution implements ComponentTabPagesContribution {

        @Nonnull
        @Override
        public TabPagesComponent createComponent() {
            return new InstalledAddonsPage();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return PAGE_ID;
        }
    };
}
