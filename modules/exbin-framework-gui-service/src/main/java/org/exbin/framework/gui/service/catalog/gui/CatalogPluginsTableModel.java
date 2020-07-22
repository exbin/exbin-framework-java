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

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.catalog.entity.XBENode;
import org.exbin.xbup.catalog.entity.XBEXFile;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCXFile;
import org.exbin.xbup.core.catalog.base.XBCXPlugin;
import org.exbin.xbup.core.catalog.base.service.XBCXPlugService;

/**
 * Table model for catalog plugins.
 *
 * @version 0.2.1 2020/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogPluginsTableModel extends AbstractTableModel {

    private XBCatalog catalog;
    private XBCXPlugService pluginService;
    private XBCNode node;

    private final String[] columnNames = new String[]{"Index", "Filename", "Line Editors", "Pane Editors"};
    private final Class[] columnClasses = new Class[]{
        java.lang.Long.class, java.lang.String.class, java.lang.Long.class, java.lang.Long.class
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
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return items.get(rowIndex).index;
            }
            case 1: {
                return items.get(rowIndex).fileName;
            }
            case 2: {
                return items.get(rowIndex).lineEditors;
            }
            case 3: {
                return items.get(rowIndex).paneEditors;
            }
        }
        return "";
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public XBCNode getNode() {
        return node;
    }

    public void setNode(XBCNode node) {
        this.node = node;
        items = new ArrayList<>();
        if (node != null) {
            for (XBCXPlugin plugin : ((List<XBCXPlugin>) pluginService.findPluginsForNode(node))) {
                // TODO items.add(new PluginItemRecord(plugin));
            }
        }
    }

    public XBCXFile getItem(int rowIndex) {
        return items.get(rowIndex).file;
    }

    public void addItem(String fileName, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
//        items.add(new PluginItemRecord(fileName, data));
//        fireTableDataChanged();
    }

    public XBCXFile removeItem(int rowIndex) {
        XBCXFile result = items.remove(rowIndex).file;
        fireTableDataChanged();
        return result;
    }

    public void setCatalog(XBCatalog catalog) {
        this.catalog = catalog;

        pluginService = catalog == null ? null : catalog.getCatalogService(XBCXPlugService.class);
    }

    public void persist() {
        for (PluginItemRecord itemRecord : items) {
//            if (itemRecord.file == null) {
//                XBEXFile file = new XBEXFile();
//                file.setNode((XBENode) node);
//                file.setFilename(itemRecord.fileName);
//            }
//
//            if (itemRecord.modifiedData != null) {
//                ((XBEXFile) itemRecord.file).setContent(itemRecord.modifiedData);
//                pluginService.persistItem(itemRecord.file);
//            }
        }
    }

    public void setItemData(int rowIndex, byte[] fileContent) {
//        items.get(rowIndex).modifiedData = fileContent;
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    private class PluginItemRecord {

        public long index;
        public XBCXFile file = null;
        public String fileName = null;
        public long lineEditors;
        public long paneEditors;

        public PluginItemRecord(long index, String fileName, long lineEditors, long paneEditors) {
            this.index = index;
            this.fileName = fileName;
            this.lineEditors = lineEditors;
            this.paneEditors = paneEditors;
        }
    }
}
