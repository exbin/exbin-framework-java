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
package org.exbin.framework.editor.text.preferences;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;

/**
 * Text color preferences.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorPreferences {

    public static final String PREFERENCES_TEXT_COLOR_DEFAULT = "textColor.default";
    public static final String PREFERENCES_TEXT_COLOR_TEXT = "textColor.text";
    public static final String PREFERENCES_TEXT_COLOR_BACKGROUND = "textColor.background";
    public static final String PREFERENCES_TEXT_COLOR_SELECTION = "textColor.selection";
    public static final String PREFERENCES_TEXT_COLOR_SELECTION_BACKGROUND = "textColor.selectionBackground";
    public static final String PREFERENCES_TEXT_COLOR_FOUND = "textColor.found";

    private final Preferences preferences;

    public TextColorPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isUseDefaultColors() {
        return preferences.getBoolean(PREFERENCES_TEXT_COLOR_DEFAULT, true);
    }

    public void setUseDefaultColors(boolean useDefaultColor) {
        preferences.putBoolean(PREFERENCES_TEXT_COLOR_DEFAULT, useDefaultColor);
    }

    @Nullable
    public Integer getTextColor() {
        return getColorAsInt(PREFERENCES_TEXT_COLOR_TEXT);
    }

    @Nullable
    public Integer getTextBackgroundColor() {
        return getColorAsInt(PREFERENCES_TEXT_COLOR_BACKGROUND);
    }

    @Nullable
    public Integer getSelectionTextColor() {
        return getColorAsInt(PREFERENCES_TEXT_COLOR_SELECTION);
    }

    @Nullable
    public Integer getSelectionBackgroundColor() {
        return getColorAsInt(PREFERENCES_TEXT_COLOR_SELECTION_BACKGROUND);
    }

    @Nullable
    public Integer getFoundBackgroundColor() {
        return getColorAsInt(PREFERENCES_TEXT_COLOR_FOUND);
    }

    @Nullable
    private Integer getColorAsInt(String key) {
        String value = preferences.get(key);
        return value == null ? null : Integer.valueOf(value);
    }

    public void setTextColor(int color) {
        preferences.putInt(PREFERENCES_TEXT_COLOR_TEXT, color);
    }

    public void setTextBackgroundColor(int color) {
        preferences.putInt(PREFERENCES_TEXT_COLOR_BACKGROUND, color);
    }

    public void setSelectionTextColor(int color) {
        preferences.putInt(PREFERENCES_TEXT_COLOR_SELECTION, color);
    }

    public void setSelectionBackgroundColor(int color) {
        preferences.putInt(PREFERENCES_TEXT_COLOR_SELECTION_BACKGROUND, color);
    }

    public void setFoundBackgroundColor(int color) {
        preferences.putInt(PREFERENCES_TEXT_COLOR_FOUND, color);
    }
}
