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
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.action.api.ActionModuleApi;

/**
 * Implementation of XBUP framework undo/redo module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OperationUndoModule implements OperationUndoModuleApi {

    private static final String UNDO_MENU_GROUP_ID = MODULE_ID + ".undoMenuGroup";
    private static final String UNDO_TOOL_BAR_GROUP_ID = MODULE_ID + ".undoToolBarGroup";

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
        actionModule.registerMenuGroup(ActionConsts.EDIT_MENU_ID, new MenuGroup(UNDO_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.BELOW));
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.createUndoAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.createRedoAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerUndoManagerInMainMenu() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.createUndoManagerAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerMainToolBar() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, new ToolBarGroup(UNDO_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP), SeparationMode.AROUND));
        actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.createUndoAction(), new ToolBarPosition(UNDO_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.createRedoAction(), new ToolBarPosition(UNDO_TOOL_BAR_GROUP_ID));
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
