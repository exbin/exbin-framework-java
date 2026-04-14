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
package org.exbin.jaguif.text.font.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.text.font.gui.TextFontPanel;
import org.exbin.jaguif.text.font.settings.TextFontOptions;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.help.api.HelpLink;
import org.exbin.jaguif.help.api.HelpModuleApi;
import org.exbin.jaguif.window.api.gui.OptionsControlPanel;
import org.exbin.jaguif.window.api.controller.OptionsControlController;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.text.font.ContextFont;
import org.exbin.jaguif.text.font.TextFontState;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Text font action.
 */
@ParametersAreNonnullByDefault
public class TextFontAction extends AbstractAction {

    public static final String ACTION_ID = "textFont";
    public static final String HELP_ID = "choose-font";

    private TextFontState textFontSupported;
    private DialogParentComponent dialogParentComponent;

    public TextFontAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerChangeListener(ContextFont.class, (instance) -> {
                textFontSupported = instance instanceof TextFontState ? (TextFontState) instance : null;
                updateByContext();
            });
            registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
                updateByContext();
            });
        });
    }

    public void updateByContext() {
        setEnabled(textFontSupported != null && dialogParentComponent != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextFontPanel fontPanel = new TextFontPanel();
        fontPanel.setStoredFont(textFontSupported.getCurrentFont());
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, fontPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
        controlPanel.setController((OptionsControlController.ControlActionType actionType) -> {
            if (actionType != OptionsControlController.ControlActionType.CANCEL) {
                if (actionType == OptionsControlController.ControlActionType.SAVE) {
                    OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                    TextFontOptions fontOptions = new TextFontOptions(optionsModule.getAppOptions());
                    fontOptions.setUseDefaultFont(false);
                    fontOptions.setFont(fontPanel.getStoredFont());
                }
                textFontSupported.setCurrentFont(fontPanel.getStoredFont());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(dialogParentComponent.getComponent());
    }
}
