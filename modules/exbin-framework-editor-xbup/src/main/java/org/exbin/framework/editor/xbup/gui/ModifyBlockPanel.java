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
package org.exbin.framework.editor.xbup.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBBlockTerminationMode;
import org.exbin.xbup.core.block.XBFixedBlockType;
import org.exbin.xbup.core.block.declaration.XBBlockDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBPlugUiType;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.XBCRev;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;
import org.exbin.xbup.core.catalog.base.XBCXBlockUi;
import org.exbin.xbup.core.catalog.base.XBCXName;
import org.exbin.xbup.core.catalog.base.XBCXPlugUi;
import org.exbin.xbup.core.catalog.base.XBCXPlugin;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.token.XBAttribute;
import org.exbin.xbup.core.parser.token.pull.convert.XBTProviderToPullProvider;
import org.exbin.xbup.core.serial.XBPSerialReader;
import org.exbin.xbup.core.serial.XBPSerialWriter;
import org.exbin.xbup.core.serial.XBSerializable;
import org.exbin.xbup.core.ubnumber.UBNatural;
import org.exbin.xbup.core.ubnumber.type.UBNat32;
import org.exbin.xbup.parser_tree.XBATreeParamExtractor;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.parser_tree.XBTTreeWriter;
import org.exbin.xbup.plugin.XBCatalogPlugin;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.xbup.plugin.XBRowEditor;
import org.exbin.xbup.plugin.XBPanelEditor;
import org.exbin.xbup.plugin.XBPanelEditorCatalogPlugin;
import org.exbin.xbup.plugin.XBRowEditorCatalogPlugin;

/**
 * Panel for modifying item attributes or data.
 *
 * @version 0.2.1 2020/09/25
 * @author ExBin Project (http://exbin.org)
 */
