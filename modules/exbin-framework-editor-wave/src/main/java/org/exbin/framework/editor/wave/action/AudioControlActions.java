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
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.file.api.FileHandler;

/**
 * Audio control handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioControlActions {

    public static final String AUDIO_PLAY_ACTION_ID = "audioPlayAction";
    public static final String AUDIO_STOP_ACTION_ID = "audioStopAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    private Action audioPlayAction;
    private Action audioStopAction;

    public AudioControlActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getPlayAction() {
        if (audioPlayAction == null) {
            audioPlayAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
                    audioPanel.performPlay();
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(audioPlayAction, resourceBundle, AUDIO_PLAY_ACTION_ID);
            audioPlayAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        }
        return audioPlayAction;
    }

    @Nonnull
    public Action getStopAction() {
        if (audioStopAction == null) {
            audioStopAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
                    audioPanel.performStop();
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(audioStopAction, resourceBundle, AUDIO_STOP_ACTION_ID);
        }
        return audioStopAction;
    }
}
