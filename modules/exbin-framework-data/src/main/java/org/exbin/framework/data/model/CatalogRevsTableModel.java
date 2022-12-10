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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.base.XBCRev;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCXDescService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;

/**
 * Table model for catalog revisions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogRevsTableModel extends AbstractTableModel {

    private XBCatalog catalog;
    private XBCSpec spec;

    private final String[] columnNames = new String[]{"XBIndex", "Name", "Description", "XBLimit"};
    private final Class[] classes = new Class[]{
        java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class
    };
    private final List<CatalogRevsTableItem> revs = new ArrayList<>();

    public CatalogRevsTableModel() {
    }

    @Override
    public int getRowCount() {
        return revs.size();
    }

    @Override
    public int getColumnCount() {
        return classes.length;
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return revs.get(rowIndex).getXbIndex();
            case 1:
                return revs.get(rowIndex).getName();
            case 2:
                return revs.get(rowIndex).getDescription();
            case 3:
                return revs.get(rowIndex).getLimit();
        }

        return null;

    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return classes[columnIndex];
    }

    public XBCSpec getSpec() {
        return spec;
    }

    public void setSpec(XBCSpec spec) {
        this.spec = spec;
        reloadItems();
    }

    private void reloadItems() {
        revs.clear();
        if (spec != null && catalog != null) {
            XBCRevService revService = catalog.getCatalogService(XBCRevService.class);
            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);
            XBCXDescService descService = catalog.getCatalogService(XBCXDescService.class);
            List<XBCRev> revsList = revService.getRevs(spec);
            for (XBCRev rev : revsList) {
                CatalogRevsTableItem item = new CatalogRevsTableItem();
                item.setRev(rev);
                item.setXbIndex(rev.getXBIndex());
                item.setLimit(rev.getXBLimit());
                item.setName(nameService.getDefaultText(rev));
                item.setDescription(descService.getDefaultText(rev));
                revs.add(item);
            }
        }

        fireTableDataChanged();
    }

    public void setCatalog(XBCatalog catalog) {
        this.catalog = catalog;
        reloadItems();
    }

    public CatalogRevsTableItem getRowItem(int index) {
        return revs.get(index);
    }

    public List<CatalogRevsTableItem> getRevs() {
        return revs;
    }

    @Nullable
    public Long getRevisionForIndex(Long xbIndex) {
        long limitSum = 0;

        for (int revision = 0; revision < revs.size(); revision++) {
            CatalogRevsTableItem revItem = revs.get(revision);

            if (xbIndex < limitSum + revItem.getLimit()) {
                return (long) revision;
            }

            limitSum += revItem.getLimit();
        }

        return null;
    }
}
