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
package org.exbin.framework.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.settings.AppearanceSettingsComponent;
import org.exbin.framework.ui.settings.LanguageOptions;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.ui.settings.AppearanceOptions;

/**
 * Module for user interface handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UiModule implements UiModuleApi {

    public static final String SETTINGS_PAGE_ID = "appearance";
    public static final String SETTINGS_LANGUAGE_PAGE_ID = "language";

    private ResourceBundle resourceBundle;

    private List<Runnable> preInitActions = new ArrayList<>();
    private List<Runnable> postInitActions = new ArrayList<>();

    public UiModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(UiModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void initSwingUi() {
        executePreInitActions();

        OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
        LanguageOptions languageOptions = new LanguageOptions(preferencesModule.getAppOptions());

        // Switching language
        // TODO Move to language module, because language can be independent of UI
        Locale locale = languageOptions.getLocale();
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        languageModule.switchToLanguage(locale);

        executePostInitActions();
    }

    @Override
    public void addPreInitAction(Runnable runnable) {
        preInitActions.add(runnable);
    }

    @Override
    public void executePreInitActions() {
        for (Runnable runnable : preInitActions) {
            runnable.run();
        }
        preInitActions.clear();
    }

    @Override
    public void addPostInitAction(Runnable runnable) {
        postInitActions.add(runnable);
    }

    @Override
    public void executePostInitActions() {
        for (Runnable runnable : postInitActions) {
            runnable.run();
        }
        postInitActions.clear();
    }

    @Override
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerOptionsSettings(AppearanceOptions.class, (optionsStorage) -> new AppearanceOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(LanguageOptions.class, (optionsStorage) -> new LanguageOptions(optionsStorage));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(AppearanceSettingsComponent.COMPONENT_ID, new AppearanceSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
        /*
        OptionsGroup appearanceOptionsGroup = settingsModule.createOptionsGroup("appearance", getResourceBundle());
        settingsManagement.registerGroup(appearanceOptionsGroup);

        LanguageSettingsComponent languageOptionsPage = new LanguageSettingsComponent();
        settingsManagement.registerPage(languageOptionsPage);
        settingsManagement.registerPageRule(languageOptionsPage, new GroupOptionsPageRule(appearanceOptionsGroup));

        OptionsGroup uiOptionsGroup = settingsModule.createOptionsGroup("ui", getResourceBundle());
        settingsManagement.registerGroup(uiOptionsGroup);

        AppearanceSettingsComponent appearanceOptionsPage = new AppearanceSettingsComponent();
        settingsManagement.registerPage(appearanceOptionsPage);
        settingsManagement.registerPageRule(appearanceOptionsPage, new GroupOptionsPageRule(uiOptionsGroup)); */
    }
}
