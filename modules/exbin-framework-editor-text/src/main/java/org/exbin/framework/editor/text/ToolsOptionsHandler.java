/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.panel.TextColorPanel;
import org.exbin.framework.editor.text.panel.TextColorPanelApi;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.editor.text.preferences.TextFontParameters;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;

/**
 * Tools options action handler.
 *
 * @version 0.2.1 2019/06/07
 * @author ExBin Project (http://exbin.org)
 */
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
                            TextFontParameters parameters = new TextFontParameters(application.getAppPreferences());
                            parameters.setDefaultFont(false);
                            parameters.setFont(fontPanel.getStoredFont());
                        }
                        ((TextFontApi) editorProvider).setCurrentFont(fontPanel.getStoredFont());
                    }

                    dialog.close();
                });
                WindowUtils.assignGlobalKeyListener(dialog.getWindow(), controlPanel.createOkCancelListener());
                dialog.center(dialog.getParent());
                dialog.show();
            }
        };
        ActionUtils.setupAction(toolsSetFontAction, resourceBundle, "toolsSetFontAction");
        toolsSetFontAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextColorPanelApi textColorPanelHandler = new TextColorPanelApi() {
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
                final TextColorPanel colorPanel = new TextColorPanel(textColorPanelHandler);
                colorPanel.setColorsFromArray(textColorPanelHandler.getCurrentTextColors());
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(colorPanel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), colorPanel.getClass(), colorPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, colorPanel.getResourceBundle());
                controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            colorPanel.saveToPreferences(application.getAppPreferences());
                        }
                        textColorPanelHandler.setCurrentTextColors(colorPanel.getArrayFromColors());
                    }

                    dialog.close();
                });
                WindowUtils.assignGlobalKeyListener(dialog.getWindow(), controlPanel.createOkCancelListener());
                dialog.center(dialog.getParent());
                dialog.show();
            }
        };
        ActionUtils.setupAction(toolsSetColorAction, resourceBundle, "toolsSetColorAction");
        toolsSetColorAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getToolsSetFontAction() {
        return toolsSetFontAction;
    }

    public Action getToolsSetColorAction() {
        return toolsSetColorAction;
    }
}
