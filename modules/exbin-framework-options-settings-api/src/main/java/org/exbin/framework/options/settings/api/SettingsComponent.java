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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.ComponentResourceProvider;
import org.exbin.framework.context.api.ApplicationContextProvider;

/**
 * Interface for basic options settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SettingsComponent extends ComponentResourceProvider {

    /**
     * Loads configuration from given settings options.
     *
     * @param settingsOptionsProvider settings options provider
     * @param applicationContextProvider context provider
     */
    void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ApplicationContextProvider applicationContextProvider);

    /**
     * Saves configuration from given settings options.
     *
     * @param settingsOptionsProvider settings options provider
     * @param applicationContextProvider context provider
     */
    void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ApplicationContextProvider applicationContextProvider);

    /**
     * Registers listener monitoring for settings changes.
     *
     * @param listener modified settings listener
     */
    void setSettingsModifiedListener(SettingsModifiedListener listener);
}
