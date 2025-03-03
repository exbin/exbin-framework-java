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
package org.exbin.framework.ui.options.page;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.ui.gui.AppearanceOptionsPanel;
import org.exbin.framework.ui.options.AppearanceOptions;

/**
 * Appearance options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceOptionsPage implements DefaultOptionsPage<AppearanceOptions> {

    public static final String PAGE_ID = "appearance";

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<AppearanceOptions> createComponent() {
        return new AppearanceOptionsPanel();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(AppearanceOptionsPanel.class);
    }

    @Nonnull
    @Override
    public AppearanceOptions createOptions() {
        return new AppearanceOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, AppearanceOptions options) {
        new AppearanceOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, AppearanceOptions options) {
        options.copyTo(new AppearanceOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(AppearanceOptions options) {
        // TODO Drop frame module dependency / move frame options to frame module
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frame = frameModule.getFrameHandler();
        frame.setToolBarVisible(options.isShowToolBar());
        frame.setToolBarCaptionsVisible(options.isShowToolBarCaptions());
        frame.setStatusBarVisible(options.isShowStatusBar());
        frameModule.notifyFrameUpdated();
    }
}
