/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.service.catalog.gui;

import java.util.Date;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JList;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.xbup.catalog.XBECatalog;
import org.exbin.xbup.catalog.entity.service.XBENodeService;
import org.exbin.xbup.catalog.entity.service.XBERootService;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.service.XBCItemService;
import org.exbin.xbup.core.catalog.base.service.XBCNodeService;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCRootService;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;

/**
 * Panel for catalog status.
 *
 * @version 0.2.1 2020/08/18
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogStatusPanel extends javax.swing.JPanel {

    private static final String UNKNOWN = "unknown";
    private XBACatalog catalog;
    private final CatalogExtensionsListModel extModel;

    public CatalogStatusPanel() {
        extModel = new CatalogExtensionsListModel();
        initComponents();

        updateCatalog();
        // Patch for unchecked call to setModel(ListModel<E>) as a member of the raw type JList
        JList<String> newList = new JList<>();
        newList.setModel(extModel);
        extensionsList = newList;
        extensionsScrollPane.setViewportView(extensionsList);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        catalogStatusBorderPanel = new javax.swing.JPanel();
        catalogStatusPanel = new javax.swing.JPanel();
        itemsCountLabel = new javax.swing.JLabel();
        itemsCountTextField = new javax.swing.JTextField();
        nodesCountLabel = new javax.swing.JLabel();
        nodesCountTextField = new javax.swing.JTextField();
        specsCountLabel = new javax.swing.JLabel();
        specsCountTextField = new javax.swing.JTextField();
        formatsCountLabel = new javax.swing.JLabel();
        formatsCountTextField = new javax.swing.JTextField();
        groupsCountLabel = new javax.swing.JLabel();
        groupsCountTextField = new javax.swing.JTextField();
        blocksCountLabel = new javax.swing.JLabel();
        blocksCountTextField = new javax.swing.JTextField();
        defsCountLabel = new javax.swing.JLabel();
        defsCountTextField = new javax.swing.JTextField();
        revsCountLabel = new javax.swing.JLabel();
        revsCountTextField = new javax.swing.JTextField();
        lastUpdateLabel = new javax.swing.JLabel();
        lastUpdatePanel = new javax.swing.JPanel();
        lastUpdateTextField = new javax.swing.JTextField();
        lastUpdateNowButton = new javax.swing.JButton();
        extensionsBorderPanel = new javax.swing.JPanel();
        extensionsScrollPane = new javax.swing.JScrollPane();
        extensionsList = new JList<>();

        catalogStatusBorderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Catalog Status"));

        catalogStatusPanel.setLayout(new java.awt.GridLayout(9, 2));

        itemsCountLabel.setText("Count of Items");
        catalogStatusPanel.add(itemsCountLabel);

        itemsCountTextField.setEditable(false);
        itemsCountTextField.setText(UNKNOWN);
        itemsCountTextField.setBorder(null);
        catalogStatusPanel.add(itemsCountTextField);

        nodesCountLabel.setText("Nodes");
        catalogStatusPanel.add(nodesCountLabel);

        nodesCountTextField.setEditable(false);
        nodesCountTextField.setText(UNKNOWN);
        nodesCountTextField.setBorder(null);
        catalogStatusPanel.add(nodesCountTextField);

        specsCountLabel.setText("Specifications");
        catalogStatusPanel.add(specsCountLabel);

        specsCountTextField.setEditable(false);
        specsCountTextField.setText(UNKNOWN);
        specsCountTextField.setBorder(null);
        catalogStatusPanel.add(specsCountTextField);

        formatsCountLabel.setText("Formats");
        catalogStatusPanel.add(formatsCountLabel);

        formatsCountTextField.setEditable(false);
        formatsCountTextField.setText(UNKNOWN);
        formatsCountTextField.setBorder(null);
        catalogStatusPanel.add(formatsCountTextField);

        groupsCountLabel.setText("Groups");
        catalogStatusPanel.add(groupsCountLabel);

        groupsCountTextField.setEditable(false);
        groupsCountTextField.setText(UNKNOWN);
        groupsCountTextField.setBorder(null);
        catalogStatusPanel.add(groupsCountTextField);

        blocksCountLabel.setText("Blocks");
        catalogStatusPanel.add(blocksCountLabel);

        blocksCountTextField.setEditable(false);
        blocksCountTextField.setText(UNKNOWN);
        blocksCountTextField.setBorder(null);
        catalogStatusPanel.add(blocksCountTextField);

        defsCountLabel.setText("Defs");
        catalogStatusPanel.add(defsCountLabel);

        defsCountTextField.setEditable(false);
        defsCountTextField.setText(UNKNOWN);
        defsCountTextField.setBorder(null);
        catalogStatusPanel.add(defsCountTextField);

        revsCountLabel.setText("Revisions");
        catalogStatusPanel.add(revsCountLabel);

        revsCountTextField.setEditable(false);
        revsCountTextField.setText(UNKNOWN);
        revsCountTextField.setBorder(null);
        catalogStatusPanel.add(revsCountTextField);

        lastUpdateLabel.setText("Last Update");
        catalogStatusPanel.add(lastUpdateLabel);

        lastUpdateTextField.setEditable(false);
        lastUpdateTextField.setText(UNKNOWN);
        lastUpdateTextField.setBorder(null);

        lastUpdateNowButton.setText("Set Now");
        lastUpdateNowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastUpdateNowButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lastUpdatePanelLayout = new javax.swing.GroupLayout(lastUpdatePanel);
        lastUpdatePanel.setLayout(lastUpdatePanelLayout);
        lastUpdatePanelLayout.setHorizontalGroup(
            lastUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lastUpdatePanelLayout.createSequentialGroup()
                .addComponent(lastUpdateTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastUpdateNowButton))
        );
        lastUpdatePanelLayout.setVerticalGroup(
            lastUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lastUpdateNowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(lastUpdateTextField)
        );

        catalogStatusPanel.add(lastUpdatePanel);

        javax.swing.GroupLayout catalogStatusBorderPanelLayout = new javax.swing.GroupLayout(catalogStatusBorderPanel);
        catalogStatusBorderPanel.setLayout(catalogStatusBorderPanelLayout);
        catalogStatusBorderPanelLayout.setHorizontalGroup(
            catalogStatusBorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(catalogStatusBorderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(catalogStatusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addContainerGap())
        );
        catalogStatusBorderPanelLayout.setVerticalGroup(
            catalogStatusBorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(catalogStatusPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
        );

        extensionsBorderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Extensions"));

        extensionsScrollPane.setViewportView(extensionsList);

        javax.swing.GroupLayout extensionsBorderPanelLayout = new javax.swing.GroupLayout(extensionsBorderPanel);
        extensionsBorderPanel.setLayout(extensionsBorderPanelLayout);
        extensionsBorderPanelLayout.setHorizontalGroup(
            extensionsBorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extensionsBorderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(extensionsScrollPane)
                .addContainerGap())
        );
        extensionsBorderPanelLayout.setVerticalGroup(
            extensionsBorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extensionsBorderPanelLayout.createSequentialGroup()
                .addComponent(extensionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(extensionsBorderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(catalogStatusBorderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(catalogStatusBorderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extensionsBorderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lastUpdateNowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastUpdateNowButtonActionPerformed
        XBCRootService rootService = catalog == null ? null : ((XBCRootService) catalog.getCatalogService(XBCRootService.class));
        XBCNodeService nodeService = catalog == null ? null : ((XBCNodeService) catalog.getCatalogService(XBCNodeService.class));
        if (rootService instanceof XBERootService && nodeService instanceof XBENodeService) {
            EntityManager em = ((XBECatalog) catalog).getEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            ((XBERootService) rootService).setMainLastUpdateToNow();
            transaction.commit();
            em.refresh(rootService.getMainRoot());
            Optional<Date> lastUpdate = rootService.getMainLastUpdate();
            lastUpdateTextField.setText(lastUpdate.isPresent() ? lastUpdate.get().toString() : "");
        }
    }//GEN-LAST:event_lastUpdateNowButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogStatusPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blocksCountLabel;
    private javax.swing.JTextField blocksCountTextField;
    private javax.swing.JPanel catalogStatusBorderPanel;
    private javax.swing.JPanel catalogStatusPanel;
    private javax.swing.JLabel defsCountLabel;
    private javax.swing.JTextField defsCountTextField;
    private javax.swing.JPanel extensionsBorderPanel;
    private javax.swing.JList<String> extensionsList;
    private javax.swing.JScrollPane extensionsScrollPane;
    private javax.swing.JLabel formatsCountLabel;
    private javax.swing.JTextField formatsCountTextField;
    private javax.swing.JLabel groupsCountLabel;
    private javax.swing.JTextField groupsCountTextField;
    private javax.swing.JLabel itemsCountLabel;
    private javax.swing.JTextField itemsCountTextField;
    private javax.swing.JLabel lastUpdateLabel;
    private javax.swing.JButton lastUpdateNowButton;
    private javax.swing.JPanel lastUpdatePanel;
    private javax.swing.JTextField lastUpdateTextField;
    private javax.swing.JLabel nodesCountLabel;
    private javax.swing.JTextField nodesCountTextField;
    private javax.swing.JLabel revsCountLabel;
    private javax.swing.JTextField revsCountTextField;
    private javax.swing.JLabel specsCountLabel;
    private javax.swing.JTextField specsCountTextField;
    // End of variables declaration//GEN-END:variables

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        updateCatalog();
    }

    private void updateCatalog() {
        extModel.setCatalog(catalog);
        XBCRootService rootService = null;
        XBCNodeService nodeService = null;
        XBCSpecService specService = null;
        XBCRevService revService = null;
        if (catalog != null) {
            rootService = catalog.getCatalogService(XBCRootService.class);
            nodeService = catalog.getCatalogService(XBCNodeService.class);
            specService = catalog.getCatalogService(XBCSpecService.class);
            revService = catalog.getCatalogService(XBCRevService.class);
        }

        lastUpdateNowButton.setEnabled(catalog instanceof XBECatalog);

        Long count = catalog == null ? null : catalog.getCatalogService(XBCItemService.class).getItemsCount();
        itemsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = nodeService == null ? null : nodeService.getItemsCount();
        nodesCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = specService == null ? null : specService.getItemsCount();
        specsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = specService == null ? null : specService.getDefsCount();
        defsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = specService == null ? null : specService.getAllFormatSpecsCount();
        formatsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = specService == null ? null : specService.getAllGroupSpecsCount();
        groupsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = specService == null ? null : specService.getAllBlockSpecsCount();
        blocksCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = revService == null ? null : revService.getItemsCount();
        revsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        count = revService == null ? null : revService.getItemsCount();
        revsCountTextField.setText(count == null ? UNKNOWN : count.toString());
        Date date = rootService == null ? null : ((Optional<Date>) rootService.getMainLastUpdate()).orElse(null);
        lastUpdateTextField.setText(date == null ? UNKNOWN : date.toString());
    }
}