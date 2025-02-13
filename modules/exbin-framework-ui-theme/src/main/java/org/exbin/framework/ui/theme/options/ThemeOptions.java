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
package org.exbin.framework.ui.theme.options;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * UI theme options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeOptions implements OptionsData {

    public static final String KEY_LOOK_AND_FEEL = "lookAndFeel";
    public static final String KEY_ICONSET = "iconset";
    public static final String KEY_RENDERING_MODE = "rendering.renderingMode";
    public static final String KEY_RENDERING_SCALING = "rendering.guiScaling";
    public static final String KEY_RENDERING_SCALING_RATE = "rendering.guiScalingRate";
    public static final String KEY_RENDERING_FONT_ANTIALIASING = "rendering.fontAntialising";
    public static final String KEY_RENDERING_USE_SCREEN_MENU_BAR = "rendering.useScreenMenuBar";
    public static final String KEY_RENDERING_MACOS_APPEARANCE = "rendering.macOsAppearance";

    private final OptionsStorage storage;

    public ThemeOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getLookAndFeel() {
        return storage.get(KEY_LOOK_AND_FEEL, "");
    }

    public void setLookAndFeel(String lookAndFeel) {
        storage.put(KEY_LOOK_AND_FEEL, lookAndFeel);
    }

    @Nonnull
    public String getIconSet() {
        return storage.get(KEY_ICONSET, "");
    }

    public void setIconSet(String iconSet) {
        storage.put(KEY_ICONSET, iconSet);
    }

    @Nonnull
    public String getRenderingMode() {
        return storage.get(KEY_RENDERING_MODE, "");
    }

    public void setRenderingMode(String renderingMode) {
        storage.put(KEY_RENDERING_MODE, renderingMode);
    }

    @Nonnull
    public String getGuiScaling() {
        return storage.get(KEY_RENDERING_SCALING, "");
    }

    public void setGuiScaling(String guiScaling) {
        storage.put(KEY_RENDERING_SCALING, guiScaling);
    }

    public float getGuiScalingRate() {
        return storage.getFloat(KEY_RENDERING_SCALING_RATE, 1f);
    }

    public void setGuiScalingRate(float guiScalingRate) {
        storage.putFloat(KEY_RENDERING_SCALING_RATE, guiScalingRate);
    }

    @Nonnull
    public String getFontAntialiasing() {
        return storage.get(KEY_RENDERING_FONT_ANTIALIASING, "");
    }

    public void setFontAntialiasing(String fontAntialiasing) {
        storage.put(KEY_RENDERING_FONT_ANTIALIASING, fontAntialiasing);
    }

    public boolean isUseScreenMenuBar() {
        return storage.getBoolean(KEY_RENDERING_USE_SCREEN_MENU_BAR, true);
    }

    public void setUseScreenMenuBar(boolean use) {
        storage.putBoolean(KEY_RENDERING_USE_SCREEN_MENU_BAR, use);
    }

    @Nonnull
    public String getMacOsAppearance() {
        return storage.get(KEY_RENDERING_MACOS_APPEARANCE, "");
    }

    public void setMacOsAppearance(String appearance) {
        storage.put(KEY_RENDERING_MACOS_APPEARANCE, appearance);
    }

    @Override
    public void copyTo(OptionsData options) {
        ThemeOptions with = (ThemeOptions) options;
        with.setLookAndFeel(getLookAndFeel());
        with.setFontAntialiasing(getFontAntialiasing());
        with.setGuiScaling(getGuiScaling());
        with.setGuiScalingRate(getGuiScalingRate());
        with.setIconSet(getIconSet());
        with.setMacOsAppearance(getMacOsAppearance());
        with.setRenderingMode(getRenderingMode());
        with.setUseScreenMenuBar(isUseScreenMenuBar());
    }
}
