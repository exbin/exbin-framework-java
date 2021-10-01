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
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.AudioEditor;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Audio control handler.
 *
 * @version 0.2.0 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioControlActions {

    public static final String AUDIO_PLAY_ACTION_ID = "audioPlayAction";
    public static final String AUDIO_STOP_ACTION_ID = "audioStopAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action audioPlayAction;
    private Action audioStopAction;

    public AudioControlActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getPlayAction() {
        if (audioPlayAction == null) {
            audioPlayAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioEditor) {
                        AudioPanel activePanel = (AudioPanel) editorProvider.getActiveFile().getComponent();
                        activePanel.performPlay();
                    }
                }
            };
            ActionUtils.setupAction(audioPlayAction, resourceBundle, AUDIO_PLAY_ACTION_ID);
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
                    if (editorProvider instanceof AudioEditor) {
                        AudioPanel activePanel = (AudioPanel) editorProvider.getActiveFile().getComponent();
                        activePanel.performStop();
                    }
                }
            };
            ActionUtils.setupAction(audioStopAction, resourceBundle, AUDIO_STOP_ACTION_ID);
        }
        return audioStopAction;
    }
}
