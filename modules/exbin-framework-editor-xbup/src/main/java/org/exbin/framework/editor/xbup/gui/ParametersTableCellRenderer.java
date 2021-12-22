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
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.exbin.framework.api.XBApplication;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Property table cell renderer.
 *
 * @version 0.2.1 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class ParametersTableCellRenderer implements TableCellRenderer {

    private XBApplication application;
    private XBACatalog catalog;
    private XBPluginRepository pluginRepository;
    private XBTTreeNode node;
    private final XBTTreeDocument doc;

    public ParametersTableCellRenderer(XBACatalog catalog, XBPluginRepository pluginRepository, XBTTreeNode node, XBTTreeDocument doc) {
        this.catalog = catalog;
        this.pluginRepository = pluginRepository;
        this.node = node;
        this.doc = doc;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ParametersTableItem tableItem = ((ParametersTableModel) table.getModel()).getRow(row);
        JComponent component = tableItem.getRowEditor() == null ? null : tableItem.getRowEditor().getViewer();
        XBPropertyTableCellPanel cellPanel = component == null ? new XBPropertyTableCellPanel(catalog, pluginRepository, node, doc, row) : new XBPropertyTableCellPanel(component, catalog, pluginRepository, node, doc, row);
        cellPanel.setApplication(application);
        cellPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        cellPanel.getCellComponent().setBorder(null);
        return cellPanel;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setNode(XBTTreeNode node) {
        this.node = node;
    }
}
