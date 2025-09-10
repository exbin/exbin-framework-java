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
package org.exbin.framework.preferences.api.utils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;

/**
 * Test implementation of preferences module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TestPreferencesModule implements PreferencesModuleApi {

    private final OptionsStorage appPreferences = new EmptyPreferences();

    @Override
    public void setupAppPreferences(Class clazz) {
    }

    @Override
    public void setupAppPreferences(java.util.prefs.Preferences preferences) {
    }
    
    @Nonnull
    @Override
    public OptionsStorage getAppPreferences() {
        return appPreferences;
    }
}
