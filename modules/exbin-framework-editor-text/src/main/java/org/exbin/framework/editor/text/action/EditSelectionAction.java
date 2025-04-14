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
package org.exbin.framework.editor.text.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.editor.text.SelectionRange;
import org.exbin.framework.editor.text.gui.EditSelectionPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Edit selection action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditSelectionAction extends AbstractAction {

    public static final String ACTION_ID = "editSelectionAction";

    private ResourceBundle resourceBundle;
    private JTextComponent component;

    public EditSelectionAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(Component.class, (instance) -> {
                    if (instance instanceof JTextComponent) {
                        component = (JTextComponent) instance;
                        setEnabled(true);
                    } else if (instance instanceof TextPanel) {
                        component = ((TextPanel) instance).getTextComponent();
                    } else {
                        setEnabled(false);
                    }
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final EditSelectionPanel editSelectionPanel = new EditSelectionPanel();
        editSelectionPanel.setCursorPosition(component.getCaretPosition());
        editSelectionPanel.setMaxPosition(component.getText().length());
        editSelectionPanel.setSelectionRange(new SelectionRange(component.getSelectionStart(), component.getSelectionEnd()));
        DefaultControlPanel controlPanel = new DefaultControlPanel(editSelectionPanel.getResourceBundle());
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(component, Dialog.ModalityType.APPLICATION_MODAL, editSelectionPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), editSelectionPanel.getClass(), editSelectionPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, editSelectionPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                editSelectionPanel.acceptInput();
                Optional<SelectionRange> selectionRange = editSelectionPanel.getSelectionRange();
                if (selectionRange.isPresent()) {
                    SelectionRange selectionRangeInst = selectionRange.get();
                    component.setSelectionStart(selectionRangeInst.getStart());
                    component.setSelectionEnd(selectionRangeInst.getEnd());
                } else {
                    component.setSelectionStart(-1);
                    component.setSelectionEnd(-1);
                }
                // component.revealCursor();
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(editSelectionPanel::initFocus);
        dialog.showCentered(component);
    }
}
