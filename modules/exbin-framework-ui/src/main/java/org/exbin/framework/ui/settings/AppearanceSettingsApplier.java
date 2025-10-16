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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsProvider;

/**
 * Appearance settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceSettingsApplier implements SettingsApplier {

    @Override
    public void applySettings(Object instance, SettingsProvider settingsProvider) {
        AppearanceOptions options = settingsProvider.getSettings(AppearanceOptions.class);
        // TODO Drop frame module dependency / move frame options to frame module
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frame = frameModule.getFrameHandler();
        frame.setToolBarVisible(options.isShowToolBar());
        frame.setToolBarCaptionsVisible(options.isShowToolBarCaptions());
        frame.setStatusBarVisible(options.isShowStatusBar());
        frameModule.notifyFrameUpdated();
    }
}
