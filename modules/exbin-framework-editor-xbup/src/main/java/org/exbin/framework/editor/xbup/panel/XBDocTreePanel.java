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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DropMode;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.viewer.DocumentItemSelectionListener;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Panel with document tree visualization.
 *
 * @version 0.2.1 2020/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBDocTreePanel extends javax.swing.JPanel {

    private XBApplication application;
    private XBTTreeDocument mainDoc;
    private final XBDocTreeModel mainDocModel;
    private XBDocTreeCellRenderer cellRenderer;

    private XBACatalog catalog;
    private XBUndoHandler undoHandler;
    private final List<ActionListener> updateEventList;

    private final List<DocumentItemSelectionListener> itemSelectionListeners = new ArrayList<>();
//    private boolean editEnabled;
//    private boolean addEnabled;
//    private final Clipboard clipboard;
//    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;

//    private Component lastFocusedComponent = null;
//    private final Map<String, ActionListener> actionListenerMap = new HashMap<>();
    public XBDocTreePanel() {
        super();
        mainDocModel = new XBDocTreeModel();
        cellRenderer = new XBDocTreeCellRenderer();

        initComponents();

        mainTree.setCellRenderer(cellRenderer);
        mainTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                XBTTreeNode selectedItem = getSelectedItem();
                itemSelectionListeners.forEach((listener) -> {
                    listener.itemSelected(selectedItem);
                });
            }
        });
//        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        updateEventList = new ArrayList<>();

//        mainTree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
//            updateItemStatus();
//        });
        /*clipboard.addFlavorListener(new FlavorListener() {
         @Override
         public void flavorsChanged(FlavorEvent e) {
         pasteAction.setEnabled(pasteAction.isEnabled());
         }
         });*/
//        actionListenerMap.put(DefaultEditorKit.cutAction, (ActionListener) (ActionEvent e) -> {
//            performCut();
//        });
//        actionListenerMap.put(DefaultEditorKit.copyAction, (ActionListener) (ActionEvent e) -> {
//            performCopy();
//        });
//        actionListenerMap.put(DefaultEditorKit.pasteAction, (ActionListener) (ActionEvent e) -> {
//            performPaste();
//        });
//        actionListenerMap.put(DefaultEditorKit.deleteNextCharAction, (ActionListener) (ActionEvent e) -> {
//            performDelete();
//        });
//        actionListenerMap.put("delete", (ActionListener) (ActionEvent e) -> {
//            performDelete();
//        });
        mainTree.setDragEnabled(true);
        mainTree.setDropMode(DropMode.USE_SELECTION);

        mainTree.setTransferHandler(new XBDocTreeTransferHandler(this));
//        mainTree.setDropTarget(new );
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setMainDoc(XBTTreeDocument mainDoc) {
        this.mainDoc = mainDoc;
        mainDocModel.setTreeDoc(mainDoc);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        cellRenderer.setCatalog(catalog);
        if (mainDoc != null) {
            mainDoc.setCatalog(catalog);
            mainDoc.processSpec();
        }
    }

    public void setUndoHandler(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    public void addTreeSelectionListener(TreeSelectionListener listener) {
        mainTree.getSelectionModel().addTreeSelectionListener(listener);
    }

//    /**
//     * Updating selected item available operations status, like add, edit,
//     * delete.
//     */
//    public void updateItemStatus() {
//        setEditEnabled(!mainTree.isSelectionEmpty());
//        updateUndoAvailable();
//        if (!editEnabled) {
//            setAddEnabled(mainDoc.getRootBlock() == null);
//        } else {
//            setAddEnabled(((XBTTreeNode) mainTree.getLastSelectedPathComponent()).getDataMode() == XBBlockDataMode.NODE_BLOCK);
//        }
//        for (Iterator it = updateEventList.iterator(); it.hasNext();) {
//            ((ActionListener) it.next()).actionPerformed(null);
//        }
//
//        if (clipboardActionsUpdateListener != null) {
//            clipboardActionsUpdateListener.stateChanged();
//        }
//
////        updateActionStatus(lastFocusedComponent);
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainScrollPane = new javax.swing.JScrollPane();
        mainTree = new javax.swing.JTree();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        mainScrollPane.setBorder(null);

        mainTree.setModel(mainDocModel);
        mainTree.setAutoscrolls(true);
        mainTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mainTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainTreeMouseReleased(evt);
            }
        });
        mainScrollPane.setViewportView(mainTree);

        add(mainScrollPane);
    }// </editor-fold>//GEN-END:initComponents

    private void mainTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMouseReleased
//        if (evt.isPopupTrigger()) {
//            mainTree.setSelectionPath(mainTree.getPathForLocation(evt.getX(), evt.getY()));
//            boolean availableItem = (mainTree.getLastSelectedPathComponent() != null);
//            setEditEnabled(availableItem);
//            boolean addPossible;
//            if (!availableItem) {
//                addPossible = mainDoc.getRootBlock() == null;
//            } else {
//                addPossible = ((XBTTreeNode) mainTree.getLastSelectedPathComponent()).getDataMode() == XBBlockDataMode.NODE_BLOCK;
//            }
//            setAddEnabled(addPossible);
//        }
    }//GEN-LAST:event_mainTreeMouseReleased

    private void mainTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMousePressed
