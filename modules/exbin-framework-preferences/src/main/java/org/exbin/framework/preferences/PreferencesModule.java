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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;

/**
 * Implementation of framework popup module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PreferencesModule implements PreferencesModuleApi {

    private OptionsStorage appPreferences;

    public PreferencesModule() {
    }

    @Override
    public void setupAppPreferences(Class clazz) {
        java.util.prefs.Preferences prefsPreferences;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            prefsPreferences = new FilePreferencesFactory().userNodeForPackage(clazz);
        } else {
            prefsPreferences = java.util.prefs.Preferences.userNodeForPackage(clazz);
        }
        appPreferences = new PreferencesWrapper(prefsPreferences);
    }

    @Nonnull
    @Override
    public OptionsStorage getAppPreferences() {
        if (appPreferences == null) {
            setupAppPreferences(App.getModuleProvider().getManifestClass());
        }
        return appPreferences;
    }

    public void setAppPreferences(OptionsStorage appPreferences) {
        this.appPreferences = appPreferences;
    }
}
