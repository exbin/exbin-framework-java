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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.core.parser.token.XBAttribute;

/**
 * Attributes list table model for item editing.
 *
 * @version 0.2.1 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class AttributesTableModel extends AbstractTableModel {

    private final ResourceBundle resourceBundle;
    private List<XBAttribute> attributes;
    private ChangeListener changeListener = null;

    private final String[] columnNames;
    private Class[] columnTypes = new Class[]{
        java.lang.Integer.class, java.lang.Integer.class
    };
    private final boolean[] columnsEditable = new boolean[]{false, true};

    public AttributesTableModel() {
        resourceBundle = LanguageUtils.getResourceBundleByClass(ModifyBlockPanel.class);
        columnNames = new String[]{resourceBundle.getString("attributesTableModel.itemOrder"), resourceBundle.getString("attributesTableModel.itemValue")};
        attributes = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return attributes.size();
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
        if (columnIndex == 1) {
            return getAttribs().get(rowIndex).convertToNatural().getInt();
        } else {
            return rowIndex;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < getRowCount()) {
            if (columnIndex == 1) {
                attributes.get(rowIndex).convertToNatural().setValue((Integer) aValue);
                fireDataChanged();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public List<XBAttribute> getAttribs() {
        return attributes;
    }

    public void setAttribs(List<XBAttribute> attributes) {
        this.attributes = attributes;
        fireTableDataChanged();
    }

    public Class[] getTypes() {
        return columnTypes;
    }

    public void setTypes(Class[] types) {
        this.columnTypes = types;
    }

    public int getAttribute(int index) {
        if (index >= attributes.size()) {
            return 0;
        }

        XBAttribute attribute = attributes.get(index);
        return attribute != null ? attribute.getNaturalInt() : 0;
    }

    public void fireDataChanged() {
        if (changeListener != null) {
            changeListener.valueChanged();
        }
    }

    public void attachChangeListener(ChangeListener listener) {
        changeListener = listener;
    }

    public interface ChangeListener {

        void valueChanged();
    }
}