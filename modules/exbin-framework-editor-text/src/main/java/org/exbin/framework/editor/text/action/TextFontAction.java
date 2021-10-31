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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileDependentAction;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.gui.OptionsControlPanel;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Text font action handler.
 *
 * @version 0.2.1 2021/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "toolsSetFontAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public TextFontAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
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
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }
        
        TextFontApi textFontApi = (TextFontApi) activeFile.get();

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        final TextFontPanel fontPanel = new TextFontPanel();
        fontPanel.setStoredFont(textFontApi.getCurrentFont());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
        controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
            if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    TextFontPreferences parameters = new TextFontPreferences(application.getAppPreferences());
                    parameters.setUseDefaultFont(false);
                    parameters.setFont(fontPanel.getStoredFont());
                }
                textFontApi.setCurrentFont(fontPanel.getStoredFont());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(frameModule.getFrame());
    }
}
