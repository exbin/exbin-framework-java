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
import org.exbin.framework.editor.text.settings.gui.TextColorPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.settings.TextColorSettings;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.window.api.controller.OptionsControlController;
import org.exbin.framework.options.api.OptionsModuleApi;

/**
 * Text color action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetColorAction";

    private FileHandler fileHandler;
    private DialogParentComponent dialogParentComponent;

    public TextColorAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
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
                manager.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                    dialogParentComponent = instance;
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
        controlPanel.setController((OptionsControlController.ControlActionType actionType) -> {
            if (actionType != OptionsControlController.ControlActionType.CANCEL) {
                if (actionType == OptionsControlController.ControlActionType.SAVE) {
                    OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
                    TextColorSettings options = new TextColorSettings(new DefaultOptionsStorage());
                    colorPanel.saveToOptions(options);
                    options.copyTo(new TextColorSettings(preferencesModule.getAppOptions()));
                }
                textColorService.setCurrentTextColors(colorPanel.getArrayFromColors());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(dialogParentComponent.getComponent());
    }
}
