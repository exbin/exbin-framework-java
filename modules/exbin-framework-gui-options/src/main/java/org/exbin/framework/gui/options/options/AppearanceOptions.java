/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.options.options;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.gui.options.api.OptionsData;
import org.exbin.framework.gui.options.preferences.AppearancePreferences;

/**
 * Appearance options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceOptions implements OptionsData {

    private boolean showToolBar;
    private boolean showToolBarCaptions;
    private boolean showStatusBar;

    public boolean isShowToolBar() {
        return showToolBar;
    }

    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    public boolean isShowToolBarCaptions() {
        return showToolBarCaptions;
    }

    public void setShowToolBarCaptions(boolean showToolBarCaptions) {
        this.showToolBarCaptions = showToolBarCaptions;
    }

    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    public void loadFromParameters(AppearancePreferences preferences) {
        showToolBar = preferences.isShowToolBar();
        showToolBarCaptions = preferences.isShowToolBarCaptions();
        showStatusBar = preferences.isShowStatusBar();
    }

    public void saveToParameters(AppearancePreferences preferences) {
        preferences.setShowToolBar(showToolBar);
        preferences.setShowToolBarCaptions(showToolBarCaptions);
        preferences.setShowStatusBar(showToolBar);
    }
}
