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
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.InferenceOptions;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.settings.api.SettingsOptionsBuilder;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Options settings memory storage.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsOptionsStorage implements SettingsOptionsProvider {

    protected final OptionsStorage optionsStorage;
    protected final Map<Class<?>, SettingsOptions> settingsOptions = new HashMap<>();
    protected final OptionsSettingsManagement mainSettingsManager;

    public SettingsOptionsStorage() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        optionsStorage = optionsModule.createMemoryStorage();
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SettingsOptions> T getSettingsOptions(Class<T> settingsClass) {
        SettingsOptions instance = settingsOptions.get(settingsClass);
        if (instance == null) {
            SettingsOptionsBuilder optionsSettingsBuilder = mainSettingsManager.getOptionsSettingsBuilder(settingsClass);
            instance = optionsSettingsBuilder.createInstance(optionsStorage);
        }

        return (T) instance;
    }

    @Nonnull
    @Override
    public <T extends InferenceOptions> Optional<T> getInference(Class<T> inferenceClass) {
        // TODO
        return Optional.empty();
    }
}
