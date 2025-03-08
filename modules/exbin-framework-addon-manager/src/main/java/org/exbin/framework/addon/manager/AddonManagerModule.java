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
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.addon.manager.action.AddonManagerAction;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.options.page.AddonManagerOptionsPage;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;

/**
 * Addon manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerModule implements AddonManagerModuleApi {

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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createAddonManagerAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE_LAST));
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
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);
        AddonManagerOptionsPage addonManagerOptionsPage = new AddonManagerOptionsPage();
        optionsPageManagement.registerPage(addonManagerOptionsPage);
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
