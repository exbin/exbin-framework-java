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
package org.exbin.framework.options.options.impl;

import org.exbin.framework.options.options.AppearanceOptions;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.options.preferences.AppearancePreferences;

/**
 * Appearance options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceOptionsImpl implements OptionsData, AppearanceOptions {

    private boolean showToolBar;
    private boolean showToolBarCaptions;
    private boolean showStatusBar;

    @Override
    public boolean isShowToolBar() {
        return showToolBar;
    }

    @Override
    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    @Override
    public boolean isShowToolBarCaptions() {
        return showToolBarCaptions;
    }

    @Override
    public void setShowToolBarCaptions(boolean showToolBarCaptions) {
        this.showToolBarCaptions = showToolBarCaptions;
    }

    @Override
    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    @Override
    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    public void loadFromPreferences(AppearancePreferences preferences) {
        showToolBar = preferences.isShowToolBar();
        showToolBarCaptions = preferences.isShowToolBarCaptions();
        showStatusBar = preferences.isShowStatusBar();
    }

    public void saveToParameters(AppearancePreferences preferences) {
        preferences.setShowToolBar(showToolBar);
        preferences.setShowToolBarCaptions(showToolBarCaptions);
        preferences.setShowStatusBar(showStatusBar);
    }
}
