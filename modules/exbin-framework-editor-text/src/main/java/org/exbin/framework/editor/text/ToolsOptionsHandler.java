/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.options.impl.TextColorOptionsImpl;
import org.exbin.framework.editor.text.options.gui.TextColorPanel;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.preferences.TextColorPreferences;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.gui.OptionsControlPanel;
import org.exbin.framework.editor.text.service.TextColorService;

/**
 * Tools options action handler.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolsOptionsHandler {

    private final ResourceBundle resourceBundle;

    private Action toolsSetFontAction;
    private Action toolsSetColorAction;

    private final EditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorTextModule.class);
    }

    public void init() {
        toolsSetFontAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextFontPanel fontPanel = new TextFontPanel();
                fontPanel.setStoredFont(((TextFontApi) editorProvider).getCurrentFont());
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            TextFontPreferences parameters = new TextFontPreferences(application.getAppPreferences());
                            parameters.setUseDefaultFont(false);
                            parameters.setFont(fontPanel.getStoredFont());
                        }
                        ((TextFontApi) editorProvider).setCurrentFont(fontPanel.getStoredFont());
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(frameModule.getFrame());
            }
        };
        ActionUtils.setupAction(toolsSetFontAction, resourceBundle, "toolsSetFontAction");
        toolsSetFontAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextColorService textColorService = new TextColorService() {
                    @Override
                    public Color[] getCurrentTextColors() {
                        return ((TextPanel) editorProvider).getCurrentColors();
                    }

                    @Override
                    public Color[] getDefaultTextColors() {
                        return ((TextPanel) editorProvider).getDefaultColors();
                    }

                    @Override
                    public void setCurrentTextColors(Color[] colors) {
                        ((TextPanel) editorProvider).setCurrentColors(colors);
                    }
                };
                final TextColorPanel colorPanel = new TextColorPanel();
                colorPanel.setTextColorService(textColorService);
                colorPanel.setColorsFromArray(textColorService.getCurrentTextColors());
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(colorPanel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), colorPanel.getClass(), colorPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, colorPanel.getResourceBundle());
                controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            TextColorOptionsImpl options = new TextColorOptionsImpl();
                            colorPanel.saveToOptions(options);
                            TextColorPreferences textColorParameters = new TextColorPreferences(application.getAppPreferences());
                            options.saveToPreferences(textColorParameters);
                        }
                        textColorService.setCurrentTextColors(colorPanel.getArrayFromColors());
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(frameModule.getFrame());
            }
        };
        ActionUtils.setupAction(toolsSetColorAction, resourceBundle, "toolsSetColorAction");
        toolsSetColorAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getToolsSetFontAction() {
        return toolsSetFontAction;
    }

    @Nonnull
    public Action getToolsSetColorAction() {
        return toolsSetColorAction;
    }
}
