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
import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.catalog.entity.XBENode;
import org.exbin.xbup.catalog.entity.XBEXFile;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCXFile;
import org.exbin.xbup.core.catalog.base.service.XBCXFileService;

/**
 * Table model for catalog node files.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class CatalogFilesTableModel extends AbstractTableModel {

    private XBCatalog catalog;
    private XBCXFileService fileService;
    private XBCNode node;

    private final String[] columnNames = new String[]{"Filename", "Size"};
    private final Class[] columnClasses = new Class[]{
        java.lang.String.class, java.lang.Long.class
    };

    private List<FileItemRecord> items = new ArrayList<>();

    public CatalogFilesTableModel() {
        node = null;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return items.get(rowIndex).fileName;
            }
            case 1: {
                byte[] data = items.get(rowIndex).modifiedData;
                if (data == null) {
                    data = ((XBCXFile) items.get(rowIndex).file).getContent();
                }

                return data == null ? 0 : data.length;
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
            for (XBCXFile file : ((List<XBCXFile>) fileService.findFilesForNode(node))) {
                items.add(new FileItemRecord(file));
            }
        }
    }

    public XBCXFile getItem(int rowIndex) {
        return items.get(rowIndex).file;
    }

    public void addItem(String fileName, byte[] data) {
        items.add(new FileItemRecord(fileName, data));
        fireTableDataChanged();
    }

    public XBCXFile removeItem(int rowIndex) {
        XBCXFile result = items.remove(rowIndex).file;
        fireTableDataChanged();
        return result;
    }

    public void setCatalog(XBCatalog catalog) {
        this.catalog = catalog;

        fileService = catalog == null ? null : catalog.getCatalogService(XBCXFileService.class);
    }

    public void persist() {
        for (FileItemRecord itemRecord : items) {
            if (itemRecord.file == null) {
                XBEXFile file = new XBEXFile();
                file.setNode((XBENode) node);
                file.setFilename(itemRecord.fileName);
                itemRecord.file = file;
            }

            if (itemRecord.modifiedData != null) {
                ((XBEXFile) itemRecord.file).setContent(itemRecord.modifiedData);
                fileService.persistItem(itemRecord.file);
            }
        }
    }

    public void setItemData(int rowIndex, byte[] fileContent) {
        items.get(rowIndex).modifiedData = fileContent;
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setFileName(int selectedRow, String fileName) {
        items.get(selectedRow).fileName = fileName;
        fireTableRowsUpdated(selectedRow, selectedRow);
    }

    private class FileItemRecord {

        public XBCXFile file = null;
        public String fileName = null;
        public byte[] modifiedData = null;

        public FileItemRecord(String fileName, byte[] data) {
            this.fileName = fileName;
            modifiedData = data;
        }

        public FileItemRecord(XBCXFile file) {
            this.file = file;
            fileName = file.getFilename();
        }
    }
}