//        if (evt.isPopupTrigger()) {
//            mainTree.setSelectionPath(mainTree.getPathForLocation(evt.getX(), evt.getY()));
//            boolean availableItem = (mainTree.getLastSelectedPathComponent() != null);
//            setEditEnabled(availableItem);
//            boolean addPossible;
//            if (!availableItem) {
//                addPossible = mainDoc.getRootBlock() == null;
//            } else {
//                addPossible = ((XBTTreeNode) mainTree.getLastSelectedPathComponent()).getDataMode() == XBBlockDataMode.NODE_BLOCK;
//            }
//            setAddEnabled(addPossible);
//        }
    }//GEN-LAST:event_mainTreeMousePressed

    public XBTTreeNode getSelectedItem() {
        return (XBTTreeNode) mainTree.getLastSelectedPathComponent();
    }

    public void reportStructureChange(XBTBlock block) {
        if (block == null) {
            mainDocModel.fireTreeChanged();
        } else {
            mainDocModel.fireTreeStructureChanged(block);
        }
    }

//    public boolean isEditEnabled() {
//        return editEnabled;
//    }
//
//    public boolean isAddEnabled() {
//        return addEnabled;
//    }
//    public boolean isPasteEnabled() {
//        return addEnabled && clipboard.isDataFlavorAvailable(XB_DATA_FLAVOR);
//    }
    public void addUpdateListener(ActionListener listener) {
        updateEventList.add(listener);
    }

    public void removeUpdateListener(ActionListener listener) {
        updateEventList.remove(listener);
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void performSelectAll() {
        mainTree.setSelectionRow(0);
    }

//    public void performUndo() {
//        try {
//            getUndoHandler().performUndo();
//            reportStructureChange(null);
//            updateItemStatus();
//        } catch (Exception ex) {
//            Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void performRedo() {
//        try {
//            getUndoHandler().performRedo();
//            reportStructureChange(null);
//            updateItemStatus();
//        } catch (Exception ex) {
//            Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new XBDocTreePanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JTree mainTree;
    // End of variables declaration//GEN-END:variables

//    public void setEditEnabled(boolean editEnabled) {
//        if (editEnabled != this.editEnabled) {
//            this.editEnabled = editEnabled;
//            firePropertyChange("editEnabled", !editEnabled, editEnabled);
//        }
//    }
//
//    public void setAddEnabled(boolean addEnabled) {
//        if (addEnabled != this.addEnabled) {
//            this.addEnabled = addEnabled;
//            firePropertyChange("addEnabled", !addEnabled, addEnabled);
//            firePropertyChange("pasteEnabled", !editEnabled, editEnabled);
//        }
//    }
//
//    public void updateUndoAvailable() {
//        firePropertyChange("undoAvailable", false, true);
//        firePropertyChange("redoAvailable", false, true);
//    }
    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        mainTree.setComponentPopupMenu(popupMenu);
    }

    boolean isSelection() {
        return getSelectedItem() != null;
    }

    public void addItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.add(listener);
    }

    public void removeItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.remove(listener);
    }

//    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
//        clipboardActionsUpdateListener = updateListener;
//    }
//    public boolean updateActionStatus(Component component) {
//        if (component == mainTree) {
//            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//
//            MainFrameManagement frameManagement = mainFrame.getMainFrameManagement();
//            lastFocusedComponent = component;
//            frameManagement.getCutAction().setEnabled(editEnabled);
//            frameManagement.getCopyAction().setEnabled(editEnabled);
//            frameManagement.getPasteAction().setEnabled(addEnabled && clipboard.isDataFlavorAvailable(xbFlavor));
//            frameManagement.getDeleteAction().setEnabled(editEnabled);
//            frameManagement.getSelectAllAction().setEnabled(false);
//
//            frameManagement.getUndoAction().setEnabled(treeUndo.canUndo());
//            frameManagement.getRedoAction().setEnabled(treeUndo.canRedo());
//
//            mainFrame.getItemAddAction().setEnabled(addEnabled);
//            mainFrame.getItemModifyAction().setEnabled(editEnabled);
//            mainFrame.getItemPropertiesAction().setEnabled(editEnabled);
//            mainFrame.getEditFindAction().setEnabled(false);
//            mainFrame.getEditFindAgainAction().setEnabled(false);
//            mainFrame.getEditReplaceAction().setEnabled(false);
//            mainFrame.getEditGotoAction().setEnabled(false);
//
//            return true;
//        }
//
//        lastFocusedComponent = null;
//        return false;
//    }
//    @Override
//    public void releaseActionStatus() {
//        mainFrame.getItemAddAction().setEnabled(false);
//        mainFrame.getItemModifyAction().setEnabled(false);
//        mainFrame.getItemPropertiesAction().setEnabled(false);
//        mainFrame.getEditFindAction().setEnabled(false);
//        mainFrame.getEditFindAgainAction().setEnabled(false);
//        mainFrame.getEditReplaceAction().setEnabled(false);
//        mainFrame.getEditGotoAction().setEnabled(false);
//    }
//
//    @Override
//    public boolean performAction(String eventName, ActionEvent event) {
//        if (lastFocusedComponent != null) {
//            ActionListener actionListener = actionListenerMap.get(eventName);
//            if (actionListener != null) {
//                actionListener.actionPerformed(event);
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public Object getUndoHandle() {
//        return treeUndo;
//    }
}
