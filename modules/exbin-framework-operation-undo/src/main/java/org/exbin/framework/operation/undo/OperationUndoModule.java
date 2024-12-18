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
package org.exbin.framework.operation.undo;

import org.exbin.framework.operation.undo.action.BasicUndoActions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.GroupToolBarContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionToolBarContributionRule;
import org.exbin.framework.action.api.SeparationMenuContributionRule;
import org.exbin.framework.action.api.SeparationToolBarContributionRule;
import org.exbin.framework.action.api.ToolBarContribution;

/**
 * Implementation of undo/redo support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OperationUndoModule implements OperationUndoModuleApi {

    private BasicUndoActions defaultUndoActions = null;

    public OperationUndoModule() {
    }

    public void init() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Override
    public void registerMainMenu() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuContribution contribution = actionModule.registerMenuGroup(ActionConsts.EDIT_MENU_ID, MODULE_ID, OperationUndoModuleApi.UNDO_MENU_GROUP_ID);
        actionModule.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
        actionModule.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.BELOW));
        contribution = actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.createUndoAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
        contribution = actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.createRedoAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerMainToolBar() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ToolBarContribution contribution = actionModule.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID);
        actionModule.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionMode.TOP));
        actionModule.registerToolBarRule(contribution, new SeparationToolBarContributionRule(SeparationMode.AROUND));
        contribution = actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.createUndoAction());
        actionModule.registerToolBarRule(contribution, new GroupToolBarContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
        contribution = actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.createRedoAction());
        actionModule.registerToolBarRule(contribution, new GroupToolBarContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
    }

    @Nonnull
    @Override
    public UndoActions createUndoActions() {
        BasicUndoActions undoActions = new BasicUndoActions();
        return undoActions;
    }

    @Nonnull
    public BasicUndoActions getDefaultUndoActions() {
        if (defaultUndoActions == null) {
            defaultUndoActions = new BasicUndoActions();
        }

        return defaultUndoActions;
    }
}
