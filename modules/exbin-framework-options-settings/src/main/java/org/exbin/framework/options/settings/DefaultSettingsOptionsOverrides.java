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
package org.exbin.framework.options.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.InferenceOptions;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.settings.api.SettingsOptionsOverrides;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Default settings options overrides.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSettingsOptionsOverrides implements SettingsOptionsOverrides {

    protected final SettingsOptionsProvider settingsOptionsProvider;
    protected final Map<Class<? extends SettingsOptions>, Class<? extends SettingsOptions>> overrides = new HashMap<>();

    public DefaultSettingsOptionsOverrides(SettingsOptionsProvider settingsOptionsProvider) {
        this.settingsOptionsProvider = settingsOptionsProvider;
    }

    @Nonnull
    @Override
    public <T extends SettingsOptions, U extends SettingsOptions> void overrideSettingsOptions(Class<T> settingsClass, Class<U> overrideClass) {
        overrides.put(settingsClass, overrideClass);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends SettingsOptions> T getSettingsOptions(Class<T> settingsClass) {
        Class<? extends SettingsOptions> override = overrides.get(settingsClass);
        if (override != null) {
            return (T) settingsOptionsProvider.getSettingsOptions(override);
        }

        return (T) settingsOptionsProvider.getSettingsOptions(settingsClass);
    }

    @Nonnull
    @Override
    public <T extends InferenceOptions> Optional<T> getInferenceOptions(Class<T> inferenceClass) {
        return (Optional<T>) settingsOptionsProvider.getInferenceOptions(inferenceClass);
    }
}
