/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.options.settings.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ActiveContextProvider;

/**
 * Interface for settings applier.
 */
@ParametersAreNonnullByDefault
public interface SettingsApplier {

    /**
     * Applies settings to specific context.
     *
     * @param contextProvider context provider
     * @param settingsProvider options settings provider
     */
    void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider);
}
