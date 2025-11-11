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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import org.exbin.framework.App;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.DependenciesTableModel;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Addon details panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonDetailsPanel extends javax.swing.JPanel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonDetailsPanel.class);
    private Controller controller;
    private MouseListener providerLinkListener;
    private final DependenciesTableModel dependenciesTableModel = new DependenciesTableModel();
    private boolean recordChangeInProgress = false;
    private String providerLink = null;
    // TODO private final Thread detailsThread

    public AddonDetailsPanel() {
        initComponents();
        init();
    }

    private void init() {
        dependenciesTable.setModel(dependenciesTableModel);
        overviewTextPane.addHyperlinkListener((HyperlinkEvent event) -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                DesktopUtils.openDesktopURL(event.getURL().toExternalForm());
            }
        });
        try {
            AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
            String addonServiceUrl = addonManagerModule.getAddonServiceUrl();
            HTMLDocument htmlDocument = new HTMLDocument();
            htmlDocument.setBase(new URI(addonServiceUrl).toURL());
            overviewTextPane.setDocument(htmlDocument);
        } catch (MalformedURLException | URISyntaxException ex) {
            Logger.getLogger(AddonDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        providerLinkListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && !evt.isPopupTrigger()) {
                    DesktopUtils.openDesktopURL(providerLink);
                }
            }
        };
        providerLabel.addMouseListener(providerLinkListener);
        providerLabel.setComponentPopupMenu(new JPopupMenu() {

            @Override
            public void show(Component invoker, int x, int y) {
                MenuPopupModuleApi actionPopupModule = App.getModule(MenuPopupModuleApi.class);
                actionPopupModule.createLinkPopupMenu(providerLink).show(invoker, x, y);
            }
        });
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller control) {
        this.controller = control;
    }

    public void setRecord(ItemRecord itemRecord, boolean selectedForOperation) {
        recordChangeInProgress = true;
        addonNameLabel.setText(itemRecord.getName());
        versionLabel.setText(itemRecord.getVersion());
        String provider = itemRecord.getProvider().orElse("");
        String providerHomepage = itemRecord.getHomepage().orElse(null);

        if (providerLinkListener != null) {
            providerLabel.removeMouseListener(providerLinkListener);
        }
        if (providerHomepage != null) {
            provider = "<html><body><a href=\"" + providerHomepage + "\">" + (provider.isEmpty() ? resourceBundle.getString("record.provider") : provider) + "</a></body></html>";
            providerLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            providerLabel.setToolTipText(providerHomepage);
            providerLink = providerHomepage;
        } else {
            providerLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            providerLabel.setToolTipText(null);
            providerLink = null;
        }
        providerLabel.setText(provider);
        String description = itemRecord.getDescription().orElse("");
        String details = controller.getModuleDetails(itemRecord);
        if (!details.isEmpty()) {
            details = "<hr/>" + details;
        }
        overviewTextPane.setText("<html><body><p>" + description + "<br/>id: " + itemRecord.getId() + "</p>" + details + "</body></html>");
        if (itemRecord instanceof AddonRecord) {
            dependenciesTableModel.setDependencies(((AddonRecord) itemRecord).getDependencies());
        } else {
            dependenciesTableModel.setDependencies(null);
        }
        controlPanel.removeAll();
        if (itemRecord.isInstalled()) {
            boolean alreadyRemoved = controller.isAlreadyRemoved(itemRecord.getId());
            boolean alreadyInstalled = controller.isAlreadyInstalled(itemRecord.getId());
            removeButton.setEnabled(itemRecord.isAddon() && !alreadyRemoved);
            controlPanel.add(removeButton);
            enablementButton.setText(resourceBundle.getString(itemRecord.isEnabled() ? "disableButton.text" : "enableButton.text"));
            controlPanel.add(enablementButton);
            updateCheckBox.setSelected(selectedForOperation);
            updateCheckBox.setEnabled(itemRecord.isUpdateAvailable() && !alreadyInstalled);
            controlPanel.add(updateCheckBox);
            updateButton.setEnabled(itemRecord.isUpdateAvailable() && !alreadyInstalled);
            controlPanel.add(updateButton);
        } else {
            boolean isInstalled = controller.isAlreadyInstalled(itemRecord.getId());
            installCheckBox.setSelected(selectedForOperation);
            installCheckBox.setEnabled(!isInstalled);
            installButton.setEnabled(!isInstalled);
            controlPanel.add(installCheckBox);
            controlPanel.add(installButton);
        }
        controlPanel.revalidate();
        controlPanel.repaint();
        recordChangeInProgress = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        installButton = new javax.swing.JButton();
        installCheckBox = new javax.swing.JCheckBox();
        updateButton = new javax.swing.JButton();
        updateCheckBox = new javax.swing.JCheckBox();
        enablementButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        addonNameLabel = new javax.swing.JLabel();
        providerLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        overviewScrollPane = new javax.swing.JScrollPane();
        overviewTextPane = new javax.swing.JTextPane();
        dependenciesScrollPane = new javax.swing.JScrollPane();
        dependenciesTable = new javax.swing.JTable();

        installButton.setText(resourceBundle.getString("installButton.text")); // NOI18N
        installButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installButtonActionPerformed(evt);
            }
        });

        installCheckBox.setToolTipText(resourceBundle.getString("installCheckBox.toolTipText")); // NOI18N
        installCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 0));
        installCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                installCheckBoxItemStateChanged(evt);
            }
        });

        updateButton.setText(resourceBundle.getString("updateButton.text")); // NOI18N
        updateButton.setEnabled(false);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        updateCheckBox.setToolTipText(resourceBundle.getString("updateCheckBox.toolTipText")); // NOI18N
        updateCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 0));
        updateCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateCheckBoxItemStateChanged(evt);
            }
        });

        enablementButton.setText(resourceBundle.getString("enableButton.text")); // NOI18N
        enablementButton.setEnabled(false);
        enablementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enablementButtonActionPerformed(evt);
            }
        });

        removeButton.setText(resourceBundle.getString("removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        add(controlPanel, java.awt.BorderLayout.PAGE_END);

        addonNameLabel.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        addonNameLabel.setText("Addon Name");

        providerLabel.setFont(providerLabel.getFont().deriveFont((providerLabel.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD));
        providerLabel.setText("Addon Provider");

        versionLabel.setText("Version");

        overviewTextPane.setEditable(false);
        overviewTextPane.setContentType("text/html"); // NOI18N
        overviewScrollPane.setViewportView(overviewTextPane);

        tabbedPane.addTab(resourceBundle.getString("overviewTab.title"), overviewScrollPane); // NOI18N

        dependenciesScrollPane.setViewportView(dependenciesTable);

        tabbedPane.addTab(resourceBundle.getString("dependenciesTab.title"), dependenciesScrollPane); // NOI18N

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(addonNameLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(providerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(versionLabel)))
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addonNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(providerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(infoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void enablementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enablementButtonActionPerformed
        controller.changeEnablement();
    }//GEN-LAST:event_enablementButtonActionPerformed

    private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
        controller.performInstall();
    }//GEN-LAST:event_installButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        controller.performUpdate();
    }//GEN-LAST:event_updateButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        controller.performRemove();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void installCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_installCheckBoxItemStateChanged
        if (!recordChangeInProgress) {
            controller.changeSelection();
        }
    }//GEN-LAST:event_installCheckBoxItemStateChanged

    private void updateCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_updateCheckBoxItemStateChanged
        if (!recordChangeInProgress) {
            controller.changeSelection();
        }
    }//GEN-LAST:event_updateCheckBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addonNameLabel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JScrollPane dependenciesScrollPane;
    private javax.swing.JTable dependenciesTable;
    private javax.swing.JButton enablementButton;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JButton installButton;
    private javax.swing.JCheckBox installCheckBox;
    private javax.swing.JScrollPane overviewScrollPane;
    private javax.swing.JTextPane overviewTextPane;
    private javax.swing.JLabel providerLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JButton updateButton;
    private javax.swing.JCheckBox updateCheckBox;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        boolean isAlreadyInstalled(String moduleId);

        boolean isAlreadyRemoved(String moduleId);

        void changeEnablement();

        void performInstall();

        void performUpdate();

        void performRemove();

        void changeSelection();

        @Nonnull
        String getModuleDetails(ItemRecord itemRecord);
    }
}
