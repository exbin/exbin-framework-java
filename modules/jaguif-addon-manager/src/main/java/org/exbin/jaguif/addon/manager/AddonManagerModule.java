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
package org.exbin.jaguif.addon.manager;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ApplicationBundleKeys;
import org.exbin.jaguif.addon.manager.action.AddonManagerAction;
import org.exbin.jaguif.addon.manager.api.AddonManagerModuleApi;
import org.exbin.jaguif.addon.manager.contribution.AddonManagerContribution;
import org.exbin.jaguif.addon.manager.settings.AddonManagerOptions;
import org.exbin.jaguif.addon.manager.settings.AddonManagerSettingsComponent;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;

/**
 * Addon manager module.
 */
@ParametersAreNonnullByDefault
public class AddonManagerModule implements AddonManagerModuleApi {

    public static final String SETTINGS_PAGE_ID = "addonManager";
    private String manualLegacyGitHubUrl = "https://github.com/exbin/bined/releases/tag/";

    private static boolean devMode = false;
    private AddonManager addonManager = null;

    public AddonManagerModule() {
    }

    @Nonnull
    @Override
    public Action createAddonManagerAction() {
        return new AddonManagerAction();
    }

    @Override
    public void registerAddonManagerMenuItem() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = new AddonManagerContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE_LAST));
    }

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public void setDevMode(boolean devMode) {
        AddonManagerModule.devMode = devMode;
    }

    @Override
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(AddonManagerOptions.class, (optionsStorage) -> new AddonManagerOptions(optionsStorage));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, null);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(AddonManagerSettingsComponent.COMPONENT_ID, new AddonManagerSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    @Nonnull
    public AddonManager getAddonManager() {
        if (addonManager == null) {
            addonManager = new AddonManager();
        }
        return addonManager;
    }

    @Nonnull
    @Override
    public String getManualLegacyUrl() {
        ResourceBundle appBundle = App.getAppBundle();
        return manualLegacyGitHubUrl + appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
    }
}
