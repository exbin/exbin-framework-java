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
package org.exbin.framework.ui.theme.options.impl;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.ui.theme.api.preferences.UiPreferences;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.ui.theme.options.ThemeOptions;

/**
 * UI options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeOptionsImpl implements OptionsData, ThemeOptions {

    private String lookAndFeel = "";
    private String iconSet = "";
    private String renderingMode;
    private String guiScaling;
    private float guiScalingRate;
    private String fontAntialiasing;
    private boolean useScreenMenuBar;
    private String macOsAppearance;

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
    public String getIconSet() {
        return iconSet;
    }

    @Override
    public void setIconSet(String iconSet) {
        this.iconSet = iconSet;
    }

    @Nonnull
    @Override
    public String getRenderingMode() {
        return ObjectUtils.requireNonNull(renderingMode);
    }

    @Override
    public void setRenderingMode(String renderingMode) {
        this.renderingMode = renderingMode;
    }

    @Nonnull
    @Override
    public String getGuiScaling() {
        return ObjectUtils.requireNonNull(guiScaling);
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
        return ObjectUtils.requireNonNull(fontAntialiasing);
    }

    @Override
    public void setFontAntialiasing(String fontAntialiasing) {
        this.fontAntialiasing = fontAntialiasing;
    }

    @Override
    public boolean isUseScreenMenuBar() {
        return useScreenMenuBar;
    }

    @Override
    public void setUseScreenMenuBar(boolean useScreenMenuBar) {
        this.useScreenMenuBar = useScreenMenuBar;
    }

    @Nonnull
    @Override
    public String getMacOsAppearance() {
        return ObjectUtils.requireNonNull(macOsAppearance);
    }

    @Override
    public void setMacOsAppearance(String macOsAppearance) {
        this.macOsAppearance = macOsAppearance;
    }

    public void loadFromPreferences(UiPreferences preferences) {
        lookAndFeel = preferences.getLookAndFeel();
        iconSet = preferences.getIconSet();
        renderingMode = preferences.getRenderingMode();
        guiScaling = preferences.getGuiScaling();
        guiScalingRate = preferences.getGuiScalingRate();
        fontAntialiasing = preferences.getFontAntialiasing();
        useScreenMenuBar = preferences.isUseScreenMenuBar();
        macOsAppearance = preferences.getMacOsAppearance();
    }

    public void saveToParameters(UiPreferences preferences) {
        preferences.setLookAndFeel(lookAndFeel);
        preferences.setIconSet(iconSet);
        preferences.setRenderingMode(renderingMode);
        preferences.setGuiScaling(guiScaling);
        preferences.setGuiScalingRate(guiScalingRate);
        preferences.setFontAntialiasing(fontAntialiasing);
        preferences.setUseScreenMenuBar(useScreenMenuBar);
        preferences.setMacOsAppearance(macOsAppearance);
    }
}
