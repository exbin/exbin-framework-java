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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for options settings overrides.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SettingsOptionsOverrides extends SettingsOptionsProvider {

    /**
     * Add settings options override.
     *
     * @param <T> settings options type
     * @param <U> settings options type
     * @param settingsClass settings options class
     * @param overrideClass settings override class
     */
    <T extends SettingsOptions, U extends SettingsOptions> void overrideSettingsOptions(Class<T> settingsClass, Class<U> overrideClass);
}
