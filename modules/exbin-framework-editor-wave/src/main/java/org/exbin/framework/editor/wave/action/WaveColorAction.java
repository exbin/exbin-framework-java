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
package org.exbin.framework.editor.wave.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.wave.options.gui.WaveColorPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.editor.wave.service.WaveColorService;
import org.exbin.framework.editor.wave.service.impl.WaveColorServiceImpl;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.window.api.WindowHandler;

/**
 * Tools options action handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorAction extends AbstractAction {

    public static final String ACTION_ID = "toolsSetColorAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public WaveColorAction() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);

        WaveColorService waveColorService = new WaveColorServiceImpl(editorProvider);

        final WaveColorPanel waveColorPanel = new WaveColorPanel();
        waveColorPanel.setWaveColorService(waveColorService);
        waveColorPanel.setWaveColorsFromArray(audioPanel.getAudioPanelColors());
        DefaultControlPanel controlPanel = new DefaultControlPanel(waveColorPanel.getResourceBundle());
        final WindowHandler dialog = windowModule.createDialog(waveColorPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), waveColorPanel.getClass(), waveColorPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, waveColorPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                audioPanel.setAudioPanelColors(waveColorPanel.getWaveColorsAsArray());
            }

            dialog.close();
        });
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }
}
