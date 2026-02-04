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
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.api.AddonManagerPage;

/**
 * Installed manager page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsInstalledPage implements AddonManagerPage {

    protected AddonsPanel addonsPanel = new AddonsPanel();
    protected List<ItemChangedListener> itemChangedListeners = new ArrayList<>();

    protected AddonManager addonManager;
    protected List<Integer> filterItems = null;

    public AddonsInstalledPage() {
        init();
    }

    private void init() {
        addonsPanel.setController(new AddonsPanel.Controller() {

            @Override
            public int getItemsCount() {
                return AddonsInstalledPage.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return AddonsInstalledPage.this.getItem(index);
            }

            @Override
            public void addToCart(ItemRecord itemRecord, AddonOperationVariant variant) {
                addonManager.addCartOperation(new AddonOperation(variant, itemRecord));
            }

            @Override
            public boolean isInCart(String moduleId, AddonOperationVariant variant) {
                return addonManager.isInCart(moduleId, variant);
            }

            @Nonnull
            @Override
            public String getModuleDetails(ItemRecord itemRecord) {
                // TODO
                return "";
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
            List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
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
        };
    }

    private void notifyItemsChanged() {
        for (ItemChangedListener itemChangedListener : itemChangedListeners) {
            itemChangedListener.itemChanged();
        }
        addonsPanel.notifyItemsChanged();
    }

    public interface ItemChangedListener {

        void itemChanged();
    }
}
