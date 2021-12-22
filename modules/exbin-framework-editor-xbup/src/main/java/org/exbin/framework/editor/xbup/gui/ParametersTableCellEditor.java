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
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.exbin.framework.api.XBApplication;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.xbup.plugin.XBRowEditor;

/**
 * Property table cell renderer.
 *
 * @version 0.2.24 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class ParametersTableCellEditor extends DefaultCellEditor {

    private XBApplication application;
    private XBACatalog catalog;
    private final XBPluginRepository pluginRepository;

    private XBTTreeNode node;
    private XBRowEditor lineEditor = null;
    private JComponent lineEditorComponent = null;
    private final XBTTreeDocument doc;

    public ParametersTableCellEditor(XBACatalog catalog, XBPluginRepository pluginRepository, XBTTreeNode node, XBTTreeDocument doc) {
        super(new JTextField());
        super.setClickCountToStart(0);
        this.catalog = catalog;
        this.pluginRepository = pluginRepository;
        this.node = node;
        this.doc = doc;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ParametersTableItem tableItem = ((ParametersTableModel) table.getModel()).getRow(row);
        lineEditor = tableItem.getRowEditor();
        lineEditorComponent = lineEditor == null ? null : lineEditor.getEditor();
        XBPropertyTableCellPanel cellPanel;
        if (lineEditorComponent == null) {
            JComponent defaultComponent = (JComponent) super.getTableCellEditorComponent(table, value, isSelected, row, column);
            defaultComponent.setEnabled(false);
            cellPanel = new XBPropertyTableCellPanel(defaultComponent, catalog, pluginRepository, node, doc, row);
        } else {
            cellPanel = new XBPropertyTableCellPanel(lineEditorComponent, catalog, pluginRepository, node, doc, row);
        }
        cellPanel.setApplication(application);
        cellPanel.setBackground(table.getSelectionBackground());
        cellPanel.getCellComponent().setBorder(null);
        return cellPanel;
    }

    @Override
    public boolean stopCellEditing() {
        if (lineEditor != null) {
            try {
                lineEditor.finishEditor();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editorComponent, ex.toString(), "Cell Input", JOptionPane.ERROR_MESSAGE);
            }
        }

        return super.stopCellEditing();
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setNode(XBTTreeNode node) {
        this.node = node;
    }
}
