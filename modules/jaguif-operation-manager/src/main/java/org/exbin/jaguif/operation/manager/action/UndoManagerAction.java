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
package org.exbin.jaguif.operation.manager.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.operation.manager.OperationManagerModule;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.operation.manager.gui.UndoManagerControlPanel;
import org.exbin.jaguif.operation.manager.gui.UndoManagerPanel;
import org.exbin.jaguif.operation.undo.api.UndoRedo;
import org.exbin.jaguif.operation.undo.api.UndoRedoState;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.operation.manager.controller.UndoManagerControlController;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Undo manager action.
 */
@ParametersAreNonnullByDefault
public class UndoManagerAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "editUndoManager";

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OperationManagerModule.class);

    private UndoRedoState undoHandler = null;
    private DialogParentComponent dialogParentComponent;

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        UndoManagerPanel undoManagerPanel = new UndoManagerPanel();
        if (undoHandler instanceof UndoRedo) {
            undoManagerPanel.setCommandSequence((UndoRedo) undoHandler);
        }
        UndoManagerControlPanel undoManagerControlPanel = new UndoManagerControlPanel();
        final WindowHandler windowHandler = windowModule.createDialog(undoManagerPanel, undoManagerControlPanel);
        windowModule.setWindowTitle(windowHandler, undoManagerPanel.getResourceBundle());
        undoManagerControlPanel.setController((UndoManagerControlController.ControlActionType actionType) -> {
            windowHandler.close();
            windowHandler.dispose();
        });
        windowModule.addHeaderPanel(windowHandler.getWindow(), undoManagerPanel.getClass(), undoManagerPanel.getResourceBundle());
        windowHandler.showCentered(dialogParentComponent.getComponent());
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerChangeListener(UndoRedoState.class, (instance) -> {
            undoHandler = instance;
            setEnabled(instance != null);
        });
        registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
            dialogParentComponent = instance;
        });
    }
}
