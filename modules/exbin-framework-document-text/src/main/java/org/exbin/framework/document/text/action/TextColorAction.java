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
package org.exbin.framework.document.text.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.document.text.settings.gui.TextColorPanel;
import org.exbin.framework.document.text.gui.TextPanel;
import org.exbin.framework.document.text.settings.TextColorOptions;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.window.api.controller.OptionsControlController;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.document.text.TextColorState;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.document.text.EditorTextPanelComponent;

/**
 * Text color action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetColorAction";

    private EditorTextPanelComponent textComponent;
    private DialogParentComponent dialogParentComponent;

    public TextColorAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                    textComponent = instance instanceof EditorTextPanelComponent ? (EditorTextPanelComponent) instance : null;
                    setEnabled(textComponent != null);
                });
                registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                    dialogParentComponent = instance;
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(textComponent != null)) {
            return;
        }

        TextPanel textPanel = (TextPanel) textComponent.getTextPanel();

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextColorState textColorService = new TextColorState() {
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
        colorPanel.setColorsFromArray(textColorService.getCurrentTextColors());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final WindowHandler dialog = windowModule.createDialog(colorPanel, controlPanel);

        windowModule.addHeaderPanel(dialog.getWindow(), colorPanel.getClass(), colorPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, colorPanel.getResourceBundle());
        controlPanel.setController((OptionsControlController.ControlActionType actionType) -> {
            if (actionType != OptionsControlController.ControlActionType.CANCEL) {
                if (actionType == OptionsControlController.ControlActionType.SAVE) {
                    OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                    TextColorOptions options = new TextColorOptions(optionsModule.createMemoryStorage());
                    // TODO colorPanel.saveToOptions(options);
                    options.copyTo(new TextColorOptions(optionsModule.getAppOptions()));
                }
                textColorService.setCurrentTextColors(colorPanel.getArrayFromColors());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(dialogParentComponent.getComponent());
    }
}
