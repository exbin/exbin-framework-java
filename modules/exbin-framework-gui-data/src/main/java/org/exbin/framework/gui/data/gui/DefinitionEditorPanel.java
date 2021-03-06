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
package org.exbin.framework.gui.data.gui;

import java.awt.BorderLayout;
import javax.swing.event.ListSelectionEvent;
import org.exbin.framework.gui.component.GuiComponentModule;
import org.exbin.framework.gui.component.api.toolbar.EditItemActions;
import org.exbin.framework.gui.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.gui.component.api.toolbar.EditItemActionsHandlerEmpty;
import org.exbin.framework.gui.component.api.toolbar.MoveItemActions;
import org.exbin.framework.gui.component.api.toolbar.MoveItemActionsHandler;
import org.exbin.framework.gui.component.api.toolbar.MoveItemActionsHandlerEmpty;
import org.exbin.framework.gui.component.gui.ToolBarSidePanel;
import org.exbin.framework.gui.action.GuiActionModule;
import org.exbin.framework.gui.utils.ClipboardActions;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsHandlerEmpty;
import org.exbin.framework.gui.undo.GuiUndoModule;
import org.exbin.framework.gui.undo.api.UndoActions;
import org.exbin.framework.gui.undo.api.UndoActionsHandler;
import org.exbin.framework.gui.undo.api.UndoActionsHandlerEmpty;
import org.exbin.framework.gui.utils.GuiUtilsModule;
import org.exbin.framework.gui.utils.TestApplication;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;

/**
 * Data type definition editor panel.
 *
 * @version 0.2.1 2020/04/19
 * @author ExBin Project (http://exbin.org)
 */
public class DefinitionEditorPanel extends javax.swing.JPanel {

    private XBACatalog catalog;
    private XBCItem catalogItem;
//    private XBCSpecService specService;
    private final CatalogDefsTableModel defsModel = new CatalogDefsTableModel();
    DefinitionPropertiesComponent propertiesComponent = new DefinitionPropertiesComponent();
//    private List<CatalogDefsTableItem> removeList;
//    private List<CatalogDefsTableItem> updateList;

//    private ToolBarEditorPanel toolBarEditorPanel;
    private ToolBarSidePanel toolBarSidePanel;

    public DefinitionEditorPanel() {
        super();
        initComponents();
        init();
    }

    private void init() {
//        toolBarEditorPanel = new ToolBarEditorPanel();
        add(definitionControlSplitPane, BorderLayout.CENTER);

        toolBarSidePanel = new ToolBarSidePanel();
        toolBarSidePanel.add(definitionScrollPane);
        definitionControlSplitPane.setLeftComponent(toolBarSidePanel);
        definitionControlSplitPane.setRightComponent(propertiesComponent);

        definitionsTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = definitionsTable.getSelectedRow();
                propertiesComponent.setItem(selectedRow >= 0 ? defsModel.getRowItem(selectedRow) : null);
                propertiesComponent.repaint();
                updateItemStatus();
            }
        });

        updateItemStatus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        definitionScrollPane = new javax.swing.JScrollPane();
        definitionsTable = new javax.swing.JTable();
        definitionControlSplitPane = new javax.swing.JSplitPane();

        definitionsTable.setModel(defsModel);
        definitionsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        definitionScrollPane.setViewportView(definitionsTable);

        definitionControlSplitPane.setDividerLocation(400);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        TestApplication testApplication = GuiUtilsModule.getDefaultAppEditor();
        GuiUndoModule guiUndoModule = new GuiUndoModule();
        testApplication.addModule(GuiUndoModule.MODULE_ID, guiUndoModule);
        GuiActionModule guiActionModule = new GuiActionModule();
        testApplication.addModule(GuiActionModule.MODULE_ID, guiActionModule);
        GuiComponentModule guiComponentModule = new GuiComponentModule();
        testApplication.addModule(GuiComponentModule.MODULE_ID, guiComponentModule);

        DefinitionEditorPanel definitionEditorPanel = new DefinitionEditorPanel();
        UndoActionsHandler undoActionsHandler = new UndoActionsHandlerEmpty();
        definitionEditorPanel.setUndoHandler(undoActionsHandler, guiUndoModule.createUndoActions(undoActionsHandler));
        ClipboardActionsHandler clipboardActionsHandler = new ClipboardActionsHandlerEmpty();
        definitionEditorPanel.setClipboardHandler(clipboardActionsHandler, guiActionModule.createClipboardActions(clipboardActionsHandler));
        WindowUtils.invokeDialog(definitionEditorPanel);
        definitionEditorPanel.registerToolBarActions(guiComponentModule);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane definitionControlSplitPane;
    private javax.swing.JScrollPane definitionScrollPane;
    private javax.swing.JTable definitionsTable;
    // End of variables declaration//GEN-END:variables

