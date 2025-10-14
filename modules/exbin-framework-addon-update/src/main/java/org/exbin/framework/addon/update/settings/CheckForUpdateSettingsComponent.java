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
package org.exbin.framework.addon.update.settings;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.update.settings.gui.ApplicationUpdateSettingsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Check for update on start settings page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateSettingsComponent implements SettingsComponentProvider<CheckForUpdateOptions> {
    
    @Override
    public SettingsComponent<CheckForUpdateOptions> createComponent() {
        return new ApplicationUpdateSettingsPanel();
    }

    /* @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(ApplicationUpdateSettingsPanel.class);
    }

    @Nonnull
    @Override
    public CheckForUpdateSettings createOptions() {
        return new CheckForUpdateSettings(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, CheckForUpdateSettings options) {
        new CheckForUpdateSettings(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, CheckForUpdateSettings options) {
        options.copyTo(new CheckForUpdateSettings(preferences));
    }

    @Override
    public void applyPreferencesChanges(CheckForUpdateSettings options) {
    } */
}
