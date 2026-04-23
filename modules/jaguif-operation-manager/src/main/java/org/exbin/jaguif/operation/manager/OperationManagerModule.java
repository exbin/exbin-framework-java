/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.operation.manager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.operation.manager.action.UndoManagerAction;
import org.exbin.jaguif.operation.manager.api.OperationManagerModuleApi;
import org.exbin.jaguif.operation.undo.api.OperationUndoModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.operation.manager.contribution.UndoManagerContribution;

/**
 * Implementation of operation manager module.
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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new UndoManagerContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public Action createUndoManagerAction() {
        UndoManagerAction undoManagerAction = new UndoManagerAction();
        undoManagerAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(undoManagerAction, undoManagerAction.getResourceBundle(), UndoManagerAction.ACTION_ID);
        undoManagerAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, undoManagerAction);
        return undoManagerAction;
    }
}
