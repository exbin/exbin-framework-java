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
package org.exbin.framework.operation.manager.gui;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.operation.manager.service.UndoManagerService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.operation.api.Command;
import org.exbin.framework.operation.undo.api.UndoRedo;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;

/**
 * Undo management panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoManagerPanel extends javax.swing.JPanel {

    private UndoManagerModel undoModel = new UndoManagerModel();
    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(UndoManagerPanel.class);
    private UndoManagerService undoManagerService;

    public UndoManagerPanel() {
        initComponents();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setCommandSequence(UndoRedo undoRedo) {
        undoModel.setUndoRedo(undoRedo);
    }

    public void setUndoManagerService(UndoManagerService undoManagerService) {
        this.undoManagerService = undoManagerService;
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
        undoListScrollPane = new javax.swing.JScrollPane();
        undoList = new javax.swing.JList();
        undoDetailPanel = new javax.swing.JPanel();
        undoDetailInfoPanel = new javax.swing.JPanel();
        commandCaptionLabel = new javax.swing.JLabel();
        commandCaptionTextField = new javax.swing.JTextField();
        commandTypeLabel = new javax.swing.JLabel();
        commandTypeTextField = new javax.swing.JTextField();
        executionTimeLabel = new javax.swing.JLabel();
        executionTimeTextField = new javax.swing.JTextField();
        operationCaptionLabel = new javax.swing.JLabel();
        operationCaptionTextField = new javax.swing.JTextField();
        operationTypeLabel = new javax.swing.JLabel();
        operationTypeTextField = new javax.swing.JTextField();
        dataSizeLabel = new javax.swing.JLabel();
        dataSizeTextField = new javax.swing.JTextField();
        exportButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        splitPane.setBorder(null);
        splitPane.setDividerLocation(200);

        undoList.setModel(undoModel);
        undoList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        undoList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                undoListValueChanged(evt);
            }
        });
        undoListScrollPane.setViewportView(undoList);

        splitPane.setLeftComponent(undoListScrollPane);

        undoDetailInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("undoDetailInfoPanel.border.title"))); // NOI18N

        commandCaptionLabel.setText(resourceBundle.getString("commandCaptionLabel.text")); // NOI18N

        commandCaptionTextField.setEditable(false);

        commandTypeLabel.setText(resourceBundle.getString("commandTypeLabel.text")); // NOI18N

        commandTypeTextField.setEditable(false);

        executionTimeLabel.setText(resourceBundle.getString("executionTimeLabel.text")); // NOI18N

        executionTimeTextField.setEditable(false);

        operationCaptionLabel.setText(resourceBundle.getString("operationCaptionLabel.text")); // NOI18N

        operationCaptionTextField.setEditable(false);

        operationTypeLabel.setText(resourceBundle.getString("operationTypeLabel.text")); // NOI18N

        operationTypeTextField.setEditable(false);

        dataSizeLabel.setText(resourceBundle.getString("dataSizeLabel.text")); // NOI18N

        dataSizeTextField.setEditable(false);

        javax.swing.GroupLayout undoDetailInfoPanelLayout = new javax.swing.GroupLayout(undoDetailInfoPanel);
        undoDetailInfoPanel.setLayout(undoDetailInfoPanelLayout);
        undoDetailInfoPanelLayout.setHorizontalGroup(
            undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commandCaptionTextField)
                    .addComponent(commandTypeTextField)
                    .addComponent(operationCaptionTextField)
                    .addComponent(operationTypeTextField)
                    .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                        .addGroup(undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(commandCaptionLabel)
                            .addComponent(commandTypeLabel)
                            .addComponent(operationCaptionLabel)
                            .addComponent(operationTypeLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                        .addGroup(undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                                .addComponent(executionTimeLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(executionTimeTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dataSizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dataSizeTextField))))
                .addContainerGap())
        );
        undoDetailInfoPanelLayout.setVerticalGroup(
            undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commandCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandCaptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operationCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operationCaptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(undoDetailInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                        .addComponent(operationTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(operationTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(executionTimeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(executionTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(undoDetailInfoPanelLayout.createSequentialGroup()
                        .addComponent(dataSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(155, Short.MAX_VALUE))
        );

        exportButton.setText(resourceBundle.getString("exportButton.text")); // NOI18N
        exportButton.setEnabled(false);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout undoDetailPanelLayout = new javax.swing.GroupLayout(undoDetailPanel);
        undoDetailPanel.setLayout(undoDetailPanelLayout);
        undoDetailPanelLayout.setHorizontalGroup(
            undoDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, undoDetailPanelLayout.createSequentialGroup()
                .addGap(292, 422, Short.MAX_VALUE)
                .addComponent(exportButton)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, undoDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(undoDetailInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        undoDetailPanelLayout.setVerticalGroup(
            undoDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(undoDetailPanelLayout.createSequentialGroup()
                .addComponent(undoDetailInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportButton)
                .addContainerGap())
        );

        splitPane.setRightComponent(undoDetailPanel);

        add(splitPane, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void undoListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_undoListValueChanged
        if (!evt.getValueIsAdjusting()) {
            updateDetail(undoList.getSelectedIndex());
        }
    }//GEN-LAST:event_undoListValueChanged

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        int selectedIndex = undoList.getSelectedIndex();
        Command command = null;
        if (selectedIndex >= 0) {
            command = undoModel.getItem(selectedIndex);
        }

        undoManagerService.exportCommand(this, command);
    }//GEN-LAST:event_exportButtonActionPerformed

    public long getCommandPosition() {
        return undoList.getSelectedIndex();
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new UndoManagerPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel commandCaptionLabel;
    private javax.swing.JTextField commandCaptionTextField;
    private javax.swing.JLabel commandTypeLabel;
    private javax.swing.JTextField commandTypeTextField;
    private javax.swing.JLabel dataSizeLabel;
    private javax.swing.JTextField dataSizeTextField;
    private javax.swing.JLabel executionTimeLabel;
    private javax.swing.JTextField executionTimeTextField;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel operationCaptionLabel;
    private javax.swing.JTextField operationCaptionTextField;
    private javax.swing.JLabel operationTypeLabel;
    private javax.swing.JTextField operationTypeTextField;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel undoDetailInfoPanel;
    private javax.swing.JPanel undoDetailPanel;
    private javax.swing.JList undoList;
    private javax.swing.JScrollPane undoListScrollPane;
    // End of variables declaration//GEN-END:variables

    private void updateDetail(int selectedIndex) {
        Command command = null;
        if (selectedIndex >= 0) {
            command = undoModel.getItem(selectedIndex);
        }

        // TODO revertButton.setEnabled(selectedIndex >= 0 && selectedIndex != undoModel.getCurrentPosition());
        exportButton.setEnabled(command != null);

        commandCaptionTextField.setText(command != null ? command.getType().toString() : "");
//        commandTypeTextField.setText(command instanceof XBDocCommand ? ((XBDocCommand) command).getBasicType().name() : "");
//        Date executionTime = command != null ? command.getExecutionTime().orElse(null) : null;
//        executionTimeTextField.setText(executionTime != null ? executionTime.toString() : "");

//        XBTDocOperation operation = null;
//        if (command instanceof XBTOpDocCommand) {
//            operation = ((XBTOpDocCommand) command).getCurrentOperation().orElse(null);
//        }
//        operationCaptionTextField.setText(operation != null ? operation.getCaption() : "");
//        operationTypeTextField.setText(operation != null ? operation.getBasicType().name() : "");
//        dataSizeTextField.setText(operation != null ? String.valueOf(operation.getData().getDataSize()) : "");
    }
}
