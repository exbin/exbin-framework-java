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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;

/**
 * Addons manager tab.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsManagerTab implements AddonManagerTab {

    private AddonsPanel addonsPanel = new AddonsPanel();
    private Set<String> toInstall = new HashSet<>();
    private List<AddonsPanel.ItemChangedListener> itemChangedListeners = new ArrayList<>();

    private AddonManager addonManager;
    private List<AddonRecord> searchResult;

    public AddonsManagerTab() {
        init();
    }

    private void init() {
        addonsPanel.setController(new AddonsPanel.Controller() {

            @Override
            public void setFilter(String filter, Runnable finished) {
                // TODO
                finished.run();
            }

            @Override
            public int getItemsCount() {
                return AddonsManagerTab.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return AddonsManagerTab.this.getItem(index);
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
                addonManager.installItem(item, addonsPanel, () -> {
                    notifyItemsChanged();
                });
            }

            @Override
            public void update(ItemRecord item) {
                addonManager.updateItem(item, addonsPanel, () -> {
                    notifyItemsChanged();
                });
            }

            @Override
            public void remove(ItemRecord item) {
                addonManager.removeItem(item, addonsPanel, () -> {
                    notifyItemsChanged();
                });
            }

            @Override
            public void changeSelection(ItemRecord item) {
                String moduleId = item.getId();
                if (toInstall.contains(moduleId)) {
                    toInstall.remove(moduleId);
                } else {
                    toInstall.add(moduleId);
                }
                // TODO installSelectionChanged(addonsManagerTab.getToInstallCount());
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return toInstall.contains(item.getId());
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

    @Nonnull
    @Override
    public String getTitle() {
        return addonsPanel.getResourceBundle().getString("addonsTab.title");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        return addonsPanel;
    }

    private int getItemsCount() {
        if (searchResult == null) {
            try {
                searchResult = addonManager.searchForAddons();
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonsManagerTab.class.getName()).log(Level.SEVERE, null, ex);
                ResourceBundle resourceBundle = addonsPanel.getResourceBundle();
                JOptionPane.showMessageDialog(addonsPanel, resourceBundle.getString("addonServiceApiError.message"), resourceBundle.getString("addonServiceApiError.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
        return searchResult.size();
    }

    @Nonnull
    private ItemRecord getItem(int index) {
        return searchResult.get(index);
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

    private void notifyItemsChanged() {
        for (AddonsPanel.ItemChangedListener itemChangedListener : itemChangedListeners) {
            itemChangedListener.itemChanged();
        }
        addonsPanel.notifyItemsChanged();
    }

    public void installAddons() {
        addonManager.installAddons(toInstall, addonsPanel, () -> {
            notifyItemsChanged();
        });
    }

    @Nonnull
    public Set<String> getToInstall() {
        return toInstall;
    }

    public int getToInstallCount() {
        return toInstall.size();
    }
}
