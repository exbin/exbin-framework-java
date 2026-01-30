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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;

/**
 * Addons manager tab.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsCatalogTab implements AddonManagerTab {

    private AddonsPanel addonsPanel = new AddonsPanel();
    private List<ItemChangedListener> itemChangedListeners = new ArrayList<>();

    private AddonManager addonManager;
    private List<AddonRecord> searchResult;

    public AddonsCatalogTab() {
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
                return AddonsCatalogTab.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return AddonsCatalogTab.this.getItem(index);
            }

            @Override
            public void addToCart(ItemRecord itemRecord, AddonOperationVariant variant) {
                addonManager.addCartOperation(new AddonOperation(variant, itemRecord));
                notifyItemsChanged();
            }

            @Override
            public boolean isInCart(String moduleId, AddonOperationVariant variant) {
                return addonManager.isInCart(moduleId, variant);
            }

            /* @Override
            public boolean isAlreadyInstalled(String moduleId) {
                return addonManager.isAlreadyInstalled(moduleId);
            }

            @Override
            public boolean isAlreadyRemoved(String moduleId) {
                return addonManager.isAlreadyRemoved(moduleId);
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return false; // toInstall.contains(item.getId());
            } */
            @Nonnull
            @Override
            public String getModuleDetails(ItemRecord itemRecord) {
                return addonManager.getModuleDetails(itemRecord);
            }
        });
        itemChangedListeners.add((ItemChangedListener) addonsPanel::notifyItemChanged);
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
        if (addonManager == null) {
            return 0;
        }

        if (searchResult == null) {
            try {
                searchResult = addonManager.searchForAddons();
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonsCatalogTab.class.getName()).log(Level.SEVERE, null, ex);
                ResourceBundle resourceBundle = addonManager.getResourceBundle();
                JOptionPane.showMessageDialog(addonsPanel, resourceBundle.getString("addonServiceApiError.message"), resourceBundle.getString("addonServiceApiError.title"), JOptionPane.ERROR_MESSAGE);
                return 0;
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

    @Override
    public void notifyChanged() {
        notifyItemsChanged();
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
