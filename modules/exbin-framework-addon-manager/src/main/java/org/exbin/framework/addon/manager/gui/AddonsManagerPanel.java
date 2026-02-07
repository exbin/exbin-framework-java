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
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.manager.api.AddonManagerPage;

/**
 * Addons manager panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsManagerPanel extends javax.swing.JPanel {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonsManagerPanel.class);
    protected final List<AddonManagerPage> managerTabs = new ArrayList<>();
    protected Controller controller;
    protected Component cartComponent;

    public AddonsManagerPanel() {
        initComponents();
        init();
    }

    private void init() {
        tabbedPane.addChangeListener((ChangeEvent e) -> {
            if (controller == null) {
                return;
            }
            controller.tabSwitched();
        });
        Document document = filterTextField.getDocument();
        document.addDocumentListener(new DocumentListener() {

            private String lastFilter = "";

            @Override
            public void insertUpdate(DocumentEvent de) {
                filterValueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                filterValueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                filterValueChanged();
            }

            public void filterValueChanged() {
                if (controller != null) {
                    String newFilter = filterTextField.getText();
                    if (!lastFilter.equals(newFilter)) {
                        lastFilter = newFilter;
                        controller.setFilter(newFilter);
                    }
                }
            }
        });
        document = searchTextField.getDocument();
        document.addDocumentListener(new DocumentListener() {

            private String lastSearch = "";

            @Override
            public void insertUpdate(DocumentEvent de) {
                searchValueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                searchValueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                searchValueChanged();
            }

            public void searchValueChanged() {
                if (controller != null) {
                    String newSearch = searchTextField.getText();
                    if (!lastSearch.equals(newSearch)) {
                        lastSearch = newSearch;
                        controller.setSearch(newSearch);
                    }
                }
            }
        });
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCartComponent(Component cartComponent) {
        this.cartComponent = cartComponent;
    }

    public void setCartItemsCount(int itemsCount) {
        cartButton.setText("(" + itemsCount + ")");
    }

    public void setCatalogUrl(String addonCatalogUrl) {
        for (AddonManagerPage managerTab : managerTabs) {
            managerTab.setCatalogUrl(addonCatalogUrl);
        }
    }

    public void addManagerPage(AddonManagerPage managerPage) {
        managerTabs.add(managerPage);
        tabbedPane.add(managerPage.getTitle(), managerPage.getComponent());
        tabbedPane.revalidate();
        tabbedPane.repaint();
    }

    @Nonnull
    public AddonManagerPage getActiveTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        return managerTabs.get(selectedIndex);
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
        headerPanel = new javax.swing.JPanel();
        cartButton = new javax.swing.JToggleButton();
        filterLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());
        add(tabbedPane, java.awt.BorderLayout.CENTER);

        cartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(resourceBundle.getString("cartButton.icon"))));
        cartButton.setText("(0)");
        cartButton.setToolTipText(resourceBundle.getString("cartButton.toolTipText")); // NOI18N
        cartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cartButtonActionPerformed(evt);
            }
        });

        filterLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(resourceBundle.getString("filterLabel.icon"))));
        filterLabel.setToolTipText(resourceBundle.getString("filterLabel.toolTipText")); // NOI18N

        filterTextField.setEditable(false);

        searchLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(resourceBundle.getString("searchLabel.icon"))));
        searchLabel.setToolTipText(resourceBundle.getString("searchLabel.toolTipText")); // NOI18N

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(searchLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchTextField))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterTextField)))
                .addGap(18, 18, 18)
                .addComponent(cartButton)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterLabel)
                            .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchLabel)
                            .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cartButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(headerPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void cartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartButtonActionPerformed
        if (cartButton.isSelected()) {
            remove(tabbedPane);
            add(cartComponent, BorderLayout.CENTER);
            controller.openCart();
        } else {
            remove(cartComponent);
            add(tabbedPane, BorderLayout.CENTER);
            controller.openCatalog();
        }
        revalidate();
        repaint();
    }//GEN-LAST:event_cartButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton cartButton;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        void openCatalog();

        void openCart();

        void tabSwitched();

        /**
         * Sets filter.
         *
         * @param filter filter
         */
        void setFilter(String filter);

        /**
         * Sets search condition.
         *
         * @param search search condition
         */
        void setSearch(String search);
    }
}
