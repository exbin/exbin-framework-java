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
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.text.options.gui.TextColorPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.options.TextColorOptions;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.handler.OptionsControlHandler;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.options.api.DefaultOptionsStorage;

/**
 * Text color action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetColorAction";

    private ResourceBundle resourceBundle;
    private FileHandler fileHandler;

    public TextColorAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(FileHandler.class, (instance) -> {
                    fileHandler = instance;
                    setEnabled(fileHandler != null && (fileHandler.getComponent() instanceof TextPanel));
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(fileHandler != null && (fileHandler.getComponent() instanceof TextPanel))) {
            return;
        }

        TextPanel textPanel = (TextPanel) fileHandler.getComponent();

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        final TextColorService textColorService = new TextColorService() {
            @Override
            public Color[] getCurrentTextColors() {
                return textPanel.getCurrentColors();
            }

            @Override
            public Color[] getDefaultTextColors() {
                return textPanel.getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                textPanel.setCurrentColors(colors);
            }
        };
        final TextColorPanel colorPanel = new TextColorPanel();

        colorPanel.setTextColorService(textColorService);

        colorPanel.setColorsFromArray(textColorService.getCurrentTextColors());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final WindowHandler dialog = windowModule.createDialog(colorPanel, controlPanel);

        windowModule.addHeaderPanel(dialog.getWindow(), colorPanel.getClass(), colorPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, colorPanel.getResourceBundle());
        controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
            if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                    TextColorOptions options = new TextColorOptions(new DefaultOptionsStorage());
                    colorPanel.saveToOptions(options);
                    options.copyTo(new TextColorOptions(preferencesModule.getAppPreferences()));
                }
                textColorService.setCurrentTextColors(colorPanel.getArrayFromColors());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(frameModule.getFrame());
    }
}
