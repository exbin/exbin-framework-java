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
package org.exbin.jaguif.addon.catalog;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.addon.catalog.service.impl.AddonCatalogServiceImpl;
import org.exbin.jaguif.addon.catalog.settings.AddonCatalogOptions;
import org.exbin.jaguif.addon.catalog.settings.AddonCatalogSettingsComponent;
import org.exbin.jaguif.addon.manager.api.AddonCatalogService;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Addon manager module.
 */
@ParametersAreNonnullByDefault
public class AddonCatalogModule implements Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(AddonCatalogModule.class);
    public static final String SETTINGS_PAGE_ID = "addonCatalog";

    private static boolean devMode = false;
    private String catalogPageUrl = "https://www.exbin.org/";

    public AddonCatalogModule() {
    }

    @Nonnull
    public String getAddonServiceUrl() {
        return catalogPageUrl + (devMode ? "addon-dev/" : "addon/");
    }

    @Nonnull
    public String getCatalogPageUrl() {
        return catalogPageUrl;
    }

    public void setCatalogPageUrl(String catalogPageUrl) {
        this.catalogPageUrl = catalogPageUrl;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        AddonCatalogModule.devMode = devMode;
    }
    
    @Nonnull
    public AddonCatalogService createCatalogService() {
        return new AddonCatalogServiceImpl();
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(AddonCatalogOptions.class, (optionsStorage) -> new AddonCatalogOptions(optionsStorage));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, null);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(AddonCatalogSettingsComponent.COMPONENT_ID, new AddonCatalogSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }
}
