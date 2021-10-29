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
package org.exbin.framework.gui.undo.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.gui.undo.GuiUndoModule;
import org.exbin.framework.gui.undo.api.UndoActions;
import org.exbin.framework.gui.undo.api.UndoActionsHandler;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Basic clipboard action set.
 *
 * @version 0.2.0 2016/03/20
 * @author ExBin Project (http://exbin.org)
 */
public class BasicUndoActions implements UndoActions {

    public static final String EDIT_UNDO_ACTION_ID = "editUndoAction";
    public static final String EDIT_REDO_ACTION_ID = "editRedoAction";
    public static final String EDIT_UNDO_MANAGER_ACTION_ID = "editUndoManagerAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiUndoModule.class);

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
            ActionUtils.setupAction(undoAction, resourceBundle, EDIT_UNDO_ACTION_ID);
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
            ActionUtils.setupAction(redoAction, resourceBundle, EDIT_REDO_ACTION_ID);
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
            undoManagerAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
            ActionUtils.setupAction(undoManagerAction, resourceBundle, EDIT_UNDO_MANAGER_ACTION_ID);
        }
        return undoManagerAction;
    }
}
