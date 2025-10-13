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
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsData;

/**
 * Addon manager options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerOptions implements SettingsData {

    public static final String KEY_ACTIVATED_VERSION = "addonManager.activatedVersion";

    private final OptionsStorage storage;

    public AddonManagerOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getActivatedVersion() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        ResourceBundle appBundle = languageModule.getAppBundle();
        String defaultVersion = appBundle.getString(ApplicationInfoKeys.APPLICATION_RELEASE) + "-SNAPSHOT";
        return storage.get(KEY_ACTIVATED_VERSION, defaultVersion);
    }

    public void setActivatedVersion(String activatedVersion) {
        storage.put(KEY_ACTIVATED_VERSION, activatedVersion);
    }

    @Override
    public void copyTo(SettingsData options) {
        AddonManagerOptions with = (AddonManagerOptions) options;
        with.setActivatedVersion(getActivatedVersion());
    }
}
