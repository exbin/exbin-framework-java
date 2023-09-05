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
package org.exbin.framework.options.options.impl;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.FrameworkPreferences;

/**
 * Framework options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameworkOptionsImpl implements OptionsData, FrameworkOptions {

    private String lookAndFeel;
    private Locale languageLocale;
    private String renderingMode;
    private String guiScaling;
    private float guiScalingRate;
    private String fontAntialiasing;

    @Nonnull
    @Override
    public String getLookAndFeel() {
        return lookAndFeel;
    }

    @Override
    public void setLookAndFeel(String lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    @Nonnull
    @Override
    public Locale getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public void setLanguageLocale(Locale languageLocale) {
        this.languageLocale = languageLocale;
    }

    @Nonnull
    @Override
    public String getRenderingMode() {
        return renderingMode;
    }

    @Override
    public void setRenderingMode(String renderingMode) {
        this.renderingMode = renderingMode;
    }

    @Nonnull
    @Override
    public String getGuiScaling() {
        return guiScaling;
    }

    @Override
    public void setGuiScaling(String guiScaling) {
        this.guiScaling = guiScaling;
    }

    @Override
    public float getGuiScalingRate() {
        return guiScalingRate;
    }

    @Override
    public void setGuiScalingRate(float guiScalingRate) {
        this.guiScalingRate = guiScalingRate;
    }

    @Nonnull
    @Override
    public String getFontAntialiasing() {
        return fontAntialiasing;
    }

    @Override
    public void setFontAntialiasing(String fontAntialiasing) {
        this.fontAntialiasing = fontAntialiasing;
    }

    public void loadFromPreferences(FrameworkPreferences preferences) {
        lookAndFeel = preferences.getLookAndFeel();
        languageLocale = preferences.getLocale();
        renderingMode = preferences.getRenderingMode();
        guiScaling = preferences.getGuiScaling();
        guiScalingRate = preferences.getGuiScalingRate();
        fontAntialiasing = preferences.getFontAntialiasing();
    }

    public void saveToParameters(FrameworkPreferences preferences) {
        preferences.setLocale(languageLocale);
        preferences.setLookAndFeel(lookAndFeel);
        preferences.setRenderingMode(renderingMode);
        preferences.setGuiScaling(guiScaling);
        preferences.setGuiScalingRate(guiScalingRate);
        preferences.setFontAntialiasing(fontAntialiasing);
    }
}
