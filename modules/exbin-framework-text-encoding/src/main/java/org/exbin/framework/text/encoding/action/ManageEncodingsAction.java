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
package org.exbin.framework.text.encoding.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.text.encoding.gui.TextEncodingPanel;
import org.exbin.framework.text.encoding.gui.TextEncodingListPanel;
import org.exbin.framework.text.encoding.settings.TextEncodingOptions;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;
import org.exbin.framework.window.api.controller.OptionsControlController;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.text.encoding.CharsetListEncodingState;
import org.exbin.framework.text.encoding.ContextEncoding;

/**
 * Manage encodings action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ManageEncodingsAction extends AbstractAction {

    public static final String ACTION_ID = "manageEncodingsAction"; //NOI18N
    public static final String HELP_ID = "encoding"; //NOI18N

    private CharsetListEncodingState charsetEncodingState;

    public ManageEncodingsAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextEncoding.class, (instance) -> {
                    charsetEncodingState = instance instanceof CharsetListEncodingState ? (CharsetListEncodingState) instance : null;
                    setEnabled(charsetEncodingState != null);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextEncodingListPanel textEncodingPanel = new TextEncodingListPanel();
        textEncodingPanel.setPreferredSize(new Dimension(536, 358));
        textEncodingPanel.setEncodingList(charsetEncodingState.getEncodings());
        HelpLink helpLink = new HelpLink(HELP_ID);
        final OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(optionsControlPanel, helpLink);
        JPanel dialogPanel = windowModule.createDialogPanel(textEncodingPanel, optionsControlPanel);
        final WindowHandler dialog = windowModule.createDialog(dialogPanel, optionsControlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), textEncodingPanel.getClass(), textEncodingPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, textEncodingPanel.getResourceBundle());
        optionsControlPanel.setController((OptionsControlController.ControlActionType actionType) -> {
            if (actionType != OptionsControlController.ControlActionType.CANCEL) {
                charsetEncodingState.setEncodings(textEncodingPanel.getEncodingList());
                if (actionType == OptionsControlController.ControlActionType.SAVE) {
                    OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
                    TextEncodingOptions textEncodingPreferences = new TextEncodingOptions(preferencesModule.getAppOptions());
                    textEncodingPreferences.setEncodings(textEncodingPanel.getEncodingList());
                }
            }

            dialog.close();
            dialog.dispose();
        });
        textEncodingPanel.setController((List<String> usedEncodings, TextEncodingListPanel.EncodingsUpdate encodingsUpdate) -> {
            final TextEncodingPanel addEncodingPanel = new TextEncodingPanel();
            ResourceBundle addEncodingResourceBundle = addEncodingPanel.getResourceBundle();
            addEncodingPanel.setUsedEncodings(usedEncodings);
            DefaultControlPanel encodingsControlPanel = new DefaultControlPanel(addEncodingResourceBundle);
            helpModule.addLinkToControlPanel(encodingsControlPanel, helpLink);
            final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, encodingsControlPanel);
            windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingResourceBundle);
            windowModule.setWindowTitle(addEncodingDialog, addEncodingResourceBundle);

            encodingsControlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType == DefaultControlController.ControlActionType.OK) {
                    encodingsUpdate.update(addEncodingPanel.getEncodings());
                }

                addEncodingDialog.close();
                addEncodingDialog.dispose();
            });
            addEncodingDialog.showCentered(addEncodingPanel);
        });
        if (e.getSource() instanceof Component) {
            dialog.showCentered((Component) e.getSource());
        } else {
            dialog.show();
        }
    }
}
