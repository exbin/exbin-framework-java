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
package org.exbin.framework.editor.xbup.catalog.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.service.ServiceManagerModule;
import org.exbin.framework.service.catalog.action.AddItemAction;
import org.exbin.framework.service.catalog.action.EditItemAction;
import org.exbin.framework.service.catalog.action.ExportItemAction;
import org.exbin.framework.service.catalog.action.ImportItemAction;
import org.exbin.framework.service.catalog.gui.CatalogEditorPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCRoot;

/**
 * Edit catalog root action.
 *
 * @version 0.2.2 2021/12/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditCatalogAction extends AbstractAction {

    public static final String ACTION_ID = "editCatalogAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(EditCatalogAction.class);

    private XBApplication application;
    private XBACatalog catalog;

    private Component parentComponent;
    private XBCRoot activeItem;

    public EditCatalogAction() {
    }

    public void setup(XBApplication application) {
        this.application = application;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void setActiveItem(XBCRoot activeItem) {
        this.activeItem = activeItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        ServiceManagerModule managerModule = application.getModuleRepository().getModuleByInterface(ServiceManagerModule.class);
        MenuManagement menuManagement = managerModule.getDefaultMenuManagement();
        CatalogEditorPanel catalogEditorPanel = new CatalogEditorPanel();
        catalogEditorPanel.setController(new CatalogEditorPanel.Controller() {
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
        catalogEditorPanel.setApplication(application);
        catalogEditorPanel.setMenuManagement(menuManagement);
        catalogEditorPanel.setCatalog(catalog);
        catalogEditorPanel.setCatalogRoot(activeItem);
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(catalogEditorPanel, controlPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        frameModule.setDialogTitle(dialog, catalogEditorPanel.getResourceBundle());
        dialog.showCentered(parentComponent);
    }
}
