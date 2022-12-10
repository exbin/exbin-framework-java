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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for catalog specifications.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogSearchTableModel extends AbstractTableModel {

    private CatalogSearchTableItem searchConditions = new CatalogSearchTableItem();

    public CatalogSearchTableModel() {
        searchConditions = new CatalogSearchTableItem();
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return CatalogItemsTableModel.columnNames.length;
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return searchConditions.getName();
            }
            case 1: {
                return searchConditions.getStringId();
            }
            case 2: {
                return searchConditions.getType();
            }
            case 3: {
                return searchConditions.getDescription();
            }
        }
        return "";
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        return CatalogItemsTableModel.columnNames[columnIndex];
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return CatalogItemsTableModel.columnClasses[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                searchConditions.setName((String) aValue);
                break;
            }
            case 1: {
                searchConditions.setStringId((String) aValue);
                break;
            }
            case 2: {
                searchConditions.setType((String) aValue);
                break;
            }
            case 3: {
                searchConditions.setDescription((String) aValue);
                break;
            }
        }
    }

    public CatalogSearchTableItem getSearchConditions() {
        return searchConditions;
    }

    public void setSearchConditions(CatalogSearchTableItem searchConditions) {
        this.searchConditions = searchConditions;
    }

    @ParametersAreNonnullByDefault
    public static class CatalogSearchTableItem {

        private String name;
        private String description;
        private String stringId;
        private String type;

        public CatalogSearchTableItem() {
        }

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        public void setDescription(@Nullable String description) {
            this.description = description;
        }

        @Nullable
        public String getStringId() {
            return stringId;
        }

        public void setStringId(@Nullable String stringId) {
            this.stringId = stringId;
        }

        @Nullable
        public String getType() {
            return type;
        }

        public void setType(@Nullable String type) {
            this.type = type;
        }
    }
}
