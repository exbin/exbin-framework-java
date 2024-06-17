/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.component.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.toolbar.EditItemActions;
import org.exbin.framework.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.component.api.toolbar.SideToolBar;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Item edit default action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultEditItemActions implements EditItemActions {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ComponentModule.class);
    private EditItemActionsHandler actionsHandler = null;

    public static final String ADD_ITEM_ACTION_ID = "addItemAction";
    public static final String EDIT_ITEM_ACTION_ID = "editItemAction";
    public static final String DELETE_ITEM_ACTION_ID = "deleteItemAction";

    private final Mode mode;
    private Action addItemAction = null;
    private Action editItemAction = null;
    private Action deleteItemAction = null;

    public DefaultEditItemActions() {
        this(Mode.NORMAL);
    }

    public DefaultEditItemActions(Mode mode) {
        this.mode = mode;
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(addItemAction, resourceBundle, ADD_ITEM_ACTION_ID);
            if (mode == Mode.DIALOG) {
                addItemAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            }
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(editItemAction, resourceBundle, EDIT_ITEM_ACTION_ID);
            if (mode == Mode.DIALOG) {
                editItemAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            }
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(deleteItemAction, resourceBundle, DELETE_ITEM_ACTION_ID);
            deleteItemAction.setEnabled(false);
        }
        return deleteItemAction;
    }

    @Override
    public void updateEditItemActions() {
        if (addItemAction != null) {
            addItemAction.setEnabled(actionsHandler.canAddItem());
        }
        if (editItemAction != null) {
            editItemAction.setEnabled(actionsHandler.canEditItem());
        }
        if (deleteItemAction != null) {
            deleteItemAction.setEnabled(actionsHandler.canDeleteItem());
        }
    }

    @Override
    public void registerActions(SideToolBar sideToolBar) {
        sideToolBar.addAction(getAddItemAction());
        sideToolBar.addAction(getEditItemAction());
        sideToolBar.addAction(getDeleteItemAction());
        updateEditItemActions();
        actionsHandler.setUpdateListener(this::updateEditItemActions);
    }

    public enum Mode {
        NORMAL, DIALOG
    }
}
