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
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.audio.swing.XBWavePanel;

/**
 * Tools selection control handler.
 *
 * @version 0.2.0 2016/01/30
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsSelectionHandler {

    public static String TOOLS_SELECTION_RADIO_GROUP_ID = "toolsSelectionRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action selectionToolAction;
    private Action pencilToolAction;

    private XBWavePanel.ToolMode toolMode = XBWavePanel.ToolMode.SELECTION;

    public ToolsSelectionHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);
    }

    public void init() {

        selectionToolAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    setToolMode(XBWavePanel.ToolMode.SELECTION);
                }
            }
        };
        ActionUtils.setupAction(selectionToolAction, resourceBundle, "selectionToolAction");
        selectionToolAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        selectionToolAction.putValue(ActionUtils.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
        selectionToolAction.putValue(Action.SELECTED_KEY, toolMode == XBWavePanel.ToolMode.SELECTION);

        pencilToolAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    setToolMode(XBWavePanel.ToolMode.PENCIL);
                }
            }
        };
        ActionUtils.setupAction(pencilToolAction, resourceBundle, "pencilToolAction");
        pencilToolAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        pencilToolAction.putValue(ActionUtils.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
        pencilToolAction.putValue(Action.SELECTED_KEY, toolMode == XBWavePanel.ToolMode.PENCIL);
    }

    public void setToolMode(XBWavePanel.ToolMode mode) {
        AudioPanel activePanel = (AudioPanel) editorProvider;
        activePanel.setToolMode(mode);
    }

    public Action getSelectionToolAction() {
        return selectionToolAction;
    }

    public Action getPencilToolAction() {
        return pencilToolAction;
    }
}
