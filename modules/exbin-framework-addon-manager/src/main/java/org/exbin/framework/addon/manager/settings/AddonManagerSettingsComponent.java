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
package org.exbin.framework.addon.manager.settings;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.settings.gui.AddonManagerSettingsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsData;

/**
 * Addon manager settings component provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerSettingsComponent implements SettingsComponentProvider<AddonManagerOptions> {

    @Nonnull
    @Override
    public SettingsComponent<AddonManagerOptions> createComponent() {
        return new AddonManagerSettingsPanel();
    }
/*
    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(AddonManagerSettingsPanel.class);
    }

    @Nonnull
    @Override
    public AddonManagerOptions createOptions() {
        return new AddonManagerOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, AddonManagerOptions options) {
        new AddonManagerOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, AddonManagerOptions options) {
        options.copyTo(new AddonManagerOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(AddonManagerOptions options) {
    } */
}
