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
package org.exbin.framework.editor.wave.options.impl;

import org.exbin.framework.editor.wave.options.WaveColorOptions;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.wave.preferences.WaveColorPreferences;
import org.exbin.framework.options.api.OptionsData;

/**
 * Wave color options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorOptionsImpl implements OptionsData, WaveColorOptions {

    private boolean useDefaultColors;
    private Integer waveColor;
    private Integer waveFillColor;
    private Integer waveBackgroundColor;
    private Integer waveSelectionColor;
    private Integer waveCursorColor;
    private Integer waveCursorWaveColor;

    @Override
    public boolean isUseDefaultColors() {
        return useDefaultColors;
    }

    @Override
    public void setUseDefaultColors(boolean useDefaultColors) {
        this.useDefaultColors = useDefaultColors;
    }

    @Nullable
    @Override
    public Integer getWaveColor() {
        return waveColor;
    }

    @Override
    public void setWaveColor(@Nullable Integer waveColor) {
        this.waveColor = waveColor;
    }

    @Nullable
    @Override
    public Integer getWaveFillColor() {
        return waveFillColor;
    }

    @Override
    public void setWaveFillColor(@Nullable Integer waveFillColor) {
        this.waveFillColor = waveFillColor;
    }

    @Nullable
    @Override
    public Integer getWaveBackgroundColor() {
        return waveBackgroundColor;
    }

    @Override
    public void setWaveBackgroundColor(@Nullable Integer waveBackgroundColor) {
        this.waveBackgroundColor = waveBackgroundColor;
    }

    @Nullable
    @Override
    public Integer getWaveSelectionColor() {
        return waveSelectionColor;
    }

    @Override
    public void setWaveSelectionColor(@Nullable Integer waveSelectionColor) {
        this.waveSelectionColor = waveSelectionColor;
    }

    @Nullable
    @Override
    public Integer getWaveCursorColor() {
        return waveCursorColor;
    }

    @Override
    public void setWaveCursorColor(@Nullable Integer waveCursorColor) {
        this.waveCursorColor = waveCursorColor;
    }

    @Nullable
    @Override
    public Integer getWaveCursorWaveColor() {
        return waveCursorWaveColor;
    }

    @Override
    public void setWaveCursorWaveColor(@Nullable Integer waveCursorWaveColor) {
        this.waveCursorWaveColor = waveCursorWaveColor;
    }

    public void loadFromPreferences(WaveColorPreferences preferences) {
        useDefaultColors = preferences.isUseDefaultColors();
        waveColor = preferences.getWaveColor();
        waveFillColor = preferences.getWaveFillColor();
        waveBackgroundColor = preferences.getWaveBackgroundColor();
        waveSelectionColor = preferences.getWaveSelectionColor();
        waveCursorColor = preferences.getWaveCursorColor();
        waveCursorWaveColor = preferences.getWaveCursorWaveColor();
    }

    public void saveToPreferences(WaveColorPreferences preferences) {
        preferences.setUseDefaultColors(useDefaultColors);
        preferences.setWaveColor(waveColor);
        preferences.setWaveFillColor(waveFillColor);
        preferences.setWaveBackgroundColor(waveBackgroundColor);
        preferences.setWaveSelectionColor(waveSelectionColor);
        preferences.setWaveCursorColor(waveCursorColor);
        preferences.setWaveCursorWaveColor(waveCursorWaveColor);
    }

    public void setOptions(WaveColorOptionsImpl options) {
        useDefaultColors = options.useDefaultColors;
        waveColor = options.waveColor;
        waveFillColor = options.waveFillColor;
        waveBackgroundColor = options.waveBackgroundColor;
        waveSelectionColor = options.waveSelectionColor;
        waveCursorColor = options.waveCursorColor;
        waveCursorWaveColor = options.waveCursorWaveColor;
    }
}
