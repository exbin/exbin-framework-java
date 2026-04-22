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
package org.exbin.jaguif.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.ui.api.UiModuleApi;
import org.exbin.jaguif.ui.settings.LanguageOptions;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.ui.settings.LanguageSettingsComponent;

/**
 * Module for user interface handling.
 */
@ParametersAreNonnullByDefault
public class UiModule implements UiModuleApi {

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

    @Override
    public void initSwingUi() {
        executePreInitActions();

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        LanguageOptions languageOptions = new LanguageOptions(optionsModule.getAppOptions());

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

        settingsManagement.registerSettingsOptions(LanguageOptions.class, (optionsStorage) -> new LanguageOptions(optionsStorage));
        
        settingsManagement.registerComponent(LanguageSettingsComponent.COMPONENT_ID, new LanguageSettingsComponent());
    }
}
