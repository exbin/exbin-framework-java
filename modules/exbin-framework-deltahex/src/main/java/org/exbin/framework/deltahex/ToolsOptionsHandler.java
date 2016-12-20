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
package org.exbin.framework.deltahex;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.dialog.HexColorDialog;
import org.exbin.framework.deltahex.panel.HexColorPanelApi;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Tools options action handler.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsOptionsHandler {

    private int metaMask;
    private final ResourceBundle resourceBundle;

    private Action toolsSetFontAction;
    private Action toolsSetColorAction;

    private final HexEditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, HexEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        toolsSetFontAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                TextFontDialog dialog = new TextFontDialog(frameModule.getFrame(), true);
                dialog.setIconImage(application.getApplicationIcon());
                dialog.setLocationRelativeTo(dialog.getParent());
                editorProvider.showFontDialog(dialog);
            }
        };
        ActionUtils.setupAction(toolsSetFontAction, resourceBundle, "toolsSetFontAction");
        toolsSetFontAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                HexColorPanelApi textColorPanelFrame = new HexColorPanelApi() {
                    @Override
                    public Map<HexColorType, Color> getCurrentTextColors() {
                        return editorProvider.getCurrentColors();
                    }

                    @Override
                    public Map<HexColorType, Color> getDefaultTextColors() {
                        return editorProvider.getDefaultColors();
                    }

                    @Override
                    public void setCurrentTextColors(Map<HexColorType, Color> colors) {
                        editorProvider.setCurrentColors(colors);
                    }
                };
                HexColorDialog dialog = new HexColorDialog(frameModule.getFrame(), textColorPanelFrame, true);
                dialog.setIconImage(application.getApplicationIcon());
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.showDialog();
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
