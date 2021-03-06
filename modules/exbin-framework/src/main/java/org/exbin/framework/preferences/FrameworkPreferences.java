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
package org.exbin.framework.preferences;

import org.exbin.framework.api.Preferences;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework preferences.
 *
 * @version 0.2.0 2019/06/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameworkPreferences {

    public static final String PREFERENCES_LOOK_AND_FEEL = "lookAndFeel";
    public static final String PREFERENCES_LOCALE_LANGUAGE = "locale.language";
    public static final String PREFERENCES_LOCALE_COUNTRY = "locale.country";
    public static final String PREFERENCES_LOCALE_VARIANT = "locale.variant";

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
    public Locale getLocale() {
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

    public void setLocale(Locale locale) {
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
}
