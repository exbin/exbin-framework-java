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
package org.exbin.framework.action.manager.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;

/**
 * Key map table model.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class KeyMapTableModel extends AbstractTableModel {

    private List<KeyMapRecord> records = new ArrayList<>();

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KeyMapRecord record = records.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return record.getName();
            case 1:
                return "";
            case 2:
                return "";
        }

        return null;
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Nonnull
    public List<KeyMapRecord> getRecords() {
        return records;
    }

    public void setRecords(List<KeyMapRecord> records) {
        this.records = records;
    }
}
