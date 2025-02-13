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
package org.exbin.framework.ui.options;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * UI language options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageOptions implements OptionsData {

    public static final String KEY_LOCALE_LANGUAGE = "locale.language";
    public static final String KEY_LOCALE_COUNTRY = "locale.country";
    public static final String KEY_LOCALE_VARIANT = "locale.variant";
    public static final String KEY_LOCALE_TAG = "locale.tag";

    private final OptionsStorage preferences;

    public LanguageOptions(OptionsStorage preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public String getLocaleLanguage() {
        return preferences.get(KEY_LOCALE_LANGUAGE, "");
    }

    @Nonnull
    public String getLocaleCountry() {
        return preferences.get(KEY_LOCALE_COUNTRY, "");
    }

    @Nonnull
    public String getLocaleVariant() {
        return preferences.get(KEY_LOCALE_VARIANT, "");
    }

    @Nonnull
    public String getLocaleTag() {
        return preferences.get(KEY_LOCALE_TAG, "");
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
        preferences.put(KEY_LOCALE_LANGUAGE, language);
    }

    public void setLocaleCountry(String country) {
        preferences.put(KEY_LOCALE_COUNTRY, country);
    }

    public void setLocaleVariant(String variant) {
        preferences.put(KEY_LOCALE_VARIANT, variant);
    }

    public void setLocaleTag(String variant) {
        preferences.put(KEY_LOCALE_TAG, variant);
    }

    public void setLocale(Locale locale) {
        setLocaleTag(locale.toLanguageTag());
        setLocaleLanguage(locale.getLanguage());
        setLocaleCountry(locale.getCountry());
        setLocaleVariant(locale.getVariant());
    }

    @Override
    public void copyTo(OptionsData options) {
        LanguageOptions with = (LanguageOptions) options;
        with.setLocaleCountry(getLocaleCountry());
        with.setLocaleLanguage(getLocaleLanguage());
        with.setLocaleTag(getLocaleTag());
        with.setLocaleVariant(getLocaleVariant());
    }
}
