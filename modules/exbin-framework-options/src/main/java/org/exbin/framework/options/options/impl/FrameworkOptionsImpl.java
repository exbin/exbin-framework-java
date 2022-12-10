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

    @Override
    public String getLookAndFeel() {
        return lookAndFeel;
    }

    @Override
    public void setLookAndFeel(String lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    @Override
    public Locale getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public void setLanguageLocale(Locale languageLocale) {
        this.languageLocale = languageLocale;
    }

    public void loadFromPreferences(FrameworkPreferences preferences) {
        lookAndFeel = preferences.getLookAndFeel();
        languageLocale = preferences.getLocale();
    }

    public void saveToParameters(FrameworkPreferences preferences) {
        preferences.setLocale(languageLocale);
        preferences.setLookAndFeel(lookAndFeel);
    }
}