//    public void persist() {
//        for (CatalogDefsTableItem defItem : updateList) {
//            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);
//            XBCXDescService descService = catalog.getCatalogService(XBCXDescService.class);
//            XBCXStriService striService = catalog.getCatalogService(XBCXStriService.class);
//
//            XBESpecDef specDef = (XBESpecDef) defItem.getSpecDef();
//            if (specDef != null && specDef.getType() != defItem.getDefType()) {
//                specService.removeItem(specDef);
//                specDef = null;
//            }
//
//            if (specDef == null) {
//                specDef = (XBESpecDef) specService.createSpecDef((XBCSpec) catalogItem, defItem.getDefType());
//                specDef.setCatalogItem((XBESpec) catalogItem);
//            }
//
//            specDef.setXBIndex(defItem.getXbIndex());
//            specDef.setTarget((XBERev) defItem.getTarget());
//
//            specService.persistItem(specDef);
//
//            ((XBEXNameService) nameService).setDefaultText(specDef, defItem.getName());
//            ((XBEXDescService) descService).setDefaultText(specDef, defItem.getDescription());
//            ((XBEXStriService) striService).setItemStringIdText(specDef, defItem.getStringId());
//        }
//
//        for (CatalogDefsTableItem defItem : removeList) {
//            if (defItem.getSpecDef() != null) {
//                specService.removeItemDepth(defItem.getSpecDef());
//            }
//        }
//    }
    private void updateItemStatus() {
//        int selectedRow = itemDefinitionsTable.getSelectedRow();
//        int rowsCount = defsModel.getRowCount();
//        if ((selectedRow >= 0) && (selectedRow < rowsCount)) {
//            moveUpDefButton.setEnabled(selectedRow > 0);
//            moveDownDefButton.setEnabled(selectedRow < rowsCount - 1);
//            modifyButton.setEnabled(true);
//            removeDefButton.setEnabled(true);
//        } else {
//            moveUpDefButton.setEnabled(false);
//            moveDownDefButton.setEnabled(false);
//            modifyButton.setEnabled(false);
//            removeDefButton.setEnabled(false);
//        }
        definitionsTable.repaint();
    }

    public void setCatalogItem(XBCItem catalogItem) {
        this.catalogItem = catalogItem;
//        addButton.setEnabled(!(catalogItem instanceof XBCNode));
        defsModel.setCatalogItem(catalogItem);
//        updateList = new ArrayList<>();
//        removeList = new ArrayList<>();
        updateItemStatus();
    }

    public XBCItem getCatalogItem() {
        return catalogItem;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
//        specService = catalog.getCatalogService(XBCSpecService.class);
        defsModel.setCatalog(catalog);
    }

    public CatalogDefsTableModel getDefsModel() {
        return defsModel;
    }

    public void setUndoHandler(UndoActionsHandler undoHandler, UndoActions undoActions) {
        // toolBarEditorPanel.setUndoHandler(undoHandler, undoActions);
    }

    public void setClipboardHandler(ClipboardActionsHandler clipboardHandler, ClipboardActions clipboardActions) {
        // toolBarEditorPanel.setClipboardHandler(clipboardHandler, clipboardActions);
    }

    public void registerToolBarActions(GuiComponentModule guiComponentModule) {
        MoveItemActionsHandler moveItemActionsHandler = new MoveItemActionsHandlerEmpty();
        MoveItemActions moveItemActions = guiComponentModule.createMoveItemActions(moveItemActionsHandler);
        toolBarSidePanel.addActions(moveItemActions);
        toolBarSidePanel.addSeparator();
        EditItemActionsHandler editItemActionsHandler = new EditItemActionsHandlerEmpty();
        EditItemActions editItemActions = guiComponentModule.createEditItemActions(editItemActionsHandler);
        toolBarSidePanel.addActions(editItemActions);
    }
}
