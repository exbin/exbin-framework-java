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
package org.exbin.framework.gui.options.preferences;

import org.exbin.framework.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Apperance options preferences.
 *
 * @version 0.2.0 2019/06/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AppearanceParameters {

    public static final String PREFERENCES_TOOLBAR_VISIBLE = "toolBar.visible";
    public static final String PREFERENCES_TOOLBAR_CAPTIONS = "toolBar.captions";
    public static final String PREFERENCES_STATUSBAR_VISIBLE = "statusBar.visible";

    private final Preferences preferences;

    public AppearanceParameters(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isShowToolBar() {
        return preferences.getBoolean(PREFERENCES_TOOLBAR_VISIBLE, true);
    }

    public boolean isShowToolBarCaptions() {
        return preferences.getBoolean(PREFERENCES_TOOLBAR_CAPTIONS, true);
    }

    public boolean isShowStatusBar() {
        return preferences.getBoolean(PREFERENCES_STATUSBAR_VISIBLE, true);
    }

    public void setShowToolBar(boolean show) {
        preferences.putBoolean(PREFERENCES_TOOLBAR_VISIBLE, show);
    }

    public void setShowToolBarCaptions(boolean show) {
        preferences.putBoolean(PREFERENCES_TOOLBAR_CAPTIONS, show);
    }

    public void setShowStatusBar(boolean show) {
        preferences.putBoolean(PREFERENCES_STATUSBAR_VISIBLE, show);
    }
}
