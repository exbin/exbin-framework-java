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
import org.exbin.framework.addon.manager.model.FilterListModel;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Addons list with details panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsPanel extends javax.swing.JPanel {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonsPanel.class);
    protected FilterListPanel filterListPanel = new FilterListPanel();
    protected ItemRecord activeRecord;
    protected AddonDetailsPanel addonDetailsPanel = new AddonDetailsPanel();
    protected Controller controller;

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
            public void setFilter(String filter, Runnable finished) {
                controller.setFilter(filter, finished);
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
        filterListPanel.setRecordProvider(new FilterListModel.RecordsProvider() {
            @Override
            public int getItemsCount() {
                return controller.getItemsCount();
            }

            @Nonnull
            @Override
            public ItemRecord getItem(int index) {
                return controller.getItem(index);
            }
        });
        addonDetailsPanel.setController(new AddonDetailsPanel.Controller() {
            @Override
            public void changeEnablement() {
                // TODO
            }

            @Override
            public boolean isAlreadyInstalled(String moduleId) {
                return controller.isAlreadyInstalled(moduleId);
            }

            @Override
            public boolean isAlreadyRemoved(String moduleId) {
                return controller.isAlreadyRemoved(moduleId);
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

            @Nonnull
            @Override
            public String getModuleDetails(ItemRecord itemRecord) {
                return controller.getModuleDetails(itemRecord);
            }
        });
    }

    public void notifyItemChanged() {
        if (activeRecord != null) {
            addonDetailsPanel.setRecord(activeRecord, controller.isItemSelectedForOperation(activeRecord));
        }
    }

    public void notifyItemsChanged() {
        filterListPanel.notifyItemsChanged();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel noItemSelectedLabel;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        void setFilter(String filter, Runnable finished);

        int getItemsCount();

        @Nonnull
        ItemRecord getItem(int index);

        boolean isAlreadyInstalled(String moduleId);

        boolean isAlreadyRemoved(String moduleId);

        void install(ItemRecord item);

        void update(ItemRecord item);

        void remove(ItemRecord item);

        void changeSelection(ItemRecord item);

        boolean isItemSelectedForOperation(ItemRecord item);

        @Nonnull
        String getModuleDetails(ItemRecord itemRecord);
    }
}
