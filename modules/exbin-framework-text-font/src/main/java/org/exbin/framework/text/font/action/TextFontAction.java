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
package org.exbin.framework.text.font.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.text.font.gui.TextFontPanel;
import org.exbin.framework.text.font.options.TextFontOptions;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.window.api.controller.OptionsControlController;
import org.exbin.framework.text.font.TextFontController;

/**
 * Text font action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontAction extends AbstractAction {

    public static final String ACTION_ID = "textFontAction";
    public static final String HELP_ID = "choose-font";

    private TextFontController textFontSupported;
    private Component component;

    public TextFontAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
            manager.registerUpdateListener(TextFontController.class, (instance) -> {
                textFontSupported = instance;
                setEnabled(textFontSupported != null);
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextFontPanel fontPanel = new TextFontPanel();
        fontPanel.setStoredFont(textFontSupported.getCurrentFont());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));
        final WindowHandler dialog = windowModule.createDialog(fontPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
        controlPanel.setController((OptionsControlController.ControlActionType actionType) -> {
            if (actionType != OptionsControlController.ControlActionType.CANCEL) {
                if (actionType == OptionsControlController.ControlActionType.SAVE) {
                    PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                    TextFontOptions parameters = new TextFontOptions(preferencesModule.getAppPreferences());
                    parameters.setUseDefaultFont(false);
                    parameters.setFont(fontPanel.getStoredFont());
                }
                textFontSupported.setCurrentFont(fontPanel.getStoredFont());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(component);
    }
}
