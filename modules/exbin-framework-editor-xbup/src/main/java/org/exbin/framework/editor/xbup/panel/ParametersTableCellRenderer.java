/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.xbup.panel;

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
        JComponent component = tableItem.getLineEditor() == null ? null : tableItem.getLineEditor().getComponent();
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