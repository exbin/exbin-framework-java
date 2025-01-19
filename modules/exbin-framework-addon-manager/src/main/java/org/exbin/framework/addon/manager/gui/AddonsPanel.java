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

import java.awt.BorderLayout;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Addons list with details panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsPanel extends javax.swing.JPanel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonsPanel.class);
    private Controller controller;
    private FilterListPanel filterListPanel = new FilterListPanel();
    private ItemRecord activeRecord;
    private AddonDetailsPanel addonDetailsPanel = new AddonDetailsPanel();

    public AddonsPanel() {
        initComponents();
        splitPane.setLeftComponent(filterListPanel);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        filterListPanel.setController(new FilterListPanel.Controller() {
            @Override
            public int getItemsCount() {
                return controller.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return controller.getItem(index);
            }

            @Override
            public void notifyItemSelected(@Nullable ItemRecord itemRecord) {
                if (activeRecord != itemRecord) {
                    if (activeRecord == null) {
                        infoPanel.remove(noItemSelectedLabel);
                        addonDetailsPanel.setRecord(itemRecord, controller.isItemSelectedForOperation(itemRecord));
                        infoPanel.add(addonDetailsPanel, BorderLayout.CENTER);
                        infoPanel.revalidate();
                        infoPanel.repaint();
                    } else if (itemRecord == null) {
                        infoPanel.remove(addonDetailsPanel);
                        infoPanel.add(noItemSelectedLabel, BorderLayout.CENTER);
                        infoPanel.revalidate();
                        infoPanel.repaint();
                    } else {
                        addonDetailsPanel.setRecord(itemRecord, controller.isItemSelectedForOperation(itemRecord));
                    }
                    activeRecord = itemRecord;
                }
            }
        });
        controller.addItemChangedListener(new ItemChangedListener() {
            @Override
            public void itemChanged() {
                if (activeRecord != null) {
                    addonDetailsPanel.setRecord(activeRecord, controller.isItemSelectedForOperation(activeRecord));
                }
            }
        });
        addonDetailsPanel.setController(new AddonDetailsPanel.Controller() {
            @Override
            public void changeEnablement() {
                // TODO
            }

            @Override
            public boolean isInstalled(String moduleId) {
                return controller.isInstalled(moduleId);
            }

            @Override
            public boolean isRemoved(String moduleId) {
                return controller.isRemoved(moduleId);
            }

            @Override
            public void performInstall() {
                controller.install(activeRecord);
            }

            @Override
            public void performUpdate() {
                controller.update(activeRecord);
            }

            @Override
            public void performRemove() {
                controller.remove(activeRecord);
            }

            @Override
            public void changeSelection() {
                controller.changeSelection(activeRecord);
            }
        });
        controller.addUpdateAvailabilityListener((AvailableModuleUpdates availableModuleUpdates) -> {
            filterListPanel.notifyItemsChanged();

            for (int i = 0; i < controller.getItemsCount(); i++) {
                availableModuleUpdates.applyTo(controller.getItem(i));
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        infoPanel = new javax.swing.JPanel();
        noItemSelectedLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(250);

        infoPanel.setLayout(new java.awt.BorderLayout());

        noItemSelectedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noItemSelectedLabel.setText(resourceBundle.getString("noItemSelectedLabel.text")); // NOI18N
        noItemSelectedLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        infoPanel.add(noItemSelectedLabel, java.awt.BorderLayout.CENTER);

        splitPane.setRightComponent(infoPanel);

        add(splitPane, java.awt.BorderLayout.CENTER);
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
            WindowUtils.invokeWindow(new AddonsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel noItemSelectedLabel;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        int getItemsCount();

        @Nonnull
        ItemRecord getItem(int index);

        boolean isInstalled(String moduleId);

        boolean isRemoved(String moduleId);

        void install(ItemRecord item);

        void update(ItemRecord item);

        void remove(ItemRecord item);

        void changeSelection(ItemRecord item);

        boolean isItemSelectedForOperation(ItemRecord item);

        void addItemChangedListener(ItemChangedListener listener);

        void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener);
    }

    public interface ItemChangedListener {

        void itemChanged();
    }
}
