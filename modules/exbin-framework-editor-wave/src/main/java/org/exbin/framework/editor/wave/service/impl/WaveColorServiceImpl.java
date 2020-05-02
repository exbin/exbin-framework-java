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
package org.exbin.framework.editor.wave.service.impl;

import org.exbin.framework.editor.wave.service.*;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Wave color service.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
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
        return ((AudioPanel) editorProvider).getAudioPanelColors();
    }

    @Nonnull
    @Override
    public Color[] getDefaultWaveColors() {
        return ((AudioPanel) editorProvider).getDefaultColors();
    }

    @Override
    public void setCurrentWaveColors(Color[] colors) {
        ((AudioPanel) editorProvider).setAudioPanelColors(colors);
    }
}
