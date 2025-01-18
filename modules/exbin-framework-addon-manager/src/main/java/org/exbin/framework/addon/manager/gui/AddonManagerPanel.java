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

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;
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
    private Controller controller;
    private PacksPanel packsPanel = new PacksPanel();
    private AddonsPanel addonsPanel = new AddonsPanel();
    private AddonsPanel installedPanel = new AddonsPanel();
    private Set<String> toInstall = new HashSet<>();
    private Set<String> toUpdate = new HashSet<>();
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
                controller.updateItem(item);
            }

            @Override
            public void removeItem(ItemRecord item) {
                controller.removeItem(item);
            }

            @Override
            public void changeSelection(ItemRecord item) {
                String moduleId = item.getId();
                if (toUpdate.contains(moduleId)) {
                    toUpdate.remove(moduleId);
                } else {
                    toUpdate.add(moduleId);
                }
                controller.updateSelectionChanged(getToUpdateCount());
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return toUpdate.contains(item.getId());
            }
        });
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        addonsPanel.setController(new AddonsPanel.Controller() {

            private List<AddonRecord> searchResult;

            @Override
            public int getItemsCount() {
                if (searchResult == null) {
                    searchForAddons();
                }
                return searchResult.size();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                if (searchResult == null) {
                    searchForAddons();
                }
                return searchResult.get(index);
            }

            @Override
            public void installItem(ItemRecord item) {
                controller.installItem(item);
            }

            @Override
            public void updateItem(ItemRecord item) {
                throw new IllegalStateException();
            }

            @Override
            public void changeSelection(ItemRecord item) {
                String moduleId = item.getId();
                if (toInstall.contains(moduleId)) {
                    toInstall.remove(moduleId);
                } else {
                    toInstall.add(moduleId);
                }
                controller.installSelectionChanged(getToInstallCount());
            }

            @Override
            public void removeItem(ItemRecord item) {
                throw new IllegalStateException();
            }

            private void searchForAddons() {
                try {
                    searchResult = addonCatalogService.searchForAddons("");
                    for (int i = searchResult.size() - 1; i >= 0; i--) {
                        if (controller.isInstalled(searchResult.get(i).getId())) {
                            searchResult.remove(i);
                        }
                    }
                } catch (AddonCatalogServiceException ex) {
                    Logger.getLogger(AddonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(AddonManagerPanel.this, "API request failed", "Addon Service Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public boolean isItemSelectedForOperation(ItemRecord item) {
                return toInstall.contains(item.getId());
            }
        });
    }

    @Nonnull
    public Set<String> getToInstall() {
        return toInstall;
    }

    public int getToInstallCount() {
        return toInstall.size();
    }

    @Nonnull
    public Set<String> getToUpdate() {
        return toUpdate;
    }

    public int getToUpdateCount() {
        return toUpdate.size();
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

        boolean isInstalled(String moduleId);

        void installItem(ItemRecord item);

        void updateItem(ItemRecord item);

        void removeItem(ItemRecord item);

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
