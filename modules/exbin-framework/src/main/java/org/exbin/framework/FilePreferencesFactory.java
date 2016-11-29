/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import java.util.regex.Pattern;

/**
 * File preferences class.
 *
 * @version 0.2.0 2016/11/28
 * @author ExBin Project (http://exbin.org)
 */
public class FilePreferencesFactory implements PreferencesFactory {

    public static String PREFERENCE_FILENAME = "prefs.xml";
    public static String PREFERENCES_PATH = null;
    private Preferences userPreferences;
    private Preferences systemPreferences;

    @Override
    public Preferences systemRoot() {
        if (systemPreferences == null) {
            systemPreferences = new FilePreferences(null, "");
        }

        return systemPreferences;
    }

    @Override
    public Preferences userRoot() {
        if (userPreferences == null) {
            userPreferences = new FilePreferences(null, "");
        }

        return userPreferences;
    }

    public static File getPreferencesFile(String absolutePath) {
        if (PREFERENCES_PATH == null) {
            PREFERENCES_PATH = System.getProperty("user.home") + File.separator + ".java" + File.separator + ".userPrefs";
        }

        return new File(PREFERENCES_PATH + absolutePath.replace('/', File.separatorChar) + File.separator + PREFERENCE_FILENAME);
    }
    
    public FilePreferences userNodeForPackage(Class clazz) {
        System.setProperty("java.util.prefs.PreferencesFactory", "org.exbin.framework.FilePreferencesFactory");
        FilePreferences preferences = (FilePreferences) userRoot();
        String[] packageComponents = clazz.getPackage().getName().split(Pattern.quote("."));
        for (String packageComponent : packageComponents) {
            preferences = (FilePreferences) preferences.childSpi(packageComponent);
        }
        
        return preferences;
    }
}
