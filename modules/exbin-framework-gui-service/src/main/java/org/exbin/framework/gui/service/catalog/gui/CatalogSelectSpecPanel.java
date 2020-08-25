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
package org.exbin.framework.gui.service.catalog.gui;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.XBCXName;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;

/**
 * XBManager catalog specification selection panel.
 *
 * @version 0.2.1 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogSelectSpecPanel extends javax.swing.JPanel {

    private XBCXNameService nameService = null;
    private CatalogSelectSpecTreeModel treeModel;
    private SelectionListener selectionListener = null;
    private XBCItem selectedItem;
    private final CatalogItemType specType;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CatalogSelectSpecPanel.class);

    public CatalogSelectSpecPanel(final CatalogItemType specType) {
        this.specType = specType;
        treeModel = new CatalogSelectSpecTreeModel(null, specType);
        selectedItem = null;

        initComponents();

        specSelectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        specSelectTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultTreeCellRenderer retValue = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof XBCNode) {
                    XBCNode node = (XBCNode) value;
                    if (nameService != null) {
                        XBCXName name = nameService.getDefaultItemName(node);
                        if (name == null) {
                            retValue.setText("unknown name");
                        } else {
                            retValue.setText(name.getText());
                        }
                    } else {
                        retValue.setText("node");
                    }
                } else if (value instanceof XBCSpec) {
                    XBCSpec spec = (XBCSpec) value;
                    if (nameService != null) {
                        XBCXName name = nameService.getDefaultItemName(spec);
                        if (name == null) {
                            retValue.setText("unknown name");
                        } else {
                            retValue.setText(name.getText());
                        }
                    } else {
                        retValue.setText("node");
                    }
                }

                return retValue;
            }
        });

        specSelectTree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
            XBCItem item = (XBCItem) specSelectTree.getLastSelectedPathComponent();
            if ((item instanceof XBCNode) && (specType != CatalogItemType.NODE)) {
                item = null;
            }
            if (item == null) {
                if (selectedItem != null) {
                    selectedItem = null;
                    if (selectionListener != null) {
                        selectionListener.selectedItem(selectedItem);
                    }
                }
            } else {
                if (selectedItem != item) {
                    selectedItem = item;
                    if (selectionListener != null) {
                        selectionListener.selectedItem(selectedItem);
                    }
                }
            }
        });
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setCatalog(XBACatalog catalog) {
        treeModel = new CatalogSelectSpecTreeModel(catalog, specType);
        specSelectTree.setModel(treeModel);
        nameService = catalog == null ? null : (XBCXNameService) catalog.getCatalogService(XBCXNameService.class);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        specSelectTree = new javax.swing.JTree();

        specSelectTree.setModel(treeModel);
        scrollPane.setViewportView(specSelectTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogSelectSpecPanel(CatalogItemType.NODE));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree specSelectTree;
    // End of variables declaration//GEN-END:variables

    public XBCItem getSpec() {
        return (XBCItem) specSelectTree.getLastSelectedPathComponent();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public interface SelectionListener {

        void selectedItem(XBCItem item);
    }
}
