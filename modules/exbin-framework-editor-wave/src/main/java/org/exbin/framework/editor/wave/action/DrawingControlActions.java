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
package org.exbin.framework.editor.wave.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.xbup.audio.swing.XBWavePanel;

/**
 * Drawing mode control actions.
 *
 * @version 0.2.0 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DrawingControlActions {

    public static final String DOTS_MODE_ACTION_ID = "dotsModeAction";
    public static final String LINE_MODE_ACTION_ID = "lineModeAction";
    public static final String INTEGRAL_MODE_ACTION_ID = "integralModeAction";
    public static final String DRAWING_RADIO_GROUP_ID = "drawingRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action dotsModeAction;
    private Action lineModeAction;
    private Action integralModeAction;

    private XBWavePanel.DrawMode drawMode = XBWavePanel.DrawMode.DOTS_MODE;

    public DrawingControlActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setDrawMode(XBWavePanel.DrawMode mode) {
        AudioPanel activePanel = (AudioPanel) editorProvider;
        activePanel.setDrawMode(mode);
    }

    @Nonnull
    public Action getDotsModeAction() {
        if (dotsModeAction == null) {
            dotsModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioPanel) {
                        setDrawMode(XBWavePanel.DrawMode.DOTS_MODE);
                    }
                }
            };
            ActionUtils.setupAction(dotsModeAction, resourceBundle, DOTS_MODE_ACTION_ID);
            dotsModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            dotsModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
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
                    if (editorProvider instanceof AudioPanel) {
                        setDrawMode(XBWavePanel.DrawMode.LINE_MODE);
                    }
                }
            };
            ActionUtils.setupAction(lineModeAction, resourceBundle, LINE_MODE_ACTION_ID);
            lineModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            lineModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
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
                    if (editorProvider instanceof AudioPanel) {
                        setDrawMode(XBWavePanel.DrawMode.INTEGRAL_MODE);
                    }
                }
            };
            ActionUtils.setupAction(integralModeAction, resourceBundle, INTEGRAL_MODE_ACTION_ID);
            integralModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
            integralModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            integralModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.INTEGRAL_MODE);

        }

        return integralModeAction;
    }
}
