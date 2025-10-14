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
package org.exbin.framework.options.settings.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Interface for options settings page.
 *
 * @author ExBin Project (https://exbin.org)
 * @param <T> options data
 */
@ParametersAreNonnullByDefault
public interface SettingsPage<T extends SettingsOptions> {

    /**
     * Returns settings page id.
     *
     * @return id
     */
    @Nonnull
    String getId();

    /**
     * Creates instance of settings component.
     *
     * @return panel
     */
    @Nonnull
    SettingsComponent<T> createComponent();

    /**
     * Returns options data.
     *
     * @return options data
     */
    @Nonnull
    T createOptions();

    /**
     * Loads options from options storage.
     *
     * @param optionsStorage options storage
     * @param options output options
     */
    void loadFromOptions(OptionsStorage optionsStorage, T options);

    /**
     * Saves options to options storage.
     *
     * @param optionsStorage options storage
     * @param options input options
     */
    void saveToOptions(OptionsStorage optionsStorage, T options);
}
