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
import static org.exbin.framework.gui.service.catalog.panel.CatalogSpecItemType.BLOCK;
import static org.exbin.framework.gui.service.catalog.panel.CatalogSpecItemType.FORMAT;
import static org.exbin.framework.gui.service.catalog.panel.CatalogSpecItemType.GROUP;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.service.XBCNodeService;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;

/**
 * Table Model for Catalog Tree.
 *
 * @version 0.1.24 2014/12/12
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogSelectSpecTreeModel implements TreeModel {

    private XBCNodeService nodeService = null;
    private XBCSpecService specService = null;
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();
    private final CatalogSpecItemType specType;

    public CatalogSelectSpecTreeModel(XBACatalog catalog, CatalogSpecItemType specType) {
        this.specType = specType;

        nodeService = catalog == null ? null : catalog.getCatalogService(XBCNodeService.class);
        specService = catalog == null ? null : catalog.getCatalogService(XBCSpecService.class);
    }

    @Override
    public Object getRoot() {
        return nodeService == null ? null : nodeService.getRootNode();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == null) {
            return null;
        }
        long subNodesCount = nodeService.getSubNodesCount((XBCNode) parent);
        if (index < subNodesCount) {
            return nodeService.getSubNodeSeq(((XBCNode) parent), index);
        }

        switch (specType) {
            case BLOCK: {
                return specService.getBlockSpec((XBCNode) parent, index - subNodesCount);
            }
            case GROUP: {
                return specService.getGroupSpec((XBCNode) parent, index - subNodesCount);
            }
            case FORMAT: {
                return specService.getFormatSpec((XBCNode) parent, index - subNodesCount);
            }
        }

        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == null) {
            throw new NullPointerException("No parent");
        }
        int childrenCount = (int) nodeService.getSubNodesSeq(((XBCNode) parent));
        switch (specType) {
            case BLOCK: {
                childrenCount += specService.getBlockSpecsCount((XBCNode) parent);
                break;
            }
            case GROUP: {
                childrenCount += specService.getGroupSpecsCount((XBCNode) parent);
                break;
            }
            case FORMAT: {
                childrenCount += specService.getFormatSpecsCount((XBCNode) parent);
                break;
            }
        }
        return childrenCount;
    }

    @Override
    public boolean isLeaf(Object node) {
        return !(node instanceof XBCNode);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        // TODO: optimalization later
        int subNodeIndex = nodeService.getSubNodes(((XBCNode) parent)).indexOf(child);

        if (subNodeIndex >= 0) {
            return subNodeIndex;
        }

        int childrenCount = (int) nodeService.getSubNodesSeq(((XBCNode) parent));
        switch (specType) {
            case BLOCK: {
                return specService.getBlockSpecs((XBCNode) parent).indexOf(child) + childrenCount;
            }
            case GROUP: {
                return specService.getGroupSpecs((XBCNode) parent).indexOf(child) + childrenCount;
            }
            case FORMAT: {
                return specService.getFormatSpecs((XBCNode) parent).indexOf(child) + childrenCount;
            }
        }

        return -1;
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
