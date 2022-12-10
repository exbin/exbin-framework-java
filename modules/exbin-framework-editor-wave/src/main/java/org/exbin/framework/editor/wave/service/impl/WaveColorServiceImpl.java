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
package org.exbin.framework.editor.wave.service.impl;

import org.exbin.framework.editor.wave.service.*;
import java.awt.Color;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;

/**
 * Wave color service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorServiceImpl implements WaveColorService {

    private final EditorProvider editorProvider;

    public WaveColorServiceImpl(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public Color[] getCurrentWaveColors() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        return audioPanel.getAudioPanelColors();
    }

    @Nonnull
    @Override
    public Color[] getDefaultWaveColors() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        return audioPanel.getDefaultColors();
    }

    @Override
    public void setCurrentWaveColors(Color[] colors) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        audioPanel.setAudioPanelColors(colors);
    }
}
