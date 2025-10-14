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
package org.exbin.framework.ui.settings;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.ui.settings.gui.AppearanceSettingsPanel;
import org.exbin.framework.ui.settings.AppearanceOptions;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Appearance settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceSettingsComponent implements SettingsComponentProvider<AppearanceOptions> {

    @Nonnull
    @Override
    public SettingsComponent<AppearanceOptions> createComponent() {
        return new AppearanceSettingsPanel();
    }

/*    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(AppearanceSettingsPanel.class);
    }

    @Nonnull
    @Override
    public AppearanceSettings createOptions() {
        return new AppearanceSettings(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, AppearanceSettings options) {
        new AppearanceSettings(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, AppearanceSettings options) {
        options.copyTo(new AppearanceSettings(preferences));
    }

    @Override
    public void applyPreferencesChanges(AppearanceSettings options) {
        // TODO Drop frame module dependency / move frame options to frame module
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frame = frameModule.getFrameHandler();
        frame.setToolBarVisible(options.isShowToolBar());
        frame.setToolBarCaptionsVisible(options.isShowToolBarCaptions());
        frame.setStatusBarVisible(options.isShowStatusBar());
        frameModule.notifyFrameUpdated();
    } */
}
