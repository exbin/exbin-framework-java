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
package org.exbin.framework.gui.component;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.gui.component.api.toolbar.EditItemActions;
import org.exbin.framework.gui.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.gui.component.api.toolbar.SideToolBar;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Item edit default action set.
 *
 * @version 0.2.0 2016/03/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultEditItemActions implements EditItemActions {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiComponentModule.class);
    private EditItemActionsHandler actionsHandler = null;

    public static final String ADD_ITEM_ACTION = "addItemAction";
    public static final String EDIT_ITEM_ACTION = "editItemAction";
    public static final String DELETE_ITEM_ACTION = "deleteItemAction";

    private Action addItemAction = null;
    private Action editItemAction = null;
    private Action deleteItemAction = null;

    public DefaultEditItemActions() {
    }

    @Override
    public void setEditItemActionsHandler(EditItemActionsHandler actionsHandler) {
        this.actionsHandler = actionsHandler;
    }

    @Nonnull
    @Override
    public Action getAddItemAction() {
        if (addItemAction == null) {
            addItemAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performAddItem();
                }
            };
            ActionUtils.setupAction(addItemAction, resourceBundle, ADD_ITEM_ACTION);
            addItemAction.setEnabled(false);
        }
        return addItemAction;
    }

    @Nonnull
    @Override
    public Action getEditItemAction() {
        if (editItemAction == null) {
            editItemAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performEditItem();
                }
            };
            ActionUtils.setupAction(editItemAction, resourceBundle, EDIT_ITEM_ACTION);
            editItemAction.setEnabled(false);
        }
        return editItemAction;
    }

    @Nonnull
    @Override
    public Action getDeleteItemAction() {
        if (deleteItemAction == null) {
            deleteItemAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performDeleteItem();
                }
            };
            ActionUtils.setupAction(deleteItemAction, resourceBundle, DELETE_ITEM_ACTION);
            deleteItemAction.setEnabled(false);
        }
        return deleteItemAction;
    }

    @Override
    public void updateEditItemActions() {
        if (addItemAction != null) {
            addItemAction.setEnabled(actionsHandler.isEditable());
        }
        if (editItemAction != null) {
            editItemAction.setEnabled(actionsHandler.isSelection() && actionsHandler.isEditable());
        }
        if (deleteItemAction != null) {
            deleteItemAction.setEnabled(actionsHandler.isSelection() && actionsHandler.isEditable());
        }
    }

    @Override
    public void registerActions(SideToolBar sideToolBar) {
        sideToolBar.addAction(getAddItemAction());
        sideToolBar.addAction(getEditItemAction());
        sideToolBar.addAction(getDeleteItemAction());
        updateEditItemActions();
    }
}
