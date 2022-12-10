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
package org.exbin.framework.service.catalog.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.XBPlugUiType;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCXFile;
import org.exbin.xbup.core.catalog.base.XBCXPlugin;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;
import org.exbin.xbup.core.catalog.base.service.XBCXPlugService;

/**
 * Table model for catalog plugins.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogPluginsTableModel extends AbstractTableModel {

    private XBCatalog catalog;
    private XBCXPlugService pluginService;
    private XBCNode node;

    private final String[] columnNames = new String[]{"Index", "Filename", "Row Editors", "Panel Viewers", "Panel Editors"};
    private final Class[] columnClasses = new Class[]{
        java.lang.Long.class, java.lang.String.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class
    };

    private List<PluginItemRecord> items = new ArrayList<>();

    public CatalogPluginsTableModel() {
        node = null;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                XBCXPlugin plugin = items.get(rowIndex).plugin;
                return plugin == null ? 0 : plugin.getId();
            }
            case 1: {
                return items.get(rowIndex).fileName;
            }
            case 2: {
                return items.get(rowIndex).rowEditorsCount;
            }
            case 3: {
                return items.get(rowIndex).panelViewersCount;
            }
            case 4: {
                return items.get(rowIndex).panelEditorsCount;
            }
        }
        return "";
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Nullable
    public XBCNode getNode() {
        return node;
    }

    public void setNode(@Nullable XBCNode node) {
        this.node = node;

        XBCXUiService uiService = catalog.getCatalogService(XBCXUiService.class);

        items = new ArrayList<>();
        if (node != null) {
            for (XBCXPlugin plugin : ((List<XBCXPlugin>) pluginService.findPluginsForNode(node))) {
                long rowEditorsCount = uiService.getPlugUisCount(plugin, XBPlugUiType.ROW_EDITOR);
                long panelViewersCount = uiService.getPlugUisCount(plugin, XBPlugUiType.PANEL_VIEWER);
                long panelEditorsCount = uiService.getPlugUisCount(plugin, XBPlugUiType.PANEL_EDITOR);
                items.add(new PluginItemRecord(plugin, plugin.getPluginFile(), rowEditorsCount, panelViewersCount, panelEditorsCount));
            }
        }
    }

    public XBCXPlugin getItem(int rowIndex) {
        return items.get(rowIndex).plugin;
    }

    public void updateItem(int rowIndex, XBCXPlugin plugin, long rowEditorsCount, long panelViewersCount, long panelEditorsCount) {
        PluginItemRecord record = items.get(rowIndex);
        record.plugin = plugin;
        record.file = plugin.getPluginFile();
        record.fileName = record.file == null ? "" : record.file.getFilename();
        record.rowEditorsCount = rowEditorsCount;
        record.panelViewersCount = panelViewersCount;
        record.panelEditorsCount = panelEditorsCount;
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void addItem(XBCXPlugin plugin, XBCXFile file, long rowEditorsCount, long panelViewersCount, long panelEditorsCount) {
        items.add(new PluginItemRecord(plugin, file, rowEditorsCount, panelViewersCount, panelEditorsCount));
        fireTableDataChanged();
    }

    public XBCXPlugin removeItem(int rowIndex) {
        XBCXPlugin result = items.remove(rowIndex).plugin;
        fireTableDataChanged();
        return result;
    }

    public void setCatalog(XBCatalog catalog) {
        this.catalog = catalog;

        pluginService = catalog == null ? null : catalog.getCatalogService(XBCXPlugService.class);
    }

    @ParametersAreNonnullByDefault
    private static class PluginItemRecord {

        public XBCXPlugin plugin = null;
        public XBCXFile file;
        public String fileName = null;
        public long rowEditorsCount;
        public long panelViewersCount;
        public long panelEditorsCount;

        public PluginItemRecord(@Nullable XBCXPlugin plugin, XBCXFile file, long rowEditorsCount, long panelViewersCount, long panelEditorsCount) {
            this.plugin = plugin;
            this.file = file;
            this.fileName = file == null ? "" : file.getFilename();
            this.rowEditorsCount = rowEditorsCount;
            this.panelViewersCount = panelViewersCount;
            this.panelEditorsCount = panelEditorsCount;
        }
    }
}
