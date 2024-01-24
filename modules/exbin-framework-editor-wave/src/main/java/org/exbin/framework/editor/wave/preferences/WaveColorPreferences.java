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
package org.exbin.framework.editor.wave.preferences;

import java.util.Optional;
import javax.annotation.Nullable;
import org.exbin.framework.preferences.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.options.WaveColorOptions;

/**
 * Wave editor color preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorPreferences implements WaveColorOptions {

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

    @Override
    public boolean isUseDefaultColors() {
        return preferences.getBoolean(PREFERENCES_WAVE_COLOR_DEFAULT, true);
    }

    @Override
    public void setUseDefaultColors(boolean useDefault) {
        preferences.putBoolean(PREFERENCES_WAVE_COLOR_DEFAULT, useDefault);
    }

    @Nullable
    @Override
    public Integer getWaveColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_WAVE);
    }

    @Nullable
    @Override
    public Integer getWaveFillColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_WAVE_FILL);
    }

    @Nullable
    @Override
    public Integer getWaveBackgroundColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_BACKGROUND);
    }

    @Nullable
    @Override
    public Integer getWaveSelectionColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_SELECTION);
    }

    @Nullable
    @Override
    public Integer getWaveCursorColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_CURSOR);
    }

    @Nullable
    @Override
    public Integer getWaveCursorWaveColor() {
        return getColorAsInt(PREFERENCES_WAVE_COLOR_CURSOR_WAVE);
    }

    @Nullable
    private Integer getColorAsInt(String key) {
        Optional<String> value = preferences.get(key);
        return value.isPresent() ? Integer.valueOf(value.get()) : null;
    }

    @Override
    public void setWaveColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_WAVE, color);
    }

    public void setWaveColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_WAVE, color);
    }

    @Override
    public void setWaveFillColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_WAVE_FILL, color);
    }

    public void setWaveFillColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_WAVE_FILL, color);
    }

    @Override
    public void setWaveBackgroundColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_BACKGROUND, color);
    }

    public void setWaveBackgroundColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_BACKGROUND, color);
    }

    @Override
    public void setWaveSelectionColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_SELECTION, color);
    }

    public void setWaveSelectionColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_SELECTION, color);
    }

    @Override
    public void setWaveCursorColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_CURSOR, color);
    }

    public void setWaveCursorColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_CURSOR, color);
    }

    @Override
    public void setWaveCursorWaveColor(@Nullable Integer color) {
        setColor(PREFERENCES_WAVE_COLOR_CURSOR_WAVE, color);
    }

    public void setWaveCursorWaveColor(int color) {
        preferences.putInt(PREFERENCES_WAVE_COLOR_CURSOR_WAVE, color);
    }

    private void setColor(String preferenceName, @Nullable Integer color) {
        if (color == null) {
            preferences.remove(preferenceName);
        } else {
            preferences.putInt(preferenceName, color);
        }
    }
}
