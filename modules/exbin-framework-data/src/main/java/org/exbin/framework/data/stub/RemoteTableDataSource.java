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
package org.exbin.framework.data.stub;

import java.util.ArrayList;
import java.util.List;
import org.exbin.framework.data.TableDataRow;
import org.exbin.framework.data.TableDataSource;

/**
 * Remote table model data source.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class RemoteTableDataSource implements TableDataSource {

    public static final int ROWS_PER_REQUEST = 7;

    private final String tableSourceId;
    private final DataStub dataStub;
    private List<ColumnDefinition> columDefinition = null;
    private final List<RowsPageCache> pageCache = new ArrayList<>();

    public RemoteTableDataSource(DataStub dataStub, String tableSourceId) {
        this.dataStub = dataStub;
        this.tableSourceId = tableSourceId;
    }

    @Override
    public List<ColumnDefinition> getColumns() {
        if (columDefinition == null) {
            columDefinition = dataStub.getColumDefinition(tableSourceId);
        }

        return columDefinition;
    }

    @Override
    public int getRowCount() {
        return dataStub.getTableRowCount(tableSourceId);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int pageIndex = rowIndex / ROWS_PER_REQUEST;
        int pageOffset = rowIndex % ROWS_PER_REQUEST;

        List<TableDataRow> tableRows = dataStub.getTableRows(tableSourceId, rowIndex, 1);
        TableDataRow tableRow = tableRows.get(0);
        return tableRow.getRowData()[columnIndex];
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private RowsPageCache getPageCache(int pageIndex) {
        if (pageIndex < pageCache.size()) {
            return pageCache.get(pageIndex);
        }

        return null;
    }

    private RowsPageCache getOrLoadPage(int pageIndex) {
        RowsPageCache page = getPageCache(pageIndex);
        if (page == null) {
            List<TableDataRow> tableRows = dataStub.getTableRows(tableSourceId, pageIndex * ROWS_PER_REQUEST, ROWS_PER_REQUEST);
            page = new RowsPageCache();
            page.pageData = tableRows;
            if (pageCache.size() < pageIndex) {
                int cacheSize = pageCache.size();
                for (int i = cacheSize; i < pageIndex; i++) {
                    pageCache.add(null);
                }
                pageCache.add(page);
            }
        }

        return page;
    }

    private class RowsPageCache {

        List<TableDataRow> pageData;
    }
}
