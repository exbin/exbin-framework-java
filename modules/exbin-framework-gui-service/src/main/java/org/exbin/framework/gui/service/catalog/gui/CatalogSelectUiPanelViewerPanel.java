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
package org.exbin.framework.gui.service.catalog.gui;

import javax.annotation.Nullable;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBPlugUiType;
import org.exbin.xbup.core.catalog.base.XBCXPlugUi;

/**
 * Panel for component editor selection.
 *
 * @version 0.2.1 2020/08/17
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogSelectUiPanelViewerPanel extends javax.swing.JPanel {

    private XBACatalog catalog;
    private XBCXPlugUi plugUi;
    private XBApplication application;

    public CatalogSelectUiPanelViewerPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorButtonGroup = new javax.swing.ButtonGroup();
        noViewerRadioButton = new javax.swing.JRadioButton();
        viewerRadioButton = new javax.swing.JRadioButton();
        viewerTextField = new javax.swing.JTextField();
        selectViewerButton = new javax.swing.JButton();

        editorButtonGroup.add(noViewerRadioButton);
        noViewerRadioButton.setSelected(true);
        noViewerRadioButton.setText("No Viewer");

        editorButtonGroup.add(viewerRadioButton);
        viewerRadioButton.setText("Viewer");

        viewerTextField.setEditable(false);

        selectViewerButton.setText("Select...");
        selectViewerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectViewerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(viewerTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectViewerButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(noViewerRadioButton)
                            .addComponent(viewerRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noViewerRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewerRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectViewerButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    private void selectViewerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectViewerButtonActionPerformed
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        CatalogSelectPlugUiPanel selectPanel = new CatalogSelectPlugUiPanel(XBPlugUiType.PANEL_VIEWER);
        selectPanel.setApplication(application);
        //        editPanel.setMenuManagement(menuManagement);
        selectPanel.setCatalog(catalog);
        // selectPanel.setNode(node);

        DefaultControlPanel controlPanel = new DefaultControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(selectPanel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        //        WindowUtils.addHeaderPanel(dialog.getWindow(), editPanel.getClass(), editPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                plugUi = selectPanel.getPlugUi();
                viewerRadioButton.setSelected(true);
                viewerTextField.setText(String.valueOf(plugUi.getId()));
            }
            dialog.close();
        });
        dialog.showCentered(this);
        dialog.dispose();
    }//GEN-LAST:event_selectViewerButtonActionPerformed

    @Nullable
    public XBCXPlugUi getPlugUi() {
        return noViewerRadioButton.isSelected() ? null : plugUi;
    }

    public void setPlugUi(@Nullable XBCXPlugUi plugUi) {
        this.plugUi = plugUi;
        if (plugUi == null) {
            noViewerRadioButton.setSelected(true);
            viewerTextField.setText("");
        } else {
            viewerRadioButton.setSelected(true);
            viewerTextField.setText(String.valueOf(plugUi.getId()));
        }
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogSelectUiPanelViewerPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup editorButtonGroup;
    private javax.swing.JRadioButton noViewerRadioButton;
    private javax.swing.JButton selectViewerButton;
    private javax.swing.JRadioButton viewerRadioButton;
    private javax.swing.JTextField viewerTextField;
    // End of variables declaration//GEN-END:variables
}