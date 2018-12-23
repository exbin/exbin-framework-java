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
package org.exbin.framework.bined;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.panel.BinaryColorPanel;
import org.exbin.framework.bined.panel.BinaryColorType;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.framework.bined.panel.BinaryColorPanelApi;

/**
 * Tools options action handler.
 *
 * @version 0.2.0 2017/01/04
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsOptionsHandler {

    private int metaMask;
    private final ResourceBundle resourceBundle;

    private Action toolsSetFontAction;
    private Action toolsSetColorAction;

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        toolsSetFontAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.exbin.framework.editor.text.ToolsOptionsHandler textOptionsHandler = new org.exbin.framework.editor.text.ToolsOptionsHandler(application, editorProvider);
                textOptionsHandler.init();
                Action textToolsSetFontAction = textOptionsHandler.getToolsSetFontAction();
                textToolsSetFontAction.actionPerformed(e);
            }
        };
        ActionUtils.setupAction(toolsSetFontAction, resourceBundle, "toolsSetFontAction");
        toolsSetFontAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final BinaryColorPanelApi textColorPanelFrame = new BinaryColorPanelApi() {
                    @Override
                    public Map<BinaryColorType, Color> getCurrentTextColors() {
                        return editorProvider.getCurrentColors();
                    }

                    @Override
                    public Map<BinaryColorType, Color> getDefaultTextColors() {
                        return editorProvider.getDefaultColors();
                    }

                    @Override
                    public void setCurrentTextColors(Map<BinaryColorType, Color> colors) {
                        editorProvider.setCurrentColors(colors);
                    }
                };

                final BinaryColorPanel hexColorPanel = new BinaryColorPanel();
                hexColorPanel.setPanelApi(textColorPanelFrame);
                hexColorPanel.setColorsFromMap(textColorPanelFrame.getCurrentTextColors());
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(hexColorPanel, controlPanel);

                final JDialog dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog, hexColorPanel.getClass(), hexColorPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, hexColorPanel.getResourceBundle());
                controlPanel.setHandler(new OptionsControlHandler() {
                    @Override
                    public void controlActionPerformed(OptionsControlHandler.ControlActionType actionType) {
                        if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                            textColorPanelFrame.setCurrentTextColors(hexColorPanel.getMapFromColors());
                            if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                                hexColorPanel.saveToPreferences(application.getAppPreferences());
                            }
                        }

                        WindowUtils.closeWindow(dialog);
                    }
                });
                WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.setVisible(true);
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
