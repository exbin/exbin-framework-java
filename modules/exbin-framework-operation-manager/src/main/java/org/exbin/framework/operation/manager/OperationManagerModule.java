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
package org.exbin.framework.operation.manager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.operation.manager.action.UndoManagerAction;
import org.exbin.framework.operation.manager.api.OperationManagerModuleApi;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;

/**
 * Implementation of operation manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OperationManagerModule implements OperationManagerModuleApi {

    public OperationManagerModule() {
    }

    public void init() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Override
    public void registerOperationManagerInMainMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(OperationManagerModuleApi.MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(createUndoManagerAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public Action createUndoManagerAction() {
        UndoManagerAction undoManagerAction = new UndoManagerAction();
        undoManagerAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(undoManagerAction, undoManagerAction.getResourceBundle(), UndoManagerAction.EDIT_UNDO_MANAGER_ACTION_ID);
        undoManagerAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, undoManagerAction);
        return undoManagerAction;
    }
}
