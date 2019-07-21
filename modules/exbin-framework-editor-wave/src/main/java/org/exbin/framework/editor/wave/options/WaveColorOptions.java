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
package org.exbin.framework.editor.wave.options;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.preferences.WaveColorPreferences;
import org.exbin.framework.gui.options.api.OptionsData;

/**
 * Wave color options.
 *
 * @version 0.2.0 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorOptions implements OptionsData {

    private boolean useDefaultColors;
    private Integer waveColor;
    private Integer waveFillColor;
    private Integer waveBackgroundColor;
    private Integer waveSelectionColor;
    private Integer waveCursorColor;
    private Integer waveCursorWaveColor;

    public boolean isUseDefaultColors() {
        return useDefaultColors;
    }

    public void setUseDefaultColors(boolean useDefaultColors) {
        this.useDefaultColors = useDefaultColors;
    }

    @Nullable
    public Integer getWaveColor() {
        return waveColor;
    }

    public void setWaveColor(@Nullable Integer waveColor) {
        this.waveColor = waveColor;
    }

    @Nullable
    public Integer getWaveFillColor() {
        return waveFillColor;
    }

    public void setWaveFillColor(@Nullable Integer waveFillColor) {
        this.waveFillColor = waveFillColor;
    }

    @Nullable
    public Integer getWaveBackgroundColor() {
        return waveBackgroundColor;
    }

    public void setWaveBackgroundColor(@Nullable Integer waveBackgroundColor) {
        this.waveBackgroundColor = waveBackgroundColor;
    }

    @Nullable
    public Integer getWaveSelectionColor() {
        return waveSelectionColor;
    }

    public void setWaveSelectionColor(@Nullable Integer waveSelectionColor) {
        this.waveSelectionColor = waveSelectionColor;
    }

    @Nullable
    public Integer getWaveCursorColor() {
        return waveCursorColor;
    }

    public void setWaveCursorColor(@Nullable Integer waveCursorColor) {
        this.waveCursorColor = waveCursorColor;
    }

    @Nullable
    public Integer getWaveCursorWaveColor() {
        return waveCursorWaveColor;
    }

    public void setWaveCursorWaveColor(@Nullable Integer waveCursorWaveColor) {
        this.waveCursorWaveColor = waveCursorWaveColor;
    }

    public void loadFromParameters(WaveColorPreferences preferences) {
        useDefaultColors = preferences.isUseDefaultColors();
        waveColor = preferences.getWaveColor();
        waveFillColor = preferences.getWaveFillColor();
        waveBackgroundColor = preferences.getWaveBackgroundColor();
        waveSelectionColor = preferences.getWaveSelectionColor();
        waveCursorColor = preferences.getWaveCursorColor();
        waveCursorWaveColor = preferences.getWaveCursorWaveColor();
    }

    public void saveToParameters(WaveColorPreferences preferences) {
        preferences.setUseDefaultColors(useDefaultColors);
        preferences.setWaveColor(waveColor);
        preferences.setWaveFillColor(waveFillColor);
        preferences.setWaveBackgroundColor(waveBackgroundColor);
        preferences.setWaveSelectionColor(waveSelectionColor);
        preferences.setWaveCursorColor(waveCursorColor);
        preferences.setWaveCursorWaveColor(waveCursorWaveColor);
    }

    public void setOptions(WaveColorOptions options) {
        useDefaultColors = options.useDefaultColors;
        waveColor = options.waveColor;
        waveFillColor = options.waveFillColor;
        waveBackgroundColor = options.waveBackgroundColor;
        waveSelectionColor = options.waveSelectionColor;
        waveCursorColor = options.waveCursorColor;
        waveCursorWaveColor = options.waveCursorWaveColor;
    }
}
