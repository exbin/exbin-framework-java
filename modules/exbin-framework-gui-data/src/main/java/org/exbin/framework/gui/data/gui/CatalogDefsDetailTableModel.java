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
package org.exbin.framework.gui.data.gui;

import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;

/**
 * Table model for catalog definition bindings.
 *
 * @version 0.2.0 2016/02/01
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogDefsDetailTableModel extends AbstractTableModel {

    private CatalogDefsTableItem item = null;

    private final String[] columnNames = new String[]{"Property", "Value"};
    private final String[] rowProperties = new String[]{"Name", "Description", "Type", "Type Revision", "Operation", "StringId"};
    private final Class[] columnClasses = new Class[]{
        java.lang.String.class, java.lang.Object.class
    };

    public CatalogDefsDetailTableModel() {
    }

    @Override
    public int getRowCount() {
        if (item == null) {
            return 0;
        }
        return 6;
    }

    @Override
    public int getColumnCount() {
        return columnClasses.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (item == null) {
            return null;
        }

        switch (columnIndex) {
            case 0:
                return rowProperties[rowIndex];
            case 1: {
                switch (rowIndex) {
                    case 0:
                        return item.getName();
                    case 1:
                        return item.getDescription();
                    case 2:
                        return item.getType();
                    case 3:
                        return item.getTargetRevision();
                    case 4:
                        return item.getOperation();
                    case 5:
                        return item.getStringId();
                }

            }

            default:
                return "";
        }
    }

    public CatalogDefsTableItem getItem() {
        return item;
    }

    public void setItem(CatalogDefsTableItem item) {
        this.item = item;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public String getOperation(XBCSpecDef specDef) {
//        CatalogDefOperationType operation;
//        if (specDef instanceof XBCBlockJoin) {
//            operation = specDef.getTarget() == null
//                    ? CatalogDefOperationType.ATTRIBUTE : CatalogDefOperationType.JOIN;
//        } else if (specDef instanceof XBCBlockCons) {
//            operation = specDef.getTarget() == null
//                    ? CatalogDefOperationType.ANY : CatalogDefOperationType.CONSIST;
//        } else if (specDef instanceof XBCBlockListJoin) {
//            operation = specDef.getTarget() == null
//                    ? CatalogDefOperationType.ATTRIBUTE_LIST : CatalogDefOperationType.JOIN_LIST;
//        } else if (specDef instanceof XBCBlockListCons) {
//            operation = specDef.getTarget() == null
//                    ? CatalogDefOperationType.ANY_LIST : CatalogDefOperationType.CONSIST_LIST;
//        } else if (specDef instanceof XBCJoinDef) {
//            operation = CatalogDefOperationType.JOIN;
//        } else if (specDef instanceof XBCConsDef) {
//            operation = CatalogDefOperationType.CONSIST;
//        } else {
            return "Unknown";
//        }

//        return operation.getCaption();
    }
}
