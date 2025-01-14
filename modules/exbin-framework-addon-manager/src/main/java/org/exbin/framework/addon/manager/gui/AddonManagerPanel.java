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
package org.exbin.framework.addon.manager.gui;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.impl.AddonCatalogServiceImpl;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Addon manager panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerPanel extends javax.swing.JPanel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManagerPanel.class);
    private AddonCatalogService addonCatalogService;
    private Controller controller;
    private PacksPanel packsPanel = new PacksPanel();
    private AddonsPanel addonsPanel = new AddonsPanel();
    private AddonsPanel installedPanel = new AddonsPanel();
    private int toInstall = 0;
    private int toUpdate = 0;
    private Tab activeTab = Tab.INSTALLED;

    public AddonManagerPanel() {
        initComponents();
        tabbedPane.addChangeListener((ChangeEvent e) -> {
            if (controller == null) {
                return;
            }

            switch (tabbedPane.getSelectedIndex()) {
                case 0:
                    activeTab = Tab.ADDONS;
                    break;
                case 1:
                    activeTab = Tab.INSTALLED;
                    break;
                default:
                    throw new AssertionError();
            }

            controller.tabSwitched(activeTab);
        });
        // TODO: Support for multimodule packs
//        tabbedPane.add(resourceBundle.getString("packsTab.title"), packsPanel);
        tabbedPane.add(resourceBundle.getString("addonsTab.title"), addonsPanel);
        tabbedPane.add(resourceBundle.getString("installedTab.title"), installedPanel);
        tabbedPane.revalidate();
        tabbedPane.repaint();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        List<ItemRecord> installedItems = controller.getInstalledItems();
        installedPanel.setController(new AddonsPanel.Controller() {
            @Override
            public int getItemsCount() {
                return installedItems.size();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return installedItems.get(index);
            }

            @Override
            public void installItem(ItemRecord item) {
                throw new IllegalStateException();
            }

            @Override
            public void updateItem(ItemRecord item) {
                toUpdate += item.isSelected() ? -1 : 1;
                item.setSelected(!item.isSelected());
                controller.updateSelectionChanged(toUpdate);
            }
        });
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
        addonsPanel.setController(new AddonsPanel.Controller() {

            private AddonCatalogService.AddonsListResult searchResult;

            @Override
            public int getItemsCount() {
                if (searchResult == null) {
                    searchForAddons();
                }
                return searchResult.itemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                if (searchResult == null) {
                    searchForAddons();
                }
                return searchResult.getLazyItem(index);
            }

            @Override
            public void installItem(ItemRecord item) {
                toInstall += item.isSelected() ? -1 : 1;
                item.setSelected(!item.isSelected());
                controller.installSelectionChanged(toInstall);
            }

            private void searchForAddons() {
                searchResult = addonCatalogService.searchForAddons("");
                if (searchResult instanceof AddonCatalogServiceImpl.ServiceFailureResult) {
                    // Exception exception = ((AddonCatalogServiceImpl.ServiceFailureResult) searchResult).getException();
                    JOptionPane.showMessageDialog(AddonManagerPanel.this, "API request failed", "Addon Service Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void updateItem(ItemRecord item) {
                throw new IllegalStateException();
            }
        });
    }

    public int getToInstall() {
        return toInstall;
    }

    public int getToUpdate() {
        return toUpdate;
    }

    @Nonnull
    public Tab getActiveTab() {
        return activeTab;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());
        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new AddonManagerPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    public interface Controller {

        @Nonnull
        List<ItemRecord> getInstalledItems();

        void installSelectionChanged(int toInstall);

        void updateSelectionChanged(int toUpdate);

        void tabSwitched(Tab tab);
    }

    public enum Tab {
        PACKS,
        ADDONS,
        INSTALLED
    }
}
