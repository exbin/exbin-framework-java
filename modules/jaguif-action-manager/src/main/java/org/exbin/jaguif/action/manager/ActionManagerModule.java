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
package org.exbin.jaguif.action.manager;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.manager.settings.ActionManagerOptions;
import org.exbin.jaguif.action.manager.settings.KeyMapSettingsComponent;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Action manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManagerModule implements org.exbin.jaguif.Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ActionManagerModule.class);

    public static final String SETTINGS_PAGE_ID = "keymap";

    private ResourceBundle resourceBundle;

    public ActionManagerModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ActionManagerModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(ActionManagerOptions.class, (optionsStorage) -> new ActionManagerOptions(optionsStorage));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, getResourceBundle());
        settingsManagement.registerPage(pageContribution);

        SettingsComponentContribution settingsContribution = settingsManagement.registerComponent(KeyMapSettingsComponent.COMPONENT_ID, new KeyMapSettingsComponent());
        settingsManagement.registerSettingsRule(settingsContribution, new SettingsPageContributionRule(pageContribution, SettingsPageContributionRule.Parameter.EXPAND_VERTICALLY));
    }
}
