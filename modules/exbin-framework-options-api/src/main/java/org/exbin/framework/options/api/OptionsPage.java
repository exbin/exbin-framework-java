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
package org.exbin.framework.options.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.preferences.api.Preferences;

/**
 * Interface for basic options page.
 *
 * @author ExBin Project (https://exbin.org)
 * @param <T> options data
 */
@ParametersAreNonnullByDefault
public interface OptionsPage<T extends OptionsData> {

    /**
     * Creates instance of options panel.
     *
     * @return panel
     */
    @Nonnull
    OptionsComponent<T> createPanel();

    /**
     * Returns options data.
     *
     * @return options data
     */
    @Nonnull
    T createOptions();

    /**
     * Loads options from preferences.
     *
     * @param preferences preferences
     * @param options output options
     */
    void loadFromPreferences(Preferences preferences, T options);

    /**
     * Saves options to preferences.
     *
     * @param preferences preferences
     * @param options input options
     */
    void saveToPreferences(Preferences preferences, T options);

    /**
     * Applies options.
     *
     * @param options options
     */
    void applyPreferencesChanges(T options);
}
