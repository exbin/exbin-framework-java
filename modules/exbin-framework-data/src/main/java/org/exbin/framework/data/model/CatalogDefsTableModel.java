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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockCons;
import org.exbin.xbup.core.catalog.base.XBCBlockJoin;
import org.exbin.xbup.core.catalog.base.XBCBlockListCons;
import org.exbin.xbup.core.catalog.base.XBCBlockListJoin;
import org.exbin.xbup.core.catalog.base.XBCConsDef;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCJoinDef;
import org.exbin.xbup.core.catalog.base.XBCRev;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;
import org.exbin.xbup.core.catalog.base.XBCXName;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.catalog.base.service.XBCXDescService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.catalog.base.service.XBCXStriService;

/**
 * Table model for catalog definition bindings.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogDefsTableModel extends AbstractTableModel {

    private XBACatalog catalog;
    private XBCSpec spec = null;
    private XBCItem catalogItem = null;
    private CatalogRevsTableModel revsModel = null;

    private final List<CatalogDefsTableItem> items = new ArrayList<>();

    private final String[] columnNames = new String[]{"#", "Name", "Type", "Revision"};
    private final Class[] columnClasses = new Class[]{
        java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class
    };

    public CatalogDefsTableModel() {
    }

    @Override
    public int getRowCount() {
        if (spec == null) {
            return 0;
        }
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnClasses.length;
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (spec == null) {
            return null;
        }
        CatalogDefsTableItem item = items.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return item.getXbIndex();
            case 1:
                return item.getName();
            case 2:
                return item.getType();
            case 3:
                return item.getRevision();

            default:
                return "";
        }
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Nonnull
    public List<CatalogDefsTableItem> getDefs() {
        return items;
    }

    public void setCatalogItem(XBCItem item) {
        this.catalogItem = item;
        items.clear();
        if (item instanceof XBCSpec) {
            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);
            XBCXDescService descService = catalog.getCatalogService(XBCXDescService.class);
            XBCXStriService striService = catalog.getCatalogService(XBCXStriService.class);
            XBCSpecService specService = catalog.getCatalogService(XBCSpecService.class);

            spec = (XBCSpec) item;
            List<XBCSpecDef> specDefs = specService.getSpecDefs(spec);
            for (XBCSpecDef specDef : specDefs) {
                CatalogDefsTableItem tableItem = new CatalogDefsTableItem();
                tableItem.setSpecDef(specDef);
                tableItem.setXbIndex(specDef.getXBIndex());
                tableItem.setOperation(getOperation(specDef));
                tableItem.setDefType(specDef.getType());

                tableItem.setType("");
                Optional<XBCRev> targetRev = specDef.getTargetRev();
                if (targetRev.isPresent()) {
                    tableItem.setTarget(targetRev.get());
                    XBCSpec targetSpec = (XBCSpec) targetRev.get().getParent();
                    XBCXName name = nameService.getDefaultItemName(targetSpec);
                    if (name != null) {
                        tableItem.setType(name.getText());
                    }
                }

                tableItem.setName(nameService.getDefaultText(specDef));
                tableItem.setDescription(descService.getDefaultText(specDef));
                tableItem.setStringId(striService.getItemStringIdText(specDef));

                items.add(tableItem);
            }

            updateDefRevisions();
        }

        fireTableDataChanged();
    }

    @Nonnull
    public String getOperation(XBCSpecDef specDef) {
        CatalogDefOperationType operation;
        if (specDef instanceof XBCBlockJoin) {
            operation = specDef.getTargetRev().isPresent()
                    ? CatalogDefOperationType.JOIN : CatalogDefOperationType.ATTRIBUTE;
        } else if (specDef instanceof XBCBlockCons) {
            operation = specDef.getTargetRev().isPresent()
                    ? CatalogDefOperationType.CONSIST : CatalogDefOperationType.ANY;
        } else if (specDef instanceof XBCBlockListJoin) {
            operation = specDef.getTargetRev().isPresent()
                    ? CatalogDefOperationType.JOIN_LIST : CatalogDefOperationType.ATTRIBUTE_LIST;
        } else if (specDef instanceof XBCBlockListCons) {
            operation = specDef.getTargetRev().isPresent()
                    ? CatalogDefOperationType.CONSIST_LIST : CatalogDefOperationType.ANY_LIST;
        } else if (specDef instanceof XBCJoinDef) {
            operation = CatalogDefOperationType.JOIN;
        } else if (specDef instanceof XBCConsDef) {
            operation = CatalogDefOperationType.CONSIST;
        } else {
            return "Unknown";
        }

        return operation.getCaption();
    }

    public CatalogDefsTableItem getRowItem(int rowIndex) {
        return items.get(rowIndex);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        if (catalogItem != null) {
            setCatalogItem(catalogItem);
        }
    }

    public void addDefs(CatalogDefsTableItem defItem) {
        items.add(defItem);
        defItem.setRevision(revsModel.getRevisionForIndex(defItem.getXbIndex()));
    }

    public void removeItem(XBCSpecDef specDef) {
        for (int i = 0; i < items.size(); i++) {
            CatalogDefsTableItem item = items.get(i);
            if (item.getSpecDef() == specDef) {
                items.remove(item);
                break;
            }
        }
    }

    public void moveItemDown(int itemPos) {
        CatalogDefsTableItem item = items.get(itemPos);
        CatalogDefsTableItem nextItem = items.get(itemPos + 1);
        Long prevIndex = item.getXbIndex();
        item.setXbIndex(nextItem.getXbIndex());
        nextItem.setXbIndex(prevIndex);

        if (revsModel != null) {
            item.setRevision(revsModel.getRevisionForIndex(item.getXbIndex()));
            nextItem.setRevision(revsModel.getRevisionForIndex(nextItem.getXbIndex()));
        }

        items.remove(item);
        items.add(itemPos + 1, item);
    }

    public void setRevsModel(CatalogRevsTableModel revsModel) {
        this.revsModel = revsModel;

        if (catalogItem != null) {
            setCatalogItem(catalogItem);
        }
    }

    public void updateDefRevisions() {
        for (CatalogDefsTableItem tableItem : items) {
            if (revsModel != null) {
                tableItem.setRevision(revsModel.getRevisionForIndex(tableItem.getXbIndex()));
            } else {
                tableItem.setRevision(null);
            }
        }

        fireTableDataChanged();
    }
}
