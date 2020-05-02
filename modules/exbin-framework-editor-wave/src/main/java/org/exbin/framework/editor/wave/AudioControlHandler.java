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
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Audio control handler.
 *
 * @version 0.2.0 2016/01/23
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioControlHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action audioPlayAction;
    private Action audioStopAction;

    public AudioControlHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);
    }

    public void init() {
        audioPlayAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    AudioPanel activePanel = (AudioPanel) editorProvider;
                    activePanel.performPlay();
                }
            }
        };
        ActionUtils.setupAction(audioPlayAction, resourceBundle, "audioPlayAction");
        audioPlayAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));

        audioStopAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    AudioPanel activePanel = (AudioPanel) editorProvider;
                    activePanel.performStop();
                }
            }
        };
        ActionUtils.setupAction(audioStopAction, resourceBundle, "audioStopAction");
    }

    @Nonnull
    public Action getPlayAction() {
        return audioPlayAction;
    }

    @Nonnull
    public Action getStopAction() {
        return audioStopAction;
    }
}
