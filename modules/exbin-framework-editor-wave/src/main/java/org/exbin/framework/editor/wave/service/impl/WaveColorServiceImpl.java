/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.wave.service.impl;

import org.exbin.framework.editor.wave.service.*;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.panel.AudioPanel;
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
