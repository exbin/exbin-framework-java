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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Parameters list table model for item editing.
 *
 * @version 0.2.0 2016/02/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBPropertyTableModel extends AbstractTableModel {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(XBPropertyTablePanel.class);
    private List<XBPropertyTableItem> parameters;

    private final String[] columnNames;
    private Class[] columnTypes = new Class[]{
        java.lang.String.class, java.lang.Object.class
    };
    private final boolean[] columnsEditable = new boolean[]{false, true};

    public XBPropertyTableModel() {
        columnNames = new String[]{"Property", "Value"};
        parameters = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return parameters.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getTypes()[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnsEditable[columnIndex];
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return getRow(rowIndex).getValueName();
            case 1:
                return null;
            default:
                return "";
        }
    }

    public XBPropertyTableItem getRow(int rowIndex) {
        return parameters.get(rowIndex);
    }

    public void removeRow(int rowIndex) {
        parameters.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void addRow(XBPropertyTableItem rowData) {
        parameters.add(rowData);
        fireTableRowsInserted(parameters.size() - 1, parameters.size() - 1);
    }

    @Nonnull
    public List<XBPropertyTableItem> getParameters() {
        return parameters;
    }

    public void setParameters(List<XBPropertyTableItem> attributes) {
        this.parameters = attributes;
    }

    @Nonnull
    public Class[] getTypes() {
        return columnTypes;
    }

    public void setTypes(Class[] types) {
        this.columnTypes = types;
    }
}
