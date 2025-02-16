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
package org.exbin.framework.addon.manager.options.page;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import org.exbin.framework.addon.manager.options.*;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.options.gui.AddonManagerOptionsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Addon manager options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerOptionsPage implements DefaultOptionsPage<AddonManagerOptions> {

    public static final String PAGE_ID = "addonManager";

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<AddonManagerOptions> createPanel() {
        return new AddonManagerOptionsPanel();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(AddonManagerOptionsPanel.class);
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
    }
}
