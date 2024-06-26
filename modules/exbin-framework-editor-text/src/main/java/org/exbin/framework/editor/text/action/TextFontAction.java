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
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.handler.OptionsControlHandler;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowHandler;

/**
 * Text font action handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetFontAction";

    private ResourceBundle resourceBundle;
    private FileHandler fileHandler;

    public TextFontAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Override
            public void register(ComponentActivationManager manager) {
                manager.registerUpdateListener(FileHandler.class, (instance) -> {
                    fileHandler = instance;
                    setEnabled(fileHandler instanceof TextFontApi);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(fileHandler instanceof TextFontApi)) {
            return;
        }

        TextFontApi textFontApi = (TextFontApi) fileHandler;
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        final TextFontPanel fontPanel = new TextFontPanel();
        fontPanel.setStoredFont(textFontApi.getCurrentFont());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final WindowHandler dialog = windowModule.createDialog(fontPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
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
        dialog.showCentered(frameModule.getFrame());
    }

    public void setFileHandler(@Nullable FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
}
