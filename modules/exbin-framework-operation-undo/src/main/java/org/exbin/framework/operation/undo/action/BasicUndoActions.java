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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.operation.undo.OperationUndoModule;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.operation.undo.api.UndoRedoHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Undo handling action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicUndoActions implements UndoActions {

    public static final String EDIT_UNDO_ACTION_ID = "editUndoAction";
    public static final String EDIT_REDO_ACTION_ID = "editRedoAction";

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OperationUndoModule.class);

    public BasicUndoActions() {
    }

    @Override
    public Action createUndoAction() {
        UndoAction undoAction = new UndoAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(undoAction, resourceBundle, EDIT_UNDO_ACTION_ID);
        undoAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, ActionUtils.getMetaMask()));
        undoAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, undoAction);
        return undoAction;
    }

    @Override
    public Action createRedoAction() {
        RedoAction redoAction = new RedoAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(redoAction, resourceBundle, EDIT_REDO_ACTION_ID);
        redoAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | ActionUtils.getMetaMask()));
        redoAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, redoAction);
        return redoAction;
    }

    @ParametersAreNonnullByDefault
    private class UndoAction extends AbstractAction implements ActionActiveComponent {

        private UndoRedoHandler undoHandler = null;

        @Override
        public void actionPerformed(ActionEvent e) {
            undoHandler.performUndo();
        }

        @Override
        public void register(ComponentActivationManager manager) {
            manager.registerUpdateListener(UndoRedoHandler.class, (instance) -> {
                undoHandler = instance;
                boolean canUndo = undoHandler != null && undoHandler.canUndo();
                setEnabled(canUndo);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private class RedoAction extends AbstractAction implements ActionActiveComponent {

        private UndoRedoHandler undoHandler = null;

        @Override
        public void actionPerformed(ActionEvent e) {
            undoHandler.performRedo();
        }

        @Override
        public void register(ComponentActivationManager manager) {
            manager.registerUpdateListener(UndoRedoHandler.class, (instance) -> {
                undoHandler = instance;
                boolean canRedo = undoHandler != null && undoHandler.canRedo();
                setEnabled(canRedo);
            });
        }
    }
}
