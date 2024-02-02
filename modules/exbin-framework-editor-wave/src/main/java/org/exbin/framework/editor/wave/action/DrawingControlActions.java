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

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.editor.wave.AudioEditor;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.framework.file.api.FileHandler;

/**
 * Drawing mode control actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DrawingControlActions {

    public static final String DOTS_MODE_ACTION_ID = "dotsModeAction";
    public static final String LINE_MODE_ACTION_ID = "lineModeAction";
    public static final String INTEGRAL_MODE_ACTION_ID = "integralModeAction";
    public static final String DRAWING_RADIO_GROUP_ID = "drawingRadioGroup";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    private Action dotsModeAction;
    private Action lineModeAction;
    private Action integralModeAction;

    private XBWavePanel.DrawMode drawMode = XBWavePanel.DrawMode.DOTS_MODE;

    public DrawingControlActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setDrawMode(XBWavePanel.DrawMode mode) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        audioPanel.setDrawMode(mode);
    }

    @Nonnull
    public Action getDotsModeAction() {
        if (dotsModeAction == null) {
            dotsModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        setDrawMode(XBWavePanel.DrawMode.DOTS_MODE);
                    }
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(dotsModeAction, resourceBundle, DOTS_MODE_ACTION_ID);
            dotsModeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            dotsModeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
            dotsModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.DOTS_MODE);
        }
        return dotsModeAction;
    }

    @Nonnull
    public Action getLineModeAction() {
        if (lineModeAction == null) {
            lineModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        setDrawMode(XBWavePanel.DrawMode.LINE_MODE);
                    }
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(lineModeAction, resourceBundle, LINE_MODE_ACTION_ID);
            lineModeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            lineModeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
            lineModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.LINE_MODE);

        }
        return lineModeAction;
    }

    @Nonnull
    public Action getIntegralModeAction() {
        if (integralModeAction == null) {
            integralModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        setDrawMode(XBWavePanel.DrawMode.INTEGRAL_MODE);
                    }
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(integralModeAction, resourceBundle, INTEGRAL_MODE_ACTION_ID);
            integralModeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
            integralModeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            integralModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.INTEGRAL_MODE);

        }

        return integralModeAction;
    }
}
