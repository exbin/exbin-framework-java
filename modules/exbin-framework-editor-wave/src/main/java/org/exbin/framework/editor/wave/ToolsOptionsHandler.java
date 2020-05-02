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
package org.exbin.framework.editor.wave;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.wave.options.gui.WaveColorPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.editor.wave.service.WaveColorService;
import org.exbin.framework.editor.wave.service.impl.WaveColorServiceImpl;

/**
 * Tools options action handler.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsOptionsHandler {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);

    private Action toolsSetColorAction;

    private final EditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
    }

    public void init() {
        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);

                WaveColorService waveColorService = new WaveColorServiceImpl(editorProvider);

                final WaveColorPanel waveColorPanel = new WaveColorPanel();
                waveColorPanel.setWaveColorService(waveColorService);
                waveColorPanel.setWaveColorsFromArray(((AudioPanel) editorProvider).getAudioPanelColors());
                DefaultControlPanel controlPanel = new DefaultControlPanel(waveColorPanel.getResourceBundle());
                JPanel dialogPanel = WindowUtils.createDialogPanel(waveColorPanel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), waveColorPanel.getClass(), waveColorPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, waveColorPanel.getResourceBundle());
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType == ControlActionType.OK) {
                        ((AudioPanel) editorProvider).setAudioPanelColors(waveColorPanel.getWaveColorsAsArray());
                    }

                    dialog.close();
                });
                dialog.showCentered((Component) e.getSource());
                dialog.dispose();
            }
        };
        ActionUtils.setupAction(toolsSetColorAction, resourceBundle, "toolsSetColorAction");
        toolsSetColorAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getToolsSetColorAction() {
        return toolsSetColorAction;
    }
}
