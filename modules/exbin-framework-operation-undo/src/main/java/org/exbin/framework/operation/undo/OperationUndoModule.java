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
package org.exbin.framework.operation.undo;

import org.exbin.framework.operation.undo.action.BasicUndoActions;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.operation.undo.api.UndoActionsHandler;
import org.exbin.framework.operation.undo.api.UndoUpdateListener;
import org.exbin.framework.operation.undo.handler.UndoManagerControlHandler;
import org.exbin.framework.operation.undo.gui.UndoManagerControlPanel;
import org.exbin.framework.operation.undo.gui.UndoManagerPanel;
import org.exbin.framework.operation.undo.gui.UndoManagerModel;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.action.api.ActionModuleApi;

/**
 * Implementation of XBUP framework undo/redo module.
 *
 * @version 0.2.0 2016/01/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OperationUndoModule implements OperationUndoModuleApi {

    private static final String UNDO_MENU_GROUP_ID = MODULE_ID + ".undoMenuGroup";
    private static final String UNDO_TOOL_BAR_GROUP_ID = MODULE_ID + ".undoToolBarGroup";

    private XBApplication application;
    private final UndoManagerModel undoModel = new UndoManagerModel();
    private XBUndoHandler undoHandler;

    private BasicUndoActions defaultUndoActions = null;

    public OperationUndoModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        undoModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                updateUndoStatus();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        });
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public void actionEditUndo() {
        try {
            undoHandler.performUndo();
        } catch (Exception ex) {
            Logger.getLogger(OperationUndoModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actionEditRedo() {
        try {
            undoHandler.performRedo();
        } catch (Exception ex) {
            Logger.getLogger(OperationUndoModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setUndoHandler(final XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        getDefaultUndoActions().setUndoActionsHandler(new UndoActionsHandler() {
            @Override
            public boolean canUndo() {
                return undoHandler.canUndo();
            }

            @Override
            public boolean canRedo() {
                return undoHandler.canRedo();
            }

            @Override
            public void performUndo() {
                actionEditUndo();
            }

            @Override
            public void performRedo() {
                actionEditRedo();
            }

            @Override
            public void performUndoManager() {
                openUndoManager();
            }

            @Override
            public void setUndoUpdateListener(UndoUpdateListener undoUpdateListener) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        undoModel.setUndoHandler(undoHandler);
        undoHandler.addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                updateUndoStatus();
            }

            @Override
            public void undoCommandAdded(Command cmnd) {
            }
        });
    }

    @Override
    public void updateUndoStatus() {
        getDefaultUndoActions().updateUndoActions();
    }

    @Override
    public void registerMainMenu() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.EDIT_MENU_ID, new MenuGroup(UNDO_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.BELOW));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.getUndoAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.getRedoAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerUndoManagerInMainMenu() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, OperationUndoModuleApi.MODULE_ID, defaultUndoActions.getUndoManagerAction(), new MenuPosition(UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerMainToolBar() {
        getDefaultUndoActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(UNDO_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP), SeparationMode.AROUND));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.getUndoAction(), new ToolBarPosition(UNDO_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, defaultUndoActions.getRedoAction(), new ToolBarPosition(UNDO_TOOL_BAR_GROUP_ID));
    }

    @Override
    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    @Override
    public void openUndoManager() {
        FrameModuleApi frameModule = OperationUndoModule.this.application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        UndoManagerPanel undoManagerPanel = new UndoManagerPanel(undoModel);
        UndoManagerControlPanel undoManagerControlPanel = new UndoManagerControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(undoManagerPanel, undoManagerControlPanel);
        frameModule.setDialogTitle(dialog, undoManagerPanel.getResourceBundle());
        undoManagerControlPanel.setHandler((UndoManagerControlHandler.ControlActionType actionType) -> {
            dialog.close();
            dialog.dispose();
        });
        WindowUtils.addHeaderPanel(dialog.getWindow(), undoManagerPanel.getClass(), undoManagerPanel.getResourceBundle());
        dialog.showCentered(frameModule.getFrame());
    }

    @Nonnull
    @Override
    public UndoActions createUndoActions(UndoActionsHandler undoActionsHandler) {
        BasicUndoActions undoActions = new BasicUndoActions();
        undoActions.setUndoActionsHandler(undoActionsHandler);
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