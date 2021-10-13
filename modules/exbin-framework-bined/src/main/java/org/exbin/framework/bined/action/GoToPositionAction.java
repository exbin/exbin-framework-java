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
package org.exbin.framework.bined.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.GoToBinaryPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.gui.file.api.FileDependentAction;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Go to position action.
 *
 * @version 0.2.1 2021/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToPositionAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "goToPositionAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public GoToPositionAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        setEnabled(activeFile.isPresent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isEmpty()) {
            throw new IllegalStateException();
        }

        ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
        final GoToBinaryPanel goToPanel = new GoToBinaryPanel();
        goToPanel.setCursorPosition(codeArea.getDataPosition());
        goToPanel.setMaxPosition(codeArea.getDataSize());
        DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, controlPanel);
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, editorProvider.getEditorComponent(), "", Dialog.ModalityType.APPLICATION_MODAL);
        WindowUtils.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, goToPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                goToPanel.acceptInput();
                codeArea.setCaretPosition(goToPanel.getTargetPosition());
                codeArea.revealCursor();
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(goToPanel::initFocus);
        dialog.showCentered(editorProvider.getEditorComponent());
    }
}
