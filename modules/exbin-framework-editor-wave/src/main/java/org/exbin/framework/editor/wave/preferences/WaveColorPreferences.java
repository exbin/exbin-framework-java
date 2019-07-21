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
package org.exbin.framework.editor.wave.preferences;

import javax.annotation.Nullable;
import org.exbin.framework.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Wave editor color preferences.
 *
 * @version 0.2.0 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorPreferences {

    public static final String PREFERENCES_WAVE_COLOR_DEFAULT = "waveColor.default";
    public static final String PREFERENCES_WAVE_COLOR_WAVE = "waveColor.wave";
    public static final String PREFERENCES_WAVE_COLOR_WAVE_FILL = "waveColor.waveFill";
    public static final String PREFERENCES_WAVE_COLOR_BACKGROUND = "waveColor.background";
    public static final String PREFERENCES_WAVE_COLOR_SELECTION = "waveColor.selection";
    public static final String PREFERENCES_WAVE_COLOR_CURSOR = "waveColor.cursor";
    public static final String PREFERENCES_WAVE_COLOR_CURSOR_WAVE = "waveColor.cursorWave";

    private final Preferences preferences;

    public WaveColorPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isUseDefaultColors() {
        return preferences.getBoolean(PREFERENCES_WAVE_COLOR_DEFAULT, true);
    }

    public void setUseDefaultColors(boolean useDefault) {
        preferences.putBoolean(PREFERENCES_WAVE_COLOR_DEFAULT, useDefault);
    }

    @Nullable
    public Integer getWaveColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_WAVE);
    }

    @Nullable
    public Integer getWaveFillColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_WAVE_FILL);
    }

    @Nullable
    public Integer getWaveBackgroundColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_BACKGROUND);
    }

    @Nullable
    public Integer getWaveSelectionColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_SELECTION);
    }

    @Nullable
    public Integer getWaveCursorColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_CURSOR);
    }

    @Nullable
    public Integer getWaveCursorWaveColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_CURSOR_WAVE);
    }

    @Nullable
    private Integer getColorAsInt(String key) {
        String value = preferences.get(key);
        return value == null ? null : Integer.valueOf(value);
    }

    public void setWaveColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_WAVE, Integer.toString(color));
    }

    public void setWaveFillColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_WAVE_FILL, Integer.toString(color));
    }

    public void setWaveBackgroundColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_BACKGROUND, Integer.toString(color));
    }

    public void setWaveSelectionColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_SELECTION, Integer.toString(color));
    }

    public void setWaveCursorColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_CURSOR, Integer.toString(color));
    }

    public void setWaveCursorWaveColor(int color) {
        preferences.put(PREFERENCES_WAVE_COLOR_CURSOR_WAVE, Integer.toString(color));
    }
}
