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
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.operation.undo.api.UndoRedoState;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.operation.undo.api.UndoRedoController;
import org.exbin.framework.action.api.ActionContextChangeRegistration;

/**
 * Redo action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RedoAction extends AbstractAction implements ActionContextChange {

    public static final String EDIT_REDO_ACTION_ID = "editRedoAction";

    private ResourceBundle resourceBundle;
    private UndoRedoState undoRedo = null;

    public RedoAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, EDIT_REDO_ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (undoRedo instanceof UndoRedoController) {
            ((UndoRedoController) undoRedo).performRedo();
        }
    }

    @Override
    public void register(ActionContextChangeRegistration registrar) {
        registrar.registerUpdateListener(UndoRedoState.class, (instance) -> {
            undoRedo = instance;
            boolean canRedo = undoRedo != null && undoRedo.canRedo();
            setEnabled(canRedo);
        });
    }
}
