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
package org.exbin.framework.editor.xbup.catalog.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.action.api.MenuManagement;
import org.exbin.framework.gui.service.catalog.action.AddItemAction;
import org.exbin.framework.gui.service.catalog.action.EditItemAction;
import org.exbin.framework.gui.service.catalog.action.ExportItemAction;
import org.exbin.framework.gui.service.catalog.action.ImportItemAction;
import org.exbin.framework.gui.service.catalog.gui.CatalogEditorPanel;
import org.exbin.framework.gui.service.gui.CatalogAvailabilityPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.framework.gui.service.gui.CatalogManagementAware;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;

/**
 * Panel for showing information about document block.
 *
 * @version 0.2.1 2019/06/29
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogEditorWrapperPanel extends javax.swing.JPanel implements CatalogManagementAware {

    private XBApplication application;
    private final CatalogEditorPanel catalogEditorPanel;
    private final CatalogAvailabilityPanel catalogAvailabilityPanel;
    private XBACatalog catalog = null;
    private MenuManagement menuManagement;

    public CatalogEditorWrapperPanel() {
        initComponents();
        catalogEditorPanel = new CatalogEditorPanel();
        catalogEditorPanel.setControl(new CatalogEditorPanel.Control() {
            @Override
            public void exportItem(Component parentComponent, XBCItem currentItem) {
                ExportItemAction exportItemAction = new ExportItemAction();
                exportItemAction.setCatalog(catalog);
                exportItemAction.setParentComponent(parentComponent);
                exportItemAction.setCurrentItem(currentItem);
                exportItemAction.actionPerformed(null);
            }

            @Override
            public void importItem(Component parentComponent, XBCItem currentItem) {
                ImportItemAction importItemAction = new ImportItemAction();
                importItemAction.setCatalog(catalog);
                importItemAction.setParentComponent(parentComponent);
                importItemAction.setCurrentItem(currentItem);
                importItemAction.actionPerformed(null);
            }

            @Override
            public void addItem(Component parentComponent, XBCItem currentItem) {
                AddItemAction addItemAction = new AddItemAction();
                addItemAction.setApplication(application);
                addItemAction.setCatalog(catalog);
                addItemAction.setParentComponent(parentComponent);
                addItemAction.setCurrentItem(currentItem);
                addItemAction.actionPerformed(null);
                XBCItem resultItem = addItemAction.getResultItem();
                if (resultItem != null) {
                    catalogEditorPanel.reloadNodesTree();
                    catalogEditorPanel.setNode(resultItem instanceof XBCNode ? (XBCNode) resultItem : catalogEditorPanel.getSpecsNode());
                    catalogEditorPanel.selectSpecTableRow(resultItem);
                }
            }

            @Override
            public void editItem(Component parentComponent, XBCItem currentItem) {
                EditItemAction editItemAction = new EditItemAction();
                editItemAction.setApplication(application);
                editItemAction.setCatalog(catalog);
                editItemAction.setMenuManagement(menuManagement);
                editItemAction.setParentComponent(parentComponent);
                editItemAction.setCurrentItem(currentItem);
                editItemAction.actionPerformed(null);
                XBCItem resultItem = editItemAction.getResultItem();
                if (resultItem != null) {
                    catalogEditorPanel.setItem(currentItem);
                    catalogEditorPanel.setSpecsNode(catalogEditorPanel.getSpecsNode());
                    catalogEditorPanel.selectSpecTableRow(currentItem);
                }
            }
        });
        catalogAvailabilityPanel = new CatalogAvailabilityPanel();
        init();
    }

    private void init() {
        add(catalogAvailabilityPanel, BorderLayout.CENTER);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        catalogEditorPanel.setApplication(application);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return catalogEditorPanel.getResourceBundle();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogEditorWrapperPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void setMenuManagement(MenuManagement menuManagement) {
        this.menuManagement = menuManagement;
        catalogEditorPanel.setMenuManagement(menuManagement);
    }

    @Override
    public void setCatalog(@Nullable XBACatalog catalog) {
        if (this.catalog == null && catalog != null) {
            catalogAvailabilityPanel.setCatalog(catalog);
            catalogEditorPanel.setCatalog(catalog);
            removeAll();
            add(catalogEditorPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        } else if (this.catalog != null && catalog == null) {
            catalogAvailabilityPanel.setCatalog(catalog);
            removeAll();
            add(catalogAvailabilityPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
            catalogEditorPanel.setCatalog(catalog);
        } else {
            catalogEditorPanel.setCatalog(catalog);
        }

        this.catalog = catalog;
    }
}
