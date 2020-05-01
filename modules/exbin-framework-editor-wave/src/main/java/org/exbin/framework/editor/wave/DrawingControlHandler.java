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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.panel.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.audio.swing.XBWavePanel;

/**
 * Drawing mode control handler.
 *
 * @version 0.2.0 2016/01/23
 * @author ExBin Project (http://exbin.org)
 */
public class DrawingControlHandler {

    public static String DRAWING_RADIO_GROUP_ID = "drawingRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action dotsModeAction;
    private Action lineModeAction;
    private Action integralModeAction;

    private XBWavePanel.DrawMode drawMode = XBWavePanel.DrawMode.DOTS_MODE;

    public DrawingControlHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);
    }

    public void init() {
        dotsModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    setDrawMode(XBWavePanel.DrawMode.DOTS_MODE);
                }
            }
        };
        ActionUtils.setupAction(dotsModeAction, resourceBundle, "dotsModeAction");
        dotsModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        dotsModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
        dotsModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.DOTS_MODE);

        lineModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    setDrawMode(XBWavePanel.DrawMode.LINE_MODE);
                }
            }
        };
        ActionUtils.setupAction(lineModeAction, resourceBundle, "lineModeAction");
        lineModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        lineModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
        lineModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.LINE_MODE);

        integralModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    setDrawMode(XBWavePanel.DrawMode.INTEGRAL_MODE);
                }
            }
        };
        ActionUtils.setupAction(integralModeAction, resourceBundle, "integralModeAction");
        integralModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, DRAWING_RADIO_GROUP_ID);
        integralModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        integralModeAction.putValue(Action.SELECTED_KEY, drawMode == XBWavePanel.DrawMode.INTEGRAL_MODE);
    }

    public void setDrawMode(XBWavePanel.DrawMode mode) {
        AudioPanel activePanel = (AudioPanel) editorProvider;
        activePanel.setDrawMode(mode);
    }

    public Action getDotsModeAction() {
        return dotsModeAction;
    }

    public Action getLineModeAction() {
        return lineModeAction;
    }

    public Action getIntegralModeAction() {
        return integralModeAction;
    }
}
