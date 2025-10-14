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
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Appearance options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceOptions implements SettingsOptions {

    public static final String KEY_TOOLBAR_VISIBLE = "toolBar.visible";
    public static final String KEY_TOOLBAR_CAPTIONS = "toolBar.captions";
    public static final String KEY_STATUSBAR_VISIBLE = "statusBar.visible";

    private final OptionsStorage storage;

    public AppearanceOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isShowToolBar() {
        return storage.getBoolean(KEY_TOOLBAR_VISIBLE, true);
    }

    public boolean isShowToolBarCaptions() {
        return storage.getBoolean(KEY_TOOLBAR_CAPTIONS, true);
    }

    public boolean isShowStatusBar() {
        return storage.getBoolean(KEY_STATUSBAR_VISIBLE, true);
    }

    public void setShowToolBar(boolean show) {
        storage.putBoolean(KEY_TOOLBAR_VISIBLE, show);
    }

    public void setShowToolBarCaptions(boolean show) {
        storage.putBoolean(KEY_TOOLBAR_CAPTIONS, show);
    }

    public void setShowStatusBar(boolean show) {
        storage.putBoolean(KEY_STATUSBAR_VISIBLE, show);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        AppearanceOptions with = (AppearanceOptions) options;
        with.setShowStatusBar(isShowStatusBar());
        with.setShowToolBar(isShowToolBar());
        with.setShowToolBarCaptions(isShowToolBarCaptions());
    }
}
