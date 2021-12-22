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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Property table cell renderer.
 *
 * @version 0.2.1 2020/09/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBPropertyTableCellRenderer implements TableCellRenderer {

    private XBACatalog catalog;
    private XBPluginRepository pluginRepository;
    private XBTTreeNode node;
    private final XBTTreeDocument doc;

    public XBPropertyTableCellRenderer(XBTTreeNode node, XBTTreeDocument doc) {
        this.node = node;
        this.doc = doc;
    }

    @Nonnull
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        XBPropertyTableItem tableItem = ((XBPropertyTableModel) table.getModel()).getRow(row);
        JComponent component = tableItem.getRowEditor() == null ? null : tableItem.getRowEditor().getViewer();
        XBPropertyTableCellPanel cellPanel = component == null ? new XBPropertyTableCellPanel(catalog, pluginRepository, node, doc, row) : new XBPropertyTableCellPanel(component, catalog, pluginRepository, node, doc, row);
        cellPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        cellPanel.getCellComponent().setBorder(null);
        return cellPanel;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    public void setBlock(XBTBlock block) {
        this.node = (XBTTreeNode) block;
    }
}
