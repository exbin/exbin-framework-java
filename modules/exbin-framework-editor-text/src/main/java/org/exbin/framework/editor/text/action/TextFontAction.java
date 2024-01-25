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

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.OptionsControlHandler;
import org.exbin.framework.utils.gui.OptionsControlPanel;
import org.exbin.framework.file.api.FileHandler;

/**
 * Text font action handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "toolsSetFontAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public TextFontAction() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
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

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextFontPanel fontPanel = new TextFontPanel();
        fontPanel.setStoredFont(textFontApi.getCurrentFont());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final DialogWrapper dialog = windowModule.createDialog(fontPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
        windowModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
        controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
            if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                    TextFontPreferences parameters = new TextFontPreferences(preferencesModule.getAppPreferences());
                    parameters.setUseDefaultFont(false);
                    parameters.setFont(fontPanel.getStoredFont());
                }
                textFontApi.setCurrentFont(fontPanel.getStoredFont());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(windowModule.getFrame());
    }
}
