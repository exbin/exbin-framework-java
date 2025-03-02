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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.model.ItemRecord;

/**
 * Installed manager tab.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InstalledManagerTab implements AddonManagerTab {

    private AddonsPanel installedPanel = new AddonsPanel();
    private Set<String> toUpdate = new HashSet<>();
    private List<AddonsPanel.ItemChangedListener> itemChangedListeners = new ArrayList<>();

    private AddonManager addonManager;
    private List<Integer> filterItems = null;

    public InstalledManagerTab() {
        init();
    }

    private void init() {
        installedPanel.setController(new AddonsPanel.Controller() {

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
                return InstalledManagerTab.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return InstalledManagerTab.this.getItem(index);
            }

            @Override
            public boolean isAlreadyInstalled(String moduleId) {
                return addonManager.isAlreadyInstalled(moduleId);
            }

            @Override
            public boolean isAlreadyRemoved(String moduleId) {
                return addonManager.isAlreadyRemoved(moduleId);
            }

            @Override
            public void install(ItemRecord item) {
                throw new IllegalStateException("Already installed");
            }

            @Override
            public void update(ItemRecord item) {
                addonManager.updateItem(item, installedPanel, () -> {
                    notifyItemsChanged();
                });
            }

            @Override
            public void remove(ItemRecord item) {
                addonManager.removeItem(item, installedPanel, () -> {
                    notifyItemsChanged();
                });
            }

            @Override
            public void changeSelection(ItemRecord item) {
                String moduleId = item.getId();
                if (toUpdate.contains(moduleId)) {
                    toUpdate.remove(moduleId);
                } else {
                    toUpdate.add(moduleId);
                }
                // TODO updateSelectionChanged(installedManagerTab.getToUpdateCount());
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return toUpdate.contains(item.getId());
            }

            @Override
            public void addItemChangedListener(AddonsPanel.ItemChangedListener listener) {
                itemChangedListeners.add(listener);
            }

            @Nonnull
            @Override
            public String getModuleDetails(ItemRecord itemRecord) {
                return addonManager.getModuleDetails(itemRecord);
            }
        });
    }

    private int getItemsCount() {
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
        return installedPanel.getResourceBundle().getString("installedTab.title");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        return installedPanel;
    }

    private void notifyItemsChanged() {
        for (AddonsPanel.ItemChangedListener itemChangedListener : itemChangedListeners) {
            itemChangedListener.itemChanged();
        }
        installedPanel.notifyItemsChanged();
    }

    public void updateAddons() {
        addonManager.updateAddons(toUpdate, installedPanel, () -> {
            notifyItemsChanged();
        });
    }

    @Nonnull
    public Set<String> getToUpdate() {
        return toUpdate;
    }

    public int getToUpdateCount() {
        return toUpdate.size();
    }
}
