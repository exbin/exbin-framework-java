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
import org.exbin.framework.editor.wave.AudioEditor;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.xbup.audio.swing.XBWavePanel;

/**
 * Edit tool actions.
 *
 * @version 0.2.0 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditToolActions {

    public static final String SELECTION_TOOL_ACTION_ID = "selectionToolAction";
    public static final String PENCIL_TOOL_ACTION_ID = "pencilToolAction";
    public static final String TOOLS_SELECTION_RADIO_GROUP_ID = "toolsSelectionRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action selectionToolAction;
    private Action pencilToolAction;

    private XBWavePanel.ToolMode toolMode = XBWavePanel.ToolMode.SELECTION;

    public EditToolActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setToolMode(XBWavePanel.ToolMode mode) {
        AudioPanel activePanel = (AudioPanel) editorProvider.getActiveFile().getComponent();
        activePanel.setToolMode(mode);
    }

    @Nonnull
    public Action getSelectionToolAction() {
        if (selectionToolAction == null) {
            selectionToolAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        setToolMode(XBWavePanel.ToolMode.SELECTION);
                    }
                }
            };
            ActionUtils.setupAction(selectionToolAction, resourceBundle, SELECTION_TOOL_ACTION_ID);
            selectionToolAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            selectionToolAction.putValue(ActionUtils.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
            selectionToolAction.putValue(Action.SELECTED_KEY, toolMode == XBWavePanel.ToolMode.SELECTION);
        }
        return selectionToolAction;
    }

    @Nonnull
    public Action getPencilToolAction() {
        if (pencilToolAction == null) {
            pencilToolAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        setToolMode(XBWavePanel.ToolMode.PENCIL);
                    }
                }
            };
            ActionUtils.setupAction(pencilToolAction, resourceBundle, PENCIL_TOOL_ACTION_ID);
            pencilToolAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            pencilToolAction.putValue(ActionUtils.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
            pencilToolAction.putValue(Action.SELECTED_KEY, toolMode == XBWavePanel.ToolMode.PENCIL);
        }
        return pencilToolAction;
    }
}
