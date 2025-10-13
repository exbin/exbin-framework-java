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
package org.exbin.framework.options;

import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.api.OptionsModuleApi;

/**
 * Implementation of options module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsModule implements OptionsModuleApi {

    private OptionsStorage appOptions;

    public OptionsModule() {
    }

    @Override
    public void setupAppOptions(Class clazz) {
        java.util.prefs.Preferences prefsPreferences;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            prefsPreferences = new FilePreferencesFactory().userNodeForPackage(clazz);
        } else {
            prefsPreferences = java.util.prefs.Preferences.userNodeForPackage(clazz);
        }
        setupAppOptions(prefsPreferences);
    }

    @Override
    public void setupAppOptions(java.util.prefs.Preferences preferences) {
        appOptions = new PreferencesWrapper(preferences);
    }

    @Nonnull
    @Override
    public OptionsStorage getAppOptions() {
        if (appOptions == null) {
            setupAppOptions(App.getModuleProvider().getManifestClass());
        }
        return appOptions;
    }

    @Nonnull
    @Override
    public OptionsStorage createStreamPreferencesStorage(InputStream inputStream) {
        java.util.prefs.Preferences filePreferences = new StreamPreferences(inputStream);
        return new PreferencesWrapper(filePreferences);
    }

    public void setAppOptions(OptionsStorage appOptions) {
        this.appOptions = appOptions;
    }
}
