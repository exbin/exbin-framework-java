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
 * @version 0.1.24 2015/01/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ParametersTableModel extends AbstractTableModel {

    private final ResourceBundle resourceBundle;
    private List<ParametersTableItem> parameters;

    private final String[] columnNames;
    private Class[] columnTypes = new Class[]{
        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
    };
    private final boolean[] columnsEditable = new boolean[]{false, false, false, true};

    public ParametersTableModel() {
        resourceBundle = LanguageUtils.getResourceBundleByClass(ModifyBlockPanel.class);
        columnNames = new String[]{
            resourceBundle.getString("parametersTableModel.itemOrder"),
            resourceBundle.getString("parametersTableModel.itemName"), 
            resourceBundle.getString("parametersTableModel.itemType"), 
            resourceBundle.getString("parametersTableModel.itemValue")
        };
        parameters = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return parameters.size();
    }

    public ParametersTableItem getRow(int index) {
        return parameters.get(index);
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rowIndex;
            case 1:
                return getParameter(rowIndex).getValueName();
            case 2:
                return getParameter(rowIndex).getTypeName();
            case 3:
                return "";
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < getRowCount()) {
            if (columnIndex == 3) {
                // ((UBNat32) parameters.get(rowIndex)).setValue((Integer) aValue);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Nonnull
    public List<ParametersTableItem> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParametersTableItem> attributes) {
        this.parameters = attributes;
    }

    @Nonnull
    public Class[] getTypes() {
        return columnTypes;
    }

    public void setTypes(Class[] types) {
        this.columnTypes = types;
    }

    @Nullable
    public ParametersTableItem getParameter(int index) {
        if (index >= parameters.size()) {
            return null;
        }

        return parameters.get(index);
    }

    public void clear() {
        parameters.clear();
    }

    public void addRow(ParametersTableItem item) {
        parameters.add(item);
    }

    public boolean isEmpty() {
        return parameters == null || parameters.isEmpty();
    }
}
