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
package org.exbin.framework.editor.text.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.TextEditor;
import org.exbin.framework.editor.text.gui.TextGoToPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;

/**
 * Go to line action.
 *
 * @version 0.2.1 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToLineAction extends AbstractAction {

    public static final String ACTION_ID = "goToLineAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public GoToLineAction() {
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
    public void actionPerformed(ActionEvent e) {
        if (editorProvider instanceof TextEditor) {
            final TextPanel activePanel = (TextPanel) editorProvider.getActiveFile().getComponent();
            final TextGoToPanel goToPanel = new TextGoToPanel();
            goToPanel.initFocus();
            goToPanel.setMaxLine(activePanel.getLineCount());
            goToPanel.setCharPos(1);
            DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
            JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, controlPanel);
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
            WindowUtils.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle());
            frameModule.setDialogTitle(dialog, goToPanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    activePanel.gotoLine(goToPanel.getLine());
                    activePanel.gotoRelative(goToPanel.getCharPos());
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(frameModule.getFrame());
        }
    }
}
