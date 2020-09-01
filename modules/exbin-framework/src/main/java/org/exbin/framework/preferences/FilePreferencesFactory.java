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

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * File preferences class.
 *
 * @version 0.2.1 2020/09/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FilePreferencesFactory implements PreferencesFactory {

    public static String preferenceFilename = "prefs.xml";
    public static String preferencesPath = null;
    private static final String PREFERENCES_FACTORY_PROPERTY = "java.util.prefs.PreferencesFactory";
    private static final String PREFERENCES_FACTORY_CLASS = "org.exbin.framework.preferences.FilePreferencesFactory";
    private Preferences userPreferences;
    private Preferences systemPreferences;

    @Nonnull
    @Override
    public Preferences systemRoot() {
        if (systemPreferences == null) {
            systemPreferences = new FilePreferences(null, "");
        }

        return systemPreferences;
    }

    @Nonnull
    @Override
    public Preferences userRoot() {
        if (userPreferences == null) {
            userPreferences = new FilePreferences(null, "");
        }

        return userPreferences;
    }

    @Nonnull
    public static File getPreferencesFile(String absolutePath) {
        if (preferencesPath == null) {
            preferencesPath = System.getProperty("user.home") + File.separator + ".java" + File.separator + ".userPrefs";
        }

        return new File(preferencesPath + absolutePath.replace('/', File.separatorChar) + File.separator + preferenceFilename);
    }

    @Nonnull
    public FilePreferences userNodeForPackage(Class clazz) {
        System.setProperty(PREFERENCES_FACTORY_PROPERTY, PREFERENCES_FACTORY_CLASS);
        FilePreferences preferences = (FilePreferences) userRoot();
        String[] packageComponents = clazz.getPackage().getName().split(Pattern.quote("."));
        for (String packageComponent : packageComponents) {
            preferences = (FilePreferences) preferences.childSpi(packageComponent);
        }

        return preferences;
    }
}
