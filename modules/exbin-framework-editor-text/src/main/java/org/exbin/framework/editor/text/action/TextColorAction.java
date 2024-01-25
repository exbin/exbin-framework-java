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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.options.impl.TextColorOptionsImpl;
import org.exbin.framework.editor.text.options.gui.TextColorPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.preferences.TextColorPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.OptionsControlHandler;
import org.exbin.framework.utils.gui.OptionsControlPanel;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.file.api.FileHandler;

/**
 * Text color action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetColorAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public TextColorAction() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextColorService textColorService = new TextColorService() {
            @Override
            public Color[] getCurrentTextColors() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                TextPanel textPanel = (TextPanel) activeFile.get().getComponent();
                return textPanel.getCurrentColors();
            }

            @Override
            public Color[] getDefaultTextColors() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                TextPanel textPanel = (TextPanel) activeFile.get().getComponent();
                return textPanel.getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                TextPanel textPanel = (TextPanel) activeFile.get().getComponent();
                textPanel.setCurrentColors(colors);
            }
        };
        final TextColorPanel colorPanel = new TextColorPanel();

        colorPanel.setTextColorService(textColorService);

        colorPanel.setColorsFromArray(textColorService.getCurrentTextColors());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final DialogWrapper dialog = windowModule.createDialog(colorPanel, controlPanel);

        WindowUtils.addHeaderPanel(dialog.getWindow(), colorPanel.getClass(), colorPanel.getResourceBundle());
        windowModule.setDialogTitle(dialog, colorPanel.getResourceBundle());
        controlPanel.setHandler(
                (OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                            TextColorOptionsImpl options = new TextColorOptionsImpl();
                            colorPanel.saveToOptions(options);
                            TextColorPreferences textColorParameters = new TextColorPreferences(preferencesModule.getAppPreferences());
                            options.saveToPreferences(textColorParameters);
                        }
                        textColorService.setCurrentTextColors(colorPanel.getArrayFromColors());
                    }

                    dialog.close();
                    dialog.dispose();
                }
        );
        dialog.showCentered(windowModule.getFrame());
    }
}