public class ModifyBlockPanel extends javax.swing.JPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private XBPluginRepository pluginRepository;

    private final AttributesTableModel attributesTableModel = new AttributesTableModel();
    private final ParametersTableModel parametersTableModel = new ParametersTableModel();
    private XBTTreeDocument doc;
    private XBTTreeNode srcNode;
    private XBTTreeNode newNode = null;

    private final BinEdFileHandler binaryDataFile;
    private XBPanelEditor customPanel;
    private XBBlockDataMode dataMode = XBBlockDataMode.NODE_BLOCK;
    private List<XBAttribute> attributes = null;
    private BinEdFileHandler tailDataBinaryDataFile = null;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(ModifyBlockPanel.class);

    private final String attributesPanelTitle;
    private final String dataPanelTitle;
    private final String parametersPanelTitle;
    private final String extAreaEditorPanelTitle;
    private final String basicPanelTitle;
    private final String customEditorPanelTitle = "Custom";
    private boolean dataChanged = false;

    private boolean initialResize = true;

    public ModifyBlockPanel() {
        initComponents();

        binaryDataFile = new BinEdFileHandler();
        BinEdComponentPanel componentPanel = (BinEdComponentPanel) binaryDataFile.getComponent();
        componentPanel.setContentData(new ByteArrayEditableData());
        customPanel = null;
        binaryEditPanel.add(binaryDataFile.getComponent());

        attributesPanelTitle = mainTabbedPane.getTitleAt(mainTabbedPane.indexOfComponent(attributesPanel));
        dataPanelTitle = mainTabbedPane.getTitleAt(mainTabbedPane.indexOfComponent(dataPanel));
        parametersPanelTitle = mainTabbedPane.getTitleAt(mainTabbedPane.indexOfComponent(parametersPanel));
        extAreaEditorPanelTitle = mainTabbedPane.getTitleAt(mainTabbedPane.indexOfComponent(tailDataPanel));
        basicPanelTitle = mainTabbedPane.getTitleAt(mainTabbedPane.indexOfComponent(basicPanel));

        init();
    }

    private void init() {
        attributesTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                updateAttributesButtons();
            }
        });

        attributesTableModel.attachChangeListener(() -> {
            dataChanged = true;
        });

        terminationModeCheckBox.addItemListener((ItemEvent e) -> {
            dataChanged = true;
        });

        // DefaultCellEditor attributesTableCellEditor = new DefaultCellEditor(new JTextField());
        // attributesTableCellEditor.setClickCountToStart(0);
        // attributesTable.getColumnModel().getColumn(1).setCellEditor(attributesTableCellEditor);
        mainTabbedPane.addChangeListener((ChangeEvent e) -> {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if (pane.getSelectedIndex() < 0) {
                return;
            }

            String currentTitle = pane.getTitleAt(pane.getSelectedIndex());
            if (basicPanelTitle.equals(currentTitle)) {
                if (dataChanged) {
                    reloadBasic();
                }
                dataChanged = false;
            } else if (attributesPanelTitle.equals(currentTitle)) {
                if (dataChanged || attributes == null) {
                    reloadAttributes();
                }
                dataChanged = false;
            } else if (parametersPanelTitle.equals(currentTitle)) {
                if (dataChanged || parametersTableModel.isEmpty()) {
                    reloadParameters();
                }
                dataChanged = false;
            } else if (customEditorPanelTitle.equals(currentTitle)) {
                if (dataChanged) {
                    reloadCustomEditor();
                }
                dataChanged = false;
            } else if (extAreaEditorPanelTitle.equals(currentTitle)) {
                if (tailDataBinaryDataFile == null) {
                    reloadTailData();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (initialResize) {
                    int parametersTableWidth = parametersTable.getWidth();
                    parametersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    parametersTable.getColumnModel().getColumn(0).setPreferredWidth(parametersTableWidth / 6);
                    parametersTable.getColumnModel().getColumn(1).setPreferredWidth(parametersTableWidth / 6);
                    parametersTable.getColumnModel().getColumn(2).setPreferredWidth(parametersTableWidth / 6);
                    parametersTable.getColumnModel().getColumn(3).setPreferredWidth(parametersTableWidth / 2);

                    initialResize = false;
                }
            }
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    private void updateAttributesButtons() {
        removeButton.setEnabled(attributesTable.getSelectedRowCount() > 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTabbedPane = new javax.swing.JTabbedPane();
        basicPanel = new javax.swing.JPanel();
        terminationModeCheckBox = new javax.swing.JCheckBox();
        parametersPanel = new javax.swing.JPanel();
        parametersScrollPane = new javax.swing.JScrollPane();
        parametersTable = new javax.swing.JTable();
        attributesPanel = new javax.swing.JPanel();
        attributesScrollPane = new javax.swing.JScrollPane();
        attributesTable = new JTable(attributesTableModel) {
            @Override
            public boolean editCellAt(int row, int column, EventObject e) {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();
                if (editor == null || !(editor instanceof JTextComponent)) {
                    return result;
                }
                if (e instanceof MouseEvent) {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ((JTextComponent) editor).selectAll();
                        }

                    });
                } else {
                    ((JTextComponent) editor).selectAll();
                }
                return result;
            }
        };
        removeButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        dataPanel = new javax.swing.JPanel();
        saveAsButton = new javax.swing.JButton();
        loadFromButton = new javax.swing.JButton();
        binaryEditPanel = new javax.swing.JPanel();
        tailDataPanel = new javax.swing.JPanel();
        binaryEditScrollPane = new javax.swing.JScrollPane();
        extLoadFromButton = new javax.swing.JButton();
        extSaveFromButto = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        terminationModeCheckBox.setText("Block size specified");

        javax.swing.GroupLayout basicPanelLayout = new javax.swing.GroupLayout(basicPanel);
        basicPanel.setLayout(basicPanelLayout);
        basicPanelLayout.setHorizontalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(terminationModeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addContainerGap())
        );
        basicPanelLayout.setVerticalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(terminationModeCheckBox)
                .addContainerGap(329, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Basic", basicPanel);

        parametersTable.setModel(parametersTableModel);
        parametersScrollPane.setViewportView(parametersTable);

        javax.swing.GroupLayout parametersPanelLayout = new javax.swing.GroupLayout(parametersPanel);
        parametersPanel.setLayout(parametersPanelLayout);
        parametersPanelLayout.setHorizontalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parametersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addContainerGap())
        );
        parametersPanelLayout.setVerticalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parametersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPane.addTab("Parameters", null, parametersPanel, "List of parameters on level 1");

        attributesTable.setModel(attributesTableModel);
        attributesTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                attributesTablePropertyChange(evt);
            }
        });
        attributesScrollPane.setViewportView(attributesTable);

        removeButton.setText(resourceBundle.getString("removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        addButton.setText(resourceBundle.getString("addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout attributesPanelLayout = new javax.swing.GroupLayout(attributesPanel);
        attributesPanel.setLayout(attributesPanelLayout);
        attributesPanelLayout.setHorizontalGroup(
            attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attributesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addGroup(attributesPanelLayout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        attributesPanelLayout.setVerticalGroup(
            attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attributesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(removeButton))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Attributes (Level 0)", null, attributesPanel, "List of attributes on level 0");

        saveAsButton.setText(resourceBundle.getString("saveAsButton.text")); // NOI18N
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });

        loadFromButton.setText(resourceBundle.getString("loadFromButton.text")); // NOI18N
        loadFromButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFromButtonActionPerformed(evt);
            }
        });

        binaryEditPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        binaryEditPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(binaryEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addGroup(dataPanelLayout.createSequentialGroup()
                        .addComponent(loadFromButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveAsButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveAsButton)
                    .addComponent(loadFromButton))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Data (Level 0)", dataPanel);

        extLoadFromButton.setText(resourceBundle.getString("loadFromButton.text")); // NOI18N
        extLoadFromButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extLoadFromButtonActionPerformed(evt);
            }
        });

        extSaveFromButto.setText(resourceBundle.getString("saveAsButton.text")); // NOI18N
        extSaveFromButto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extSaveFromButtoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tailDataPanelLayout = new javax.swing.GroupLayout(tailDataPanel);
        tailDataPanel.setLayout(tailDataPanelLayout);
        tailDataPanelLayout.setHorizontalGroup(
            tailDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tailDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tailDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(binaryEditScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addGroup(tailDataPanelLayout.createSequentialGroup()
                        .addComponent(extLoadFromButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(extSaveFromButto)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tailDataPanelLayout.setVerticalGroup(
            tailDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tailDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryEditScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tailDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(extSaveFromButto)
                    .addComponent(extLoadFromButton))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Tail Data", tailDataPanel);

        add(mainTabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void attributesTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_attributesTablePropertyChange
        attributesTable.repaint();
    }//GEN-LAST:event_attributesTablePropertyChange

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int[] selectedRows = attributesTable.getSelectedRows();
        if (selectedRows.length > 0) {
            Arrays.sort(selectedRows);
            for (int index = selectedRows.length - 1; index >= 0; index--) {
                attributes.remove(selectedRows[index]);
            }

            attributesTableModel.fireTableDataChanged();
            attributesTable.clearSelection();
            attributesTable.revalidate();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        attributes.add(new UBNat32());
        attributesTableModel.fireTableDataChanged();
        attributesTable.revalidate();
        updateAttributesButtons();
    }//GEN-LAST:event_addButtonActionPerformed

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setAcceptAllFileFilterUsed(true);
        if (saveFileChooser.showSaveDialog(WindowUtils.getFrame(this)) == JFileChooser.APPROVE_OPTION) {
            binaryDataFile.saveToFile(saveFileChooser.getSelectedFile().toURI(), null);
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void loadFromButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFromButtonActionPerformed
        JFileChooser loadFileChooser = new JFileChooser();
        loadFileChooser.setAcceptAllFileFilterUsed(true);
        if (loadFileChooser.showOpenDialog(WindowUtils.getFrame(this)) == JFileChooser.APPROVE_OPTION) {
            binaryDataFile.loadFromFile(loadFileChooser.getSelectedFile().toURI(), null);
        }
    }//GEN-LAST:event_loadFromButtonActionPerformed

    private void extLoadFromButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extLoadFromButtonActionPerformed
        JFileChooser loadFileChooser = new JFileChooser();
        loadFileChooser.setAcceptAllFileFilterUsed(true);
        if (loadFileChooser.showOpenDialog(WindowUtils.getFrame(this)) == JFileChooser.APPROVE_OPTION) {
            tailDataBinaryDataFile.loadFromFile(loadFileChooser.getSelectedFile().toURI(), null);
        }
    }//GEN-LAST:event_extLoadFromButtonActionPerformed

    private void extSaveFromButtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extSaveFromButtoActionPerformed
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setAcceptAllFileFilterUsed(true);
        if (saveFileChooser.showSaveDialog(WindowUtils.getFrame(this)) == JFileChooser.APPROVE_OPTION) {
            tailDataBinaryDataFile.saveToFile(saveFileChooser.getSelectedFile().toURI(), null);
        }
    }//GEN-LAST:event_extSaveFromButtoActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new ModifyBlockPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel attributesPanel;
    private javax.swing.JScrollPane attributesScrollPane;
    private javax.swing.JTable attributesTable;
    private javax.swing.JPanel basicPanel;
    private javax.swing.JPanel binaryEditPanel;
    private javax.swing.JScrollPane binaryEditScrollPane;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JButton extLoadFromButton;
    private javax.swing.JButton extSaveFromButto;
    private javax.swing.JButton loadFromButton;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JScrollPane parametersScrollPane;
    private javax.swing.JTable parametersTable;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JPanel tailDataPanel;
    private javax.swing.JCheckBox terminationModeCheckBox;
    // End of variables declaration//GEN-END:variables

    public void setNode(XBTTreeNode srcNode, XBTTreeDocument doc) {
        this.srcNode = srcNode;
        this.doc = doc;
        newNode = srcNode.cloneNode(true);

        mainTabbedPane.removeAll();
        customPanel = null;

        reloadBasic();
        mainTabbedPane.addTab(basicPanelTitle, basicPanel);
        dataMode = srcNode.getDataMode();
        if (dataMode == XBBlockDataMode.DATA_BLOCK) {
            mainTabbedPane.addTab(dataPanelTitle, dataPanel);

            try {
                binaryDataFile.loadFromStream(srcNode.getData(), srcNode.getDataSize());
            } catch (IOException ex) {
                Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            reloadParameters();
            TableColumnModel columnModel = parametersTable.getColumnModel();
            TableColumn column = columnModel.getColumn(3);
            ParametersTableCellEditor parametersTableCellEditor = new ParametersTableCellEditor(catalog, pluginRepository, newNode, doc);
            parametersTableCellEditor.setApplication(application);
            column.setCellEditor(parametersTableCellEditor);
            ParametersTableCellRenderer parametersTableCellRenderer = new ParametersTableCellRenderer(catalog, pluginRepository, newNode, doc);
            parametersTableCellRenderer.setApplication(application);
            column.setCellRenderer(parametersTableCellRenderer);

            try {
                customPanel = getCustomPanel(srcNode);
                if (customPanel != null) {
                    ((XBPanelEditor) customPanel).attachChangeListener(() -> {
                        dataChanged = true;
                    });

                    reloadCustomEditor();
                    mainTabbedPane.addTab(customEditorPanelTitle, customPanel.getEditor());
                }
            } catch (Exception ex) {
                Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            mainTabbedPane.addTab(parametersPanelTitle, parametersPanel);
            mainTabbedPane.addTab(attributesPanelTitle, attributesPanel);
        }

        if (srcNode.getParent() == null) {
            mainTabbedPane.addTab(extAreaEditorPanelTitle, tailDataPanel);
            tailDataBinaryDataFile = null;
        }

        mainTabbedPane.setSelectedIndex(1);
    }

    public XBTTreeNode getNode() {
        if (dataMode == XBBlockDataMode.DATA_BLOCK) {
            try {
                BinEdComponentPanel componentPanel = (BinEdComponentPanel) binaryDataFile.getComponent();        
                newNode.setData(componentPanel.getContentData().getDataInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // TODO: Update tabs on changes
            int attributesCount = attributes.size();
            UBNatural groupId = attributesCount == 0 ? new UBNat32().convertToNatural() : attributes.get(0).convertToNatural();
            UBNatural blockId = attributesCount < 2 ? new UBNat32().convertToNatural() : attributes.get(1).convertToNatural();
            XBFixedBlockType fixedBlockType = new XBFixedBlockType(groupId, blockId);
            newNode.setFixedBlockType(fixedBlockType);
            if (attributesCount > 2) {
                XBAttribute[] attribs = new XBAttribute[attributesCount - 2];
                for (int i = 0; i < attributesCount - 2; i++) {
                    attribs[i] = attributes.get(i + 2);
                }
                newNode.setAttributes(attribs);
            } else {
                newNode.clearAttributes();
            }
        }

        return newNode;
    }

    public void saveTailData(OutputStream stream) {
        try {
            if (tailDataBinaryDataFile != null) {
                tailDataBinaryDataFile.saveToStream(stream);
            }
        } catch (IOException ex) {
            Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Nullable
    private XBPanelEditor getCustomPanel(XBTTreeNode srcNode) {
        if (catalog == null) {
            return null;
        }
        if (srcNode.getBlockType() == null) {
            return null;
        }
        if (srcNode.getBlockDecl() == null) {
            return null;
        }
        XBCXUiService uiService = catalog.getCatalogService(XBCXUiService.class);
        XBCBlockDecl decl = (XBCBlockDecl) srcNode.getBlockDecl();
        if (decl == null) {
            return null;
        }
        XBCBlockRev rev = decl.getBlockSpecRev();
        if (rev == null) {
            return null;
        }
        XBCXBlockUi blockUi = uiService.findUiByPR(rev, XBPlugUiType.PANEL_EDITOR, 0);
        if (blockUi == null) {
            return null;
        }
        XBCXPlugUi plugUi = blockUi.getUi();
        if (plugUi == null) {
            return null;
        }
        XBCXPlugin plugin = plugUi.getPlugin();
        XBCatalogPlugin pluginHandler;

        // This part is stub for Java Webstart, uncomment it if needed
        /*if ("XBPicturePlugin.jar".equals(plugin.getPluginFile().getFilename())) {
         pluginHandler = new XBPicturePlugin();
         } else */
        pluginHandler = pluginRepository.getPluginHandler(plugin);
        if (pluginHandler == null) {
            return null;
        }

        return ((XBPanelEditorCatalogPlugin) pluginHandler).getPanelEditor(plugUi.getMethodIndex());
    }

    private void reloadBasic() {
        terminationModeCheckBox.setSelected(srcNode.getTerminationMode() == XBBlockTerminationMode.SIZE_SPECIFIED);
    }

    private void reloadAttributes() {
        attributes = new ArrayList<>();
        XBFixedBlockType fixedBlockType = srcNode.getFixedBlockType();
        attributes.add(fixedBlockType.getGroupID());
        if (!srcNode.getSingleAttributeType()) {
            attributes.add(fixedBlockType.getBlockID());
            attributes.addAll(Arrays.asList(srcNode.getAttributes()));
        }
        attributesTableModel.setAttribs(attributes);
        updateAttributesButtons();
    }

    private void reloadParameters() {
        parametersTableModel.clear();

        if (srcNode == null) {
            return;
        }

        XBBlockDecl decl = srcNode.getBlockDecl();
        XBCSpecService specService = catalog.getCatalogService(XBCSpecService.class);
        if (decl instanceof XBCBlockDecl) {
            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);
            XBCXUiService uiService = catalog.getCatalogService(XBCXUiService.class);
            XBCBlockSpec spec = ((XBCBlockDecl) decl).getBlockSpecRev().getParent();
            long bindCount = specService.getSpecDefsCount(spec);
            XBATreeParamExtractor paramExtractor = new XBATreeParamExtractor(srcNode, catalog);
            // TODO: if (desc != null) descTextField.setText(desc.getText());
            for (int paramIndex = 0; paramIndex < bindCount; paramIndex++) {
                // TODO: Exclusive lock
                XBCSpecDef specDef = specService.getSpecDefByOrder(spec, paramIndex);
                String specName = "";
                String specType = "";
                XBRowEditor lineEditor = null;

                if (specDef != null) {
                    XBCXName specDefName = nameService.getDefaultItemName(specDef);
                    if (specDefName != null) {
                        specName = specDefName.getText();
                    }

                    Optional<XBCRev> rowRev = specDef.getTargetRev();
                    if (rowRev.isPresent()) {
                        XBCSpec rowSpec = rowRev.get().getParent();
                        try {
                            lineEditor = getCustomEditor((XBCBlockRev) rowRev.get(), uiService);
                            if (lineEditor != null) {
                                paramExtractor.setParameterIndex(paramIndex);
                                XBPSerialReader serialReader = new XBPSerialReader(paramExtractor);
                                serialReader.read(lineEditor);

                                lineEditor.attachChangeListener(new ComponentEditorChangeListener(lineEditor, paramExtractor, paramIndex));
                            }
                        } catch (IOException | XBProcessingException ex) {
                            Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        XBCXName typeName = nameService.getDefaultItemName(rowSpec);
                        specType = typeName.getText();
                    }
                }

                ParametersTableItem itemRecord = new ParametersTableItem(specDef, specName, specType, lineEditor);
                itemRecord.setTypeName(itemRecord.getDefTypeName());
                parametersTableModel.addRow(itemRecord);
            }
        }
    }

    private void reloadTailData() {
        tailDataBinaryDataFile = new BinEdFileHandler();
        BinEdComponentPanel componentPanel = (BinEdComponentPanel) tailDataBinaryDataFile.getComponent();        
        componentPanel.setContentData(new ByteArrayEditableData());
        binaryEditScrollPane.setViewportView(componentPanel);

        if (doc != null && doc.getTailDataSize() > 0) {
            try {
                tailDataBinaryDataFile.loadFromStream(doc.getTailData().get(), doc.getTailDataSize());
            } catch (IOException ex) {
                Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void reloadCustomEditor() {
        XBPSerialReader serialReader = new XBPSerialReader(new XBTProviderToPullProvider(new XBTTreeWriter(srcNode)));
        try {
            serialReader.read((XBSerializable) customPanel);
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private XBRowEditor getCustomEditor(XBCBlockRev rev, XBCXUiService uiService) {
        if (rev == null || catalog == null) {
            return null;
        }

        XBCXBlockUi blockUi = uiService.findUiByPR(rev, XBPlugUiType.ROW_EDITOR, 0);
        if (blockUi == null) {
            return null;
        }
        XBCXPlugUi plugUi = blockUi.getUi();
        if (plugUi == null) {
            return null;
        }
        XBCXPlugin plugin = plugUi.getPlugin();
        XBCatalogPlugin pluginHandler;

        pluginHandler = pluginRepository.getPluginHandler(plugin);
        if (pluginHandler == null) {
            return null;
        }

        return ((XBRowEditorCatalogPlugin) pluginHandler).getRowEditor(plugUi.getMethodIndex());
    }

    private class ComponentEditorChangeListener implements XBRowEditor.ChangeListener {

        private final XBATreeParamExtractor paramExtractor;
        private final int parameterIndex;
        private final XBRowEditor lineEditor;

        private ComponentEditorChangeListener(XBRowEditor lineEditor, XBATreeParamExtractor paramExtractor, int parameterIndex) {
            this.lineEditor = lineEditor;
            this.paramExtractor = paramExtractor;
            this.parameterIndex = parameterIndex;
        }

        @Override
        public void valueChanged() {
            paramExtractor.setParameterIndex(parameterIndex);
            XBPSerialWriter serialWriter = new XBPSerialWriter(paramExtractor);
            serialWriter.write(lineEditor);
            dataChanged = true;
        }
    }
}
