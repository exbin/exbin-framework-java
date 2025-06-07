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
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.encoding.gui.TextEncodingPanel;
import org.exbin.framework.text.encoding.gui.TextEncodingListPanel;
import org.exbin.framework.text.encoding.options.TextEncodingOptions;
import org.exbin.framework.text.encoding.service.TextEncodingService;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.window.api.handler.OptionsControlHandler;

/**
 * Find/replace actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ManageEncodingsAction extends AbstractAction {

    public static final String ACTION_ID = "manageEncodingsAction"; //NOI18N
    public static final String HELP_ID = "encoding"; //NOI18N

    private TextEncodingService textEncodingService;
    private EncodingsHandler encodingsHandler;

    public ManageEncodingsAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    public void setTextEncodingService(TextEncodingService textEncodingService) {
        this.textEncodingService = textEncodingService;
    }

    public void setEncodingsHandler(EncodingsHandler encodingsHandler) {
        this.encodingsHandler = encodingsHandler;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextEncodingListPanel textEncodingPanel = new TextEncodingListPanel();
        textEncodingPanel.setPreferredSize(new Dimension(536, 358));
        textEncodingPanel.setEncodingList(textEncodingService.getEncodings());
        HelpLink helpLink = new HelpLink(HELP_ID);
        final OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(optionsControlPanel, helpLink);
        JPanel dialogPanel = windowModule.createDialogPanel(textEncodingPanel, optionsControlPanel);
        final WindowHandler dialog = windowModule.createDialog(dialogPanel, optionsControlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), textEncodingPanel.getClass(), textEncodingPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, textEncodingPanel.getResourceBundle());
        optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
            if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                textEncodingService.setEncodings(textEncodingPanel.getEncodingList());
                encodingsHandler.rebuildEncodings();
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                    TextEncodingOptions textEncodingPreferences = new TextEncodingOptions(preferencesModule.getAppPreferences());
                    textEncodingPreferences.setEncodings(textEncodingPanel.getEncodingList());
                }
            }

            dialog.close();
            dialog.dispose();
        });
        textEncodingPanel.setAddEncodingsOperation((List<String> usedEncodings, TextEncodingListPanel.EncodingsUpdate encodingsUpdate) -> {
            final TextEncodingPanel addEncodingPanel = new TextEncodingPanel();
            ResourceBundle addEncodingResourceBundle = addEncodingPanel.getResourceBundle();
            addEncodingPanel.setUsedEncodings(usedEncodings);
            DefaultControlPanel encodingsControlPanel = new DefaultControlPanel(addEncodingResourceBundle);
            helpModule.addLinkToControlPanel(encodingsControlPanel, helpLink);
            final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, encodingsControlPanel);
            windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingResourceBundle);
            windowModule.setWindowTitle(addEncodingDialog, addEncodingResourceBundle);

            encodingsControlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
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
