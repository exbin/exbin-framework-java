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
package org.exbin.framework.operation.undo.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.operation.undo.OperationUndoModule;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.operation.undo.api.UndoActionsHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Basic clipboard action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class BasicUndoActions implements UndoActions {

    public static final String EDIT_UNDO_ACTION_ID = "editUndoAction";
    public static final String EDIT_REDO_ACTION_ID = "editRedoAction";
    public static final String EDIT_UNDO_MANAGER_ACTION_ID = "editUndoManagerAction";

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OperationUndoModule.class);

    private UndoActionsHandler undoHandler = null;
    private Action undoAction = null;
    private Action redoAction = null;
    private Action undoManagerAction = null;

    public BasicUndoActions() {
    }

    @Override
    public void updateUndoActions() {
        boolean canUndo = undoHandler != null && undoHandler.canUndo();
        boolean canRedo = undoHandler != null && undoHandler.canRedo();
        if (undoAction != null) {
            undoAction.setEnabled(canUndo);
        }
        if (redoAction != null) {
            redoAction.setEnabled(canRedo);
        }
    }

    @Override
    public void setUndoActionsHandler(UndoActionsHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    @Override
    public Action getUndoAction() {
        if (undoAction == null) {
            undoAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    undoHandler.performUndo();
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(undoAction, resourceBundle, EDIT_UNDO_ACTION_ID);
            undoAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, ActionUtils.getMetaMask()));
            undoAction.setEnabled(false);
        }
        return undoAction;
    }

    @Override
    public Action getRedoAction() {
        if (redoAction == null) {
            redoAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    undoHandler.performRedo();
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(redoAction, resourceBundle, EDIT_REDO_ACTION_ID);
            redoAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | ActionUtils.getMetaMask()));
            redoAction.setEnabled(false);
        }
        return redoAction;
    }

    @Override
    public Action getUndoManagerAction() {
        if (undoManagerAction == null) {
            undoManagerAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    undoHandler.performUndoManager();
                }
            };
            undoManagerAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(undoManagerAction, resourceBundle, EDIT_UNDO_MANAGER_ACTION_ID);
        }
        return undoManagerAction;
    }
}
