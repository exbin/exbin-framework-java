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
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.file.api.FileHandler;

/**
 * Audio operation actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioOperationActions {

    public static final String AUDIO_REVERSE_ACTION_ID = "audioReverseAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    private Action audioReverseAction;

    public AudioOperationActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getRevertAction() {
        if (audioReverseAction == null) {
            audioReverseAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
                    audioPanel.performTransformReverse();
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(audioReverseAction, resourceBundle, AUDIO_REVERSE_ACTION_ID);
        }

        return audioReverseAction;
    }
}
