/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.xbup.panel;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
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
        return treeDoc == null ? null : treeDoc.getRootBlock();
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
        return ((XBTBlock) node).getChildAt(0) == null;
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
