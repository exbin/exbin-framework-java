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
package org.exbin.framework.undo.gui;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import org.exbin.framework.undo.handler.UndoManagerControlHandler;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.OkCancelListener;
import org.exbin.framework.utils.WindowUtils;

/**
 * Undo management control panel.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoManagerControlPanel extends javax.swing.JPanel implements UndoManagerControlHandler.UndoManagerControlService {

    private final java.util.ResourceBundle resourceBundle;
    private UndoManagerControlHandler handler;
    private OkCancelListener okCancelListener;

    public UndoManagerControlPanel() {
        this(LanguageUtils.getResourceBundleByClass(UndoManagerControlPanel.class));
        initComponents();
        init();
    }

    public UndoManagerControlPanel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initComponents();
        init();
    }

    private void init() {
        okCancelListener = new OkCancelListener() {
            @Override
            public void okEvent() {
                performClick(UndoManagerControlHandler.ControlActionType.CANCEL);
            }

            @Override
            public void cancelEvent() {
                performClick(UndoManagerControlHandler.ControlActionType.CANCEL);
            }
        };
    }

    public void setHandler(UndoManagerControlHandler handler) {
        this.handler = handler;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        revertButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        revertButton.setText(resourceBundle.getString("revertButton.text")); // NOI18N
        revertButton.setEnabled(false);
        revertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertButtonActionPerformed(evt);
            }
        });

        closeButton.setText(resourceBundle.getString("closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(revertButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(revertButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void revertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed(UndoManagerControlHandler.ControlActionType.REVERT_TO);
        }
    }//GEN-LAST:event_revertButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed(UndoManagerControlHandler.ControlActionType.CANCEL);
        }
    }//GEN-LAST:event_closeButtonActionPerformed

    @Override
    public void performClick(UndoManagerControlHandler.ControlActionType actionType) {
        switch (actionType) {
            case REVERT_TO: {
                WindowUtils.doButtonClick(revertButton);
                break;
            }
            case CANCEL: {
                WindowUtils.doButtonClick(closeButton);
            }
        }
    }

    @Nonnull
    @Override
    public JButton getDefaultButton() {
        return closeButton;
    }

    @Nonnull
    @Override
    public OkCancelListener getOkCancelListener() {
        return okCancelListener;
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new UndoManagerControlPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton revertButton;
    // End of variables declaration//GEN-END:variables
}