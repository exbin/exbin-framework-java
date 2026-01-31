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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.api.ItemRecord;

/**
 * Installed manager tab.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsInstalledTab implements AddonManagerTab {

    private AddonsPanel addonsPanel = new AddonsPanel();
    private List<ItemChangedListener> itemChangedListeners = new ArrayList<>();

    private AddonManager addonManager;
    private List<Integer> filterItems = null;

    public AddonsInstalledTab() {
        init();
    }

    private void init() {
        addonsPanel.setController(new AddonsPanel.Controller() {

            @Override
            public void setFilter(String filter, Runnable finished) {
                // TODO Implement as background thread
                List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
                List<Integer> items = null;
                filter = filter.trim().toLowerCase();
                if (!filter.isEmpty()) {
                    items = new ArrayList<>();
                    for (int i = 0; i < installedAddons.size(); i++) {
                        ItemRecord record = installedAddons.get(i);
                        if (record.getName().toLowerCase().contains(filter)) {
                            items.add(i);
                        }
                    }
                }
                filterItems = items;
                finished.run();
            }

            @Override
            public int getItemsCount() {
                return AddonsInstalledTab.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return AddonsInstalledTab.this.getItem(index);
            }

            @Override
            public void addToCart(ItemRecord itemRecord, AddonOperationVariant variant) {
                addonManager.addCartOperation(new AddonOperation(variant, itemRecord));
            }

            @Override
            public boolean isInCart(String moduleId, AddonOperationVariant variant) {
                return addonManager.isInCart(moduleId, variant);
            }

            /* @Override
            public boolean isModuleInstalled(String moduleId) {
                return addonManager.isModuleInstalled(moduleId);
            }

            @Override
            public boolean isModuleRemoved(String moduleId) {
                return addonManager.isModuleRemoved(moduleId);
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return false; // toUpdate.contains(item.getId());
            } */
            @Nonnull
            @Override
            public String getModuleDetails(ItemRecord itemRecord) {
                return addonManager.getModuleDetails(itemRecord);
            }
        });
        itemChangedListeners.add(addonsPanel::notifyItemChanged);
    }

    private int getItemsCount() {
        if (addonManager == null) {
            return 0;
        }

        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        if (filterItems != null) {
            return filterItems.size();
        }

        return installedAddons.size();
    }

    @Nonnull
    private ItemRecord getItem(int index) {
        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        if (filterItems != null) {
            return installedAddons.get(filterItems.get(index));
        }

        return installedAddons.get(index);
    }

    public void setAddonManager(AddonManager addonManager) {
        this.addonManager = addonManager;
        addonManager.addUpdateAvailabilityListener((AvailableModuleUpdates availableModuleUpdates) -> {
            int itemsCount = getItemsCount();
            for (int i = 0; i < itemsCount; i++) {
                availableModuleUpdates.applyTo(getItem(i));
            }
            notifyItemsChanged();
        });
        notifyItemsChanged();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return addonsPanel.getResourceBundle().getString("installedTab.title");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        return addonsPanel;
    }

    @Override
    public void notifyChanged() {
        notifyItemsChanged();
    }

    @Override
    public void setCatalogUrl(String addonCatalogUrl) {
        addonsPanel.setCatalogUrl(addonCatalogUrl);
    }

    private void notifyItemsChanged() {
        for (ItemChangedListener itemChangedListener : itemChangedListeners) {
            itemChangedListener.itemChanged();
        }
        addonsPanel.notifyItemsChanged();
    }

    public void updateAddons() {
        // TODO addonManager.updateAddons(toUpdate, installedPanel);
        notifyItemsChanged();
    }

    public interface ItemChangedListener {

        void itemChanged();
    }
}
