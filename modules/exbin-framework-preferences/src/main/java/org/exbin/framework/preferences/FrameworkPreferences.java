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
package org.exbin.framework.preferences;

import org.exbin.framework.preferences.api.Preferences;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameworkPreferences {

    public static final String PREFERENCES_LOOK_AND_FEEL = "lookAndFeel";
    public static final String PREFERENCES_LOCALE_LANGUAGE = "locale.language";
    public static final String PREFERENCES_LOCALE_COUNTRY = "locale.country";
    public static final String PREFERENCES_LOCALE_VARIANT = "locale.variant";
    public static final String PREFERENCES_LOCALE_TAG = "locale.tag";
    public static final String PREFERENCES_RENDERING_MODE = "rendering.renderingMode";
    public static final String PREFERENCES_RENDERING_SCALING = "rendering.guiScaling";
    public static final String PREFERENCES_RENDERING_SCALING_RATE = "rendering.guiScalingRate";
    public static final String PREFERENCES_RENDERING_FONT_ANTIALIASING = "rendering.fontAntialising";
    public static final String PREFERENCES_RENDERING_USE_SCREEN_MENU_BAR = "rendering.useScreenMenuBar";
    public static final String PREFERENCES_RENDERING_MACOS_APPEARANCE = "rendering.macOsAppearance";

    private final Preferences preferences;

    public FrameworkPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public String getLocaleLanguage() {
        return preferences.get(PREFERENCES_LOCALE_LANGUAGE, "");
    }

    @Nonnull
    public String getLocaleCountry() {
        return preferences.get(PREFERENCES_LOCALE_COUNTRY, "");
    }

    @Nonnull
    public String getLocaleVariant() {
        return preferences.get(PREFERENCES_LOCALE_VARIANT, "");
    }

    @Nonnull
    public String getLocaleTag() {
        return preferences.get(PREFERENCES_LOCALE_TAG, "");
    }

    @Nonnull
    public Locale getLocale() {
        String localeTag = getLocaleTag();
        if (!localeTag.trim().isEmpty()) {
            try {
                return Locale.forLanguageTag(localeTag);
            } catch (SecurityException ex) {
                // Ignore it in java webstart
            }
        }

        String localeLanguage = getLocaleLanguage();
        String localeCountry = getLocaleCountry();
        String localeVariant = getLocaleVariant();
        try {
            return new Locale(localeLanguage, localeCountry, localeVariant);
        } catch (SecurityException ex) {
            // Ignore it in java webstart
        }

        return Locale.ROOT;
    }

    public void setLocaleLanguage(String language) {
        preferences.put(PREFERENCES_LOCALE_LANGUAGE, language);
    }

    public void setLocaleCountry(String country) {
        preferences.put(PREFERENCES_LOCALE_COUNTRY, country);
    }

    public void setLocaleVariant(String variant) {
        preferences.put(PREFERENCES_LOCALE_VARIANT, variant);
    }

    public void setLocaleTag(String variant) {
        preferences.put(PREFERENCES_LOCALE_TAG, variant);
    }

    public void setLocale(Locale locale) {
        setLocaleTag(locale.toLanguageTag());
        setLocaleLanguage(locale.getLanguage());
        setLocaleCountry(locale.getCountry());
        setLocaleVariant(locale.getVariant());
    }

    @Nonnull
    public String getLookAndFeel() {
        return preferences.get(PREFERENCES_LOOK_AND_FEEL, "");
    }

    public void setLookAndFeel(String lookAndFeel) {
        preferences.put(PREFERENCES_LOOK_AND_FEEL, lookAndFeel);
    }

    @Nonnull
    public String getRenderingMode() {
        return preferences.get(PREFERENCES_RENDERING_MODE, "");
    }

    public void setRenderingMode(String renderingMode) {
        preferences.put(PREFERENCES_RENDERING_MODE, renderingMode);
    }

    @Nonnull
    public String getGuiScaling() {
        return preferences.get(PREFERENCES_RENDERING_SCALING, "");
    }

    public void setGuiScaling(String guiScaling) {
        preferences.put(PREFERENCES_RENDERING_SCALING, guiScaling);
    }

    public float getGuiScalingRate() {
        return preferences.getFloat(PREFERENCES_RENDERING_SCALING_RATE, 1f);
    }

    public void setGuiScalingRate(float guiScalingRate) {
        preferences.putFloat(PREFERENCES_RENDERING_SCALING_RATE, guiScalingRate);
    }

    @Nonnull
    public String getFontAntialiasing() {
        return preferences.get(PREFERENCES_RENDERING_FONT_ANTIALIASING, "");
    }

    public void setFontAntialiasing(String fontAntialiasing) {
        preferences.put(PREFERENCES_RENDERING_FONT_ANTIALIASING, fontAntialiasing);
    }

    public boolean isUseScreenMenuBar() {
        return preferences.getBoolean(PREFERENCES_RENDERING_USE_SCREEN_MENU_BAR, true);
    }

    public void setUseScreenMenuBar(boolean use) {
        preferences.putBoolean(PREFERENCES_RENDERING_USE_SCREEN_MENU_BAR, use);
    }

    @Nonnull
    public String getMacOsAppearance() {
        return preferences.get(PREFERENCES_RENDERING_MACOS_APPEARANCE, "");
    }

    public void setMacOsAppearance(String appearance) {
        preferences.put(PREFERENCES_RENDERING_MACOS_APPEARANCE, appearance);
    }
}
