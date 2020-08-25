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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.MenuManagement;
import org.exbin.framework.gui.service.YamlFileType;
import org.exbin.framework.gui.service.gui.CatalogManagerPanelable;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.xbup.catalog.XBECatalog;
import org.exbin.xbup.catalog.entity.XBEXDesc;
import org.exbin.xbup.catalog.convert.XBCatalogYaml;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCXName;
import org.exbin.xbup.core.catalog.base.XBCXStri;
import org.exbin.xbup.core.catalog.base.service.XBCNodeService;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.catalog.base.service.XBCXDescService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.catalog.base.service.XBCXStriService;

/**
 * Catalog items search panel.
 *
 * @version 0.2.1 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogItemsSearchPanel extends javax.swing.JPanel implements CatalogManagerPanelable {

    private XBApplication application;
    private XBCItem currentItem;

    private XBACatalog catalog;
//    private MainFrameManagement mainFrameManagement;
    private CatalogItemsTableModel itemsModel;
    private final CatalogSearchTableModel searchModel;
    private final XBCatalogYaml catalogYaml;

    // Cached values
    private XBCNodeService nodeService;
    private XBCSpecService specService;
    private XBCXNameService nameService;
    private XBCXDescService descService;
    private XBCXStriService striService;

    private final Map<String, ActionListener> actionListenerMap = new HashMap<>();
    private MenuManagement menuManagement;
    private CatalogSearchTableModel.CatalogSearchTableItem searchConditions = null;
    private SelectionListener selectionListener;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CatalogItemsSearchPanel.class);

    public CatalogItemsSearchPanel() {
        itemsModel = new CatalogItemsTableModel();
        searchModel = new CatalogSearchTableModel();
        catalogYaml = new XBCatalogYaml();

        initComponents();

        DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());
        defaultCellEditor.setClickCountToStart(0);
        defaultCellEditor.addCellEditorListener(new CellEditorListener() {

            @Override
            public void editingStopped(ChangeEvent e) {
                if (catalogItemsListTable.getSelectedRowCount() == 0) {
                    performSearch();
                }
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });
        int columnCount = catalogSearchTable.getColumnModel().getColumnCount();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            TableColumn column = catalogSearchTable.getColumnModel().getColumn(columnIndex);
            column.setCellEditor(defaultCellEditor);
        }

        catalogItemsListTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                if (catalogItemsListTable.getSelectedRow() >= 0) {
                    setItem(itemsModel.getItem(catalogItemsListTable.getSelectedRow()));
                } else {
                    setItem(null);
                }
            }
        });

        actionListenerMap.put(DefaultEditorKit.cutAction, (ActionListener) (ActionEvent e) -> {
            performCut();
        });
        actionListenerMap.put(DefaultEditorKit.copyAction, (ActionListener) (ActionEvent e) -> {
            performCopy();
        });
        actionListenerMap.put(DefaultEditorKit.pasteAction, (ActionListener) (ActionEvent e) -> {
            performPaste();
        });
        actionListenerMap.put(DefaultEditorKit.deleteNextCharAction, (ActionListener) (ActionEvent e) -> {
            performDelete();
        });
        actionListenerMap.put("delete", (ActionListener) (ActionEvent e) -> {
            performDelete();
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void switchToSpecTypeMode(CatalogItemType specType) {
        itemsModel.setSpecType(specType);
        catalogItemsListTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catalogSearchTable.changeSelection(0, 0, false, false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        catalogTreePopupMenu = new javax.swing.JPopupMenu();
        popupRefreshMenuItem = new javax.swing.JMenuItem();
        popupEditMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        popupExportItemMenuItem = new javax.swing.JMenuItem();
        searchPanel = new javax.swing.JPanel();
        catalogSearchScrollPane = new javax.swing.JScrollPane();
        catalogSearchTable = new javax.swing.JTable();
        searchButton = new javax.swing.JButton();
        catalogItemListScrollPane = new javax.swing.JScrollPane();
        catalogItemsListTable = new javax.swing.JTable();

        catalogTreePopupMenu.setName("catalogTreePopupMenu"); // NOI18N

        popupRefreshMenuItem.setText(resourceBundle.getString("refreshMenuItem.text")); // NOI18N
        popupRefreshMenuItem.setToolTipText(resourceBundle.getString("refreshMenuItem.toolTipText")); // NOI18N
        popupRefreshMenuItem.setName("popupRefreshMenuItem"); // NOI18N
        popupRefreshMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupRefreshMenuItemActionPerformed(evt);
            }
        });
        catalogTreePopupMenu.add(popupRefreshMenuItem);

        popupEditMenuItem.setText(resourceBundle.getString("editMenuItem.text")); // NOI18N
        popupEditMenuItem.setToolTipText(resourceBundle.getString("editMenuItem.toolTipText")); // NOI18N
        popupEditMenuItem.setName("popupEditMenuItem"); // NOI18N
        popupEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupEditMenuItemActionPerformed(evt);
            }
        });
        catalogTreePopupMenu.add(popupEditMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        catalogTreePopupMenu.add(jSeparator1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        catalogTreePopupMenu.add(jSeparator2);

        popupExportItemMenuItem.setText(resourceBundle.getString("exportItemMenuItem.text")); // NOI18N
        popupExportItemMenuItem.setToolTipText(resourceBundle.getString("exportItemMenuItem.toolTipText")); // NOI18N
        popupExportItemMenuItem.setName("popupExportItemMenuItem"); // NOI18N
        popupExportItemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupExportItemMenuItemActionPerformed(evt);
            }
        });
        catalogTreePopupMenu.add(popupExportItemMenuItem);

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        searchPanel.setName("searchPanel"); // NOI18N

        catalogSearchScrollPane.setName("catalogSearchScrollPane"); // NOI18N

        catalogSearchTable.setModel(searchModel);
        catalogSearchTable.setName("catalogSearchTable"); // NOI18N
        catalogSearchTable.setRowSelectionAllowed(false);
        catalogSearchScrollPane.setViewportView(catalogSearchTable);

        searchButton.setText("Search");
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGap(0, 409, Short.MAX_VALUE)
                .addComponent(searchButton)
                .addContainerGap())
            .addComponent(catalogSearchScrollPane)
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addComponent(catalogSearchScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchButton)
                .addContainerGap())
        );

        add(searchPanel, java.awt.BorderLayout.NORTH);

        catalogItemListScrollPane.setComponentPopupMenu(catalogTreePopupMenu);
        catalogItemListScrollPane.setName("catalogItemListScrollPane"); // NOI18N

        catalogItemsListTable.setModel(itemsModel);
        catalogItemsListTable.setComponentPopupMenu(catalogTreePopupMenu);
        catalogItemsListTable.setName("catalogItemsListTable"); // NOI18N
        catalogItemListScrollPane.setViewportView(catalogItemsListTable);

        add(catalogItemListScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void popupExportItemMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupExportItemMenuItemActionPerformed
        if (currentItem != null) {
            JFileChooser exportFileChooser = new JFileChooser();
            exportFileChooser.addChoosableFileFilter(new YamlFileType());
            exportFileChooser.setAcceptAllFileFilterUsed(true);
            if (exportFileChooser.showSaveDialog(WindowUtils.getFrame(this)) == JFileChooser.APPROVE_OPTION) {
                FileWriter fileWriter;
                try {
                    fileWriter = new FileWriter(exportFileChooser.getSelectedFile().getAbsolutePath());
                    try {
                        catalogYaml.exportCatalogItem(currentItem, fileWriter);
                    } finally {
                        fileWriter.close();
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CatalogItemsSearchPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CatalogItemsSearchPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_popupExportItemMenuItemActionPerformed

    private void popupRefreshMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupRefreshMenuItemActionPerformed
        Component invoker = catalogTreePopupMenu.getInvoker();
        reload();
    }//GEN-LAST:event_popupRefreshMenuItemActionPerformed

    private void popupEditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupEditMenuItemActionPerformed
        if (currentItem != null) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            CatalogEditItemPanel editPanel = new CatalogEditItemPanel();
            editPanel.setApplication(application);
            editPanel.setMenuManagement(menuManagement);
            editPanel.setCatalog(catalog);
            editPanel.setCatalogItem(currentItem);
            editPanel.setVisible(true);

            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(editPanel, controlPanel);
            final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
            WindowUtils.addHeaderPanel(dialog.getWindow(), editPanel.getClass(), editPanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    EntityManager em = ((XBECatalog) catalog).getEntityManager();
                    EntityTransaction transaction = em.getTransaction();
                    transaction.begin();
                    editPanel.persist();
                    setItem(currentItem);
                    em.flush();
                    transaction.commit();
                    reload();
                }
                dialog.close();
            });
            dialog.showCentered(this);
            dialog.dispose();
        }
    }//GEN-LAST:event_popupEditMenuItemActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchConditions = searchModel.getSearchConditions();
        reload();
    }//GEN-LAST:event_searchButtonActionPerformed

    public void performSearch() {
        WindowUtils.doButtonClick(searchButton);
    }

    public void setItem(XBCItem item) {
        currentItem = item;

        popupEditMenuItem.setEnabled(item != null);
        popupExportItemMenuItem.setEnabled(item != null);

        if (selectionListener != null) {
            selectionListener.selectedItem(item);
        }

//        if (mainFrameManagement != null) {
//            updateActionStatus(mainFrameManagement.getLastFocusOwner());
//        }
    }

//    public boolean updateActionStatus(Component component) {
//        if (component == catalogTree) {
//            // clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//
//            if (mainFrameManagement != null) {
//                mainFrameManagement.getCutAction().setEnabled(currentItem != null);
//                mainFrameManagement.getCopyAction().setEnabled(currentItem != null);
//                mainFrameManagement.getPasteAction().setEnabled(false); // TODO clipboard.isDataFlavorAvailable(xbFlavor));
//                mainFrameManagement.getDeleteAction().setEnabled(currentItem != null);
//                mainFrameManagement.getSelectAllAction().setEnabled(false);
//            }
//
//            // frameManagement.getUndoAction().setEnabled(treeUndo.canUndo());
//            // frameManagement.getRedoAction().setEnabled(treeUndo.canRedo());
//            return true;
//        }
//
//        return false;
//    }
//    @Override
//    public void setMainFrameManagement(MainFrameManagement mainFrameManagement) {
//        this.mainFrameManagement = mainFrameManagement;
//    }
    private void reload() {
        if (catalogSearchTable.getCellEditor() != null) {
            catalogSearchTable.getCellEditor().stopCellEditing();
        }

        itemsModel.performSearch(searchConditions);
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogItemsSearchPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane catalogItemListScrollPane;
    private javax.swing.JTable catalogItemsListTable;
    private javax.swing.JScrollPane catalogSearchScrollPane;
    private javax.swing.JTable catalogSearchTable;
    private javax.swing.JPopupMenu catalogTreePopupMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem popupEditMenuItem;
    private javax.swing.JMenuItem popupExportItemMenuItem;
    private javax.swing.JMenuItem popupRefreshMenuItem;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        nodeService = catalog == null ? null : catalog.getCatalogService(XBCNodeService.class);
        specService = catalog == null ? null : catalog.getCatalogService(XBCSpecService.class);
        nameService = catalog == null ? null : catalog.getCatalogService(XBCXNameService.class);
        descService = catalog == null ? null : catalog.getCatalogService(XBCXDescService.class);
        striService = catalog == null ? null : catalog.getCatalogService(XBCXStriService.class);

        itemsModel.setCatalog(catalog);
        catalogYaml.setCatalog(catalog);
    }

    public void performCut() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performCopy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performPaste() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performDelete() {
        Object[] options = {
            "Delete",
            "Cancel"
        };

        int result = JOptionPane.showOptionDialog(this,
                "Are you sure you want to delete this item?",
                "Delete Item",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (result == JOptionPane.OK_OPTION) {
            // TODO: Use different transaction management later
            EntityManager em = ((XBECatalog) catalog).getEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            List<XBCXName> names = nameService.getItemNames(currentItem);
            for (XBCXName name : names) {
                nameService.removeItem(name);
            }

            List<XBEXDesc> descs = descService.getItemDescs(currentItem);
            for (XBEXDesc desc : descs) {
                descService.removeItem(desc);
            }

            XBCXStri stri = striService.getItemStringId(currentItem);
            if (stri != null) {
                striService.removeItem(stri);
            }

            if (currentItem instanceof XBCNode) {
                nodeService.removeItem(currentItem);
            } else {
                specService.removeItem(currentItem);
            }
            em.flush();
            transaction.commit();

            reload();
            repaint();
        }
    }

//    @Override
//    public boolean performAction(String eventName, ActionEvent event) {
//        if (mainFrameManagement != null && mainFrameManagement.getLastFocusOwner() != null) {
//            ActionListener actionListener = actionListenerMap.get(eventName);
//            if (actionListener != null) {
//                actionListener.actionPerformed(event);
//                return true;
//            }
//        }
//
//        return false;
//    }
    @Override
    public void setMenuManagement(MenuManagement menuManagement) {
        this.menuManagement = menuManagement;
        menuManagement.insertMainPopupMenu(catalogTreePopupMenu, 3);
    }

    public XBCItem getItem() {
        return currentItem;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public interface SelectionListener {

        void selectedItem(XBCItem item);
    }
}
