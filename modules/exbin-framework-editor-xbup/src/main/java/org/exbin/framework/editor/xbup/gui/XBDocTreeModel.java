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
package org.exbin.framework.editor.xbup.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.XBTDefaultBlock;
import org.exbin.xbup.parser_tree.XBTTreeDocument;

/**
 * Document tree model for XBUP document tree.
 *
 * @version 0.2.1 2020/03/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBDocTreeModel implements TreeModel {

    private XBTTreeDocument treeDoc = null;
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public XBDocTreeModel() {
        super();
    }

    @Nullable
    @Override
    public Object getRoot() {
        return treeDoc == null ? null : treeDoc.getRoot();
    }

    @Nullable
    @Override
    public Object getChild(Object parent, int index) {
        return ((XBTBlock) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((XBTBlock) parent).getChildrenCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((XBTBlock) node).getDataMode() == XBBlockDataMode.DATA_BLOCK;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return XBTDefaultBlock.getChildIndexOf((XBTBlock) parent, (XBTBlock) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        treeModelListeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        treeModelListeners.remove(listener);
    }

    public void setTreeDoc(XBTTreeDocument treeDoc) {
        this.treeDoc = treeDoc;
        fireTreeChanged();
    }

    /**
     * Performs structure change event.
     *
     * The only event raised by this model is TreeStructureChanged with the root
     * as path, i.e. the whole tree has changed.
     *
     * @param oldRoot old root node
     */
    public void fireTreeStructureChanged(XBTBlock oldRoot) {
        int listenersCount = treeModelListeners.size();
        TreeModelEvent event = new TreeModelEvent(this, new Object[]{oldRoot});
        for (int i = 0; i < listenersCount; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(event);
        }
    }

    public void fireTreeChanged() {
        int listenersCount = treeModelListeners.size();
        TreeModelEvent event = new TreeModelEvent(this, new Object[]{this});
        for (int i = 0; i < listenersCount; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(event);
        }
    }
}
