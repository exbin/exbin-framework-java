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
package org.exbin.framework.addon.manager;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.action.AddonManagerAction;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.settings.AddonManagerOptions;
import org.exbin.framework.addon.manager.settings.AddonManagerSettingsComponent;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.menu.api.MenuDefinitionManagement;

/**
 * Addon manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerModule implements AddonManagerModuleApi {

    public static final String SETTINGS_PAGE_ID = "addonManager";

    private static boolean devMode = false;
    private String addonServiceCoreUrl = "https://www.exbin.org/";
    private String manualLegacyGitHubUrl = "https://github.com/exbin/bined/releases/tag/";
    private AddonManager addonManager = null;

    public AddonManagerModule() {
    }

    @Nonnull
    @Override
    public Action createAddonManagerAction() {
        return new AddonManagerAction();
    }

    @Nonnull
    @Override
    public String getAddonServiceUrl() {
        return addonServiceCoreUrl + (devMode ? "addon-dev/" : "addon/");
    }

    @Nonnull
    @Override
    public String getAddonServiceCoreUrl() {
        return addonServiceCoreUrl;
    }

    @Override
    public void setAddonServiceCoreUrl(String addonServiceCoreUrl) {
        this.addonServiceCoreUrl = addonServiceCoreUrl;
    }

    @Nonnull
    @Override
    public String getManualLegacyUrl() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        ResourceBundle appBundle = languageModule.getAppBundle();
        return manualLegacyGitHubUrl + appBundle.getString(ApplicationInfoKeys.APPLICATION_RELEASE);
    }

    @Nonnull
    @Override
    public String getManualLegacyGitHubUrl() {
        return manualLegacyGitHubUrl;
    }

    @Override
    public void setManualLegacyGitHubUrl(String manualLegacyGitHubUrl) {
        this.manualLegacyGitHubUrl = manualLegacyGitHubUrl;
    }

    @Override
    public void registerAddonManagerMenuItem() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createAddonManagerAction());
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

        settingsManagement.registerOptionsSettings(AddonManagerOptions.class, (optionsStorage) -> new AddonManagerOptions(optionsStorage));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, null);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(AddonManagerSettingsComponent.COMPONENT_ID, new AddonManagerSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    @Nonnull
    public AddonManager getAddonManager() {
        if (addonManager == null) {
            addonManager = new AddonManager();
            addonManager.init();
        }
        return addonManager;
    }
}
