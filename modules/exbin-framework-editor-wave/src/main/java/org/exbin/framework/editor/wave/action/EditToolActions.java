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
 * Edit tool actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditToolActions {

    public static final String SELECTION_TOOL_ACTION_ID = "selectionToolAction";
    public static final String PENCIL_TOOL_ACTION_ID = "pencilToolAction";
    public static final String TOOLS_SELECTION_RADIO_GROUP_ID = "toolsSelectionRadioGroup";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    private Action selectionToolAction;
    private Action pencilToolAction;

    private XBWavePanel.ToolMode toolMode = XBWavePanel.ToolMode.SELECTION;

    public EditToolActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setToolMode(XBWavePanel.ToolMode mode) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        audioPanel.setToolMode(mode);
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(selectionToolAction, resourceBundle, SELECTION_TOOL_ACTION_ID);
            selectionToolAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            selectionToolAction.putValue(ActionConsts.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(pencilToolAction, resourceBundle, PENCIL_TOOL_ACTION_ID);
            pencilToolAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            pencilToolAction.putValue(ActionConsts.ACTION_RADIO_GROUP, TOOLS_SELECTION_RADIO_GROUP_ID);
            pencilToolAction.putValue(Action.SELECTED_KEY, toolMode == XBWavePanel.ToolMode.PENCIL);
        }
        return pencilToolAction;
    }
}
