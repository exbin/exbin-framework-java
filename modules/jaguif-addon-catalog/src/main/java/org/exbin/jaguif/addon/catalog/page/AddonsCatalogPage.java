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
package org.exbin.jaguif.addon.catalog.page;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.addon.catalog.operation.CatalogSearchOperation;
import org.exbin.jaguif.addon.manager.AddonOperation;
import org.exbin.jaguif.addon.manager.AddonOperationVariant;
import org.exbin.jaguif.addon.manager.api.AddonCatalogService;
import org.exbin.jaguif.addon.manager.gui.AddonsPanel;
import org.exbin.jaguif.addon.manager.api.AddonRecord;
import org.exbin.jaguif.addon.manager.api.ItemRecord;
import org.exbin.jaguif.addon.manager.api.AddonManagerPage;
import org.exbin.jaguif.addon.manager.api.AddonsManagementCartController;
import org.exbin.jaguif.addon.manager.api.AddonsManagementContext;
import org.exbin.jaguif.addon.manager.api.UpdateAvailabilityContext;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;
import org.exbin.jaguif.addon.manager.api.UpdateAvailabilityModules;

/**
 * Addons manager page.
 */
@ParametersAreNonnullByDefault
public class AddonsCatalogPage extends AbstractTabPagesComponent implements AddonManagerPage {

    public static final String PAGE_ID = "addonsCatalog";
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonsCatalogPage.class);
    protected AddonsPanel addonsPanel = new AddonsPanel();
    protected List<ItemChangedListener> itemChangedListeners = new ArrayList<>();
    protected AddonCatalogService addonCatalogService;

    protected AddonsManagementContext addonsManagement;
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
        itemChangedListeners.add((ItemChangedListener) addonsPanel::notifyItemChanged);
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

    @Nonnull
    @Override
    public JComponent getComponent() {
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
        return new CatalogSearchOperation(addonCatalogService, null, null, search, this::setAddonItems); // addonManager
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

    public static class Contribution implements ComponentTabPagesContribution {

        @Nonnull
        @Override
        public TabPagesComponent createComponent() {
            return new AddonsCatalogPage();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return PAGE_ID;
        }
    };
}
