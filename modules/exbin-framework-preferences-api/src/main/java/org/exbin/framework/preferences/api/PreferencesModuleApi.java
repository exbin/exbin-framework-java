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
package org.exbin.framework.preferences.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework preferences module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface PreferencesModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(PreferencesModuleApi.class);

    /**
     * Setups application preferences using class instance as base.
     *
     * @param clazz class instance
     */
    void setupAppPreferences(Class clazz);

    /**
     * Setups application preferences using preferences instance.
     *
     * @param preferences preferences instance
     */
    void setupAppPreferences(java.util.prefs.Preferences preferences);

    /**
     * Returns application preferences.
     *
     * @return application preferences
     */
    @Nonnull
    OptionsStorage getAppPreferences();
}
