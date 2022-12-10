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
package org.exbin.framework.data.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.exbin.framework.data.TableDataSource;
import org.exbin.framework.data.stub.DataStub;
import org.exbin.xbup.client.XBCatalogServiceClient;
import org.exbin.xbup.core.remote.XBServiceClient;

/**
 * Table model.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableModelSource implements TableModel {

    private final List<TableModelListener> listeners = new ArrayList<>();
    private final TableDataSource dataSource;
    private final String tableSourceId;
    private final XBServiceClient service;

    public TableModelSource(String tableSourceId, XBServiceClient service) {
        this.service = service;
        this.tableSourceId = tableSourceId;

        DataStub dataStub = new DataStub((XBCatalogServiceClient) service);
        dataSource = dataStub.getTableDataSource(tableSourceId);
    }

    @Override
    public int getRowCount() {
        return dataSource.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return dataSource.getColumns().size();
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        return dataSource.getColumns().get(columnIndex).getName();
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return dataSource.getColumns().get(columnIndex).getValueClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataSource.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        dataSource.setValueAt(value, rowIndex, columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
