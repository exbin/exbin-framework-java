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
package org.exbin.framework.gui.service.catalog.panel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Table Model for Catalog Tree
 *
 * @version 0.1.19 2010/05/30
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogTypesTreeModel implements TreeModel {

    private final XBACatalog catalog;
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public CatalogTypesTreeModel(XBACatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public Object getRoot() {
        throw new UnsupportedOperationException("Not yet implemented");
//        return catalog.getTypeManager().getRootNode();
    }

    @Override
    public Object getChild(Object parent, int index) {
        throw new UnsupportedOperationException("Not yet implemented");
        /*        if (parent==null) return null;
         return catalog.getTypeManager().getSubNode(((XBCType) parent),index); */
    }

    @Override
    public int getChildCount(Object parent) {
        throw new UnsupportedOperationException("Not yet implemented");
        /*        if (parent==null) throw new NullPointerException("No parent");
         return (int) catalog.getTypeManager().getSubNodesCount(((XBCType) parent)); */
    }

    @Override
    public boolean isLeaf(Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        // TODO: optimalization later
        throw new UnsupportedOperationException("Not yet implemented");
//        return catalog.getTypeManager().getSubNodes(((XBCType) parent)).indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener tml) {
        treeModelListeners.add(tml);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener tml) {
        treeModelListeners.remove(tml);
    }
}
