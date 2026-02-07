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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.gui.AddonsPanel;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.api.AddonManagerPage;
import org.exbin.framework.addon.manager.operation.CatalogSearchOperation;

/**
 * Addons manager page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsCatalogPage implements AddonManagerPage {

    protected AddonsPanel addonsPanel = new AddonsPanel();
    protected List<ItemChangedListener> itemChangedListeners = new ArrayList<>();
    protected AddonCatalogService addonCatalogService;

    protected AddonManager addonManager;
    protected List<AddonRecord> addonItems;

    public AddonsCatalogPage() {
        init();
    }

    private void init() {
        addonsPanel.setController(new AddonsPanel.Controller() {

            @Override
            public int getItemsCount() {
                return AddonsCatalogPage.this.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return AddonsCatalogPage.this.getItem(index);
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
                return AddonsCatalogPage.this.getModuleDetails(itemRecord);
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

    @Override
    public void setCatalogUrl(String addonCatalogUrl) {
        addonsPanel.setCatalogUrl(addonCatalogUrl);
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
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
        return new CatalogSearchOperation(addonCatalogService, addonManager, search, this::setAddonItems);
//        addonsPanel.notifyItemsChanged();
//        ResourceBundle resourceBundle = addonManager.getResourceBundle();
//        JOptionPane.showMessageDialog(addonsPanel, resourceBundle.getString("addonServiceApiError.message"), resourceBundle.getString("addonServiceApiError.title"), JOptionPane.ERROR_MESSAGE);
    }

    public void setAddonItems(List<AddonRecord> addonItems) {
        this.addonItems = addonItems;
        notifyItemsChanged();
    }

    private int getItemsCount() {
        if (addonItems == null) {
            return 0;
        }

        return addonItems.size();
    }

    @Nonnull
    private ItemRecord getItem(int index) {
        return addonItems.get(index);
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

    @Nonnull
    public String getModuleDetails(ItemRecord itemRecord) {
        if (itemRecord.isAddon()) {
            try {
                return addonCatalogService.getModuleDetails(itemRecord.getId());
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "";
    }

    public interface ItemChangedListener {

        void itemChanged();
    }
}
