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
package org.exbin.framework.editor.text.settings;

import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsData;

/**
 * Text color options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorSettings implements SettingsData {

    public static final String KEY_TEXT_COLOR_DEFAULT = "textColor.default";
    public static final String KEY_TEXT_COLOR_TEXT = "textColor.text";
    public static final String KEY_TEXT_COLOR_BACKGROUND = "textColor.background";
    public static final String KEY_TEXT_COLOR_SELECTION = "textColor.selection";
    public static final String KEY_TEXT_COLOR_SELECTION_BACKGROUND = "textColor.selectionBackground";
    public static final String KEY_TEXT_COLOR_FOUND = "textColor.found";

    private final OptionsStorage storage;

    public TextColorSettings(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isUseDefaultColors() {
        return storage.getBoolean(KEY_TEXT_COLOR_DEFAULT, true);
    }

    public void setUseDefaultColors(boolean useDefaultColor) {
        storage.putBoolean(KEY_TEXT_COLOR_DEFAULT, useDefaultColor);
    }

    @Nullable
    public Integer getTextColor() {
        return getColorAsInt(KEY_TEXT_COLOR_TEXT);
    }

    @Nullable
    public Integer getTextBackgroundColor() {
        return getColorAsInt(KEY_TEXT_COLOR_BACKGROUND);
    }

    @Nullable
    public Integer getSelectionTextColor() {
        return getColorAsInt(KEY_TEXT_COLOR_SELECTION);
    }

    @Nullable
    public Integer getSelectionBackgroundColor() {
        return getColorAsInt(KEY_TEXT_COLOR_SELECTION_BACKGROUND);
    }

    @Nullable
    public Integer getFoundBackgroundColor() {
        return getColorAsInt(KEY_TEXT_COLOR_FOUND);
    }

    @Nullable
    private Integer getColorAsInt(String key) {
        Optional<String> value = storage.get(key);
        return value.isPresent() ? Integer.valueOf(value.get()) : null;
    }

    public void setTextColor(@Nullable Integer color) {
        setColor(KEY_TEXT_COLOR_TEXT, color);
    }

    public void setTextColor(int color) {
        storage.putInt(KEY_TEXT_COLOR_TEXT, color);
    }

    public void setTextBackgroundColor(@Nullable Integer color) {
        setColor(KEY_TEXT_COLOR_BACKGROUND, color);
    }

    public void setTextBackgroundColor(int color) {
        storage.putInt(KEY_TEXT_COLOR_BACKGROUND, color);
    }

    public void setSelectionTextColor(@Nullable Integer color) {
        setColor(KEY_TEXT_COLOR_SELECTION, color);
    }

    public void setSelectionTextColor(int color) {
        storage.putInt(KEY_TEXT_COLOR_SELECTION, color);
    }

    public void setSelectionBackgroundColor(@Nullable Integer color) {
        setColor(KEY_TEXT_COLOR_SELECTION_BACKGROUND, color);
    }

    public void setSelectionBackgroundColor(int color) {
        storage.putInt(KEY_TEXT_COLOR_SELECTION_BACKGROUND, color);
    }

    public void setFoundBackgroundColor(@Nullable Integer color) {
        setColor(KEY_TEXT_COLOR_FOUND, color);
    }

    public void setFoundBackgroundColor(int color) {
        storage.putInt(KEY_TEXT_COLOR_FOUND, color);
    }

    private void setColor(String preferenceName, @Nullable Integer color) {
        if (color == null) {
            storage.remove(preferenceName);
        } else {
            storage.putInt(preferenceName, color);
        }
    }

    @Override
    public void copyTo(SettingsData options) {
        if (options instanceof TextColorSettings) {
            TextColorSettings with = (TextColorSettings) options;
            with.setFoundBackgroundColor(getFoundBackgroundColor());
            with.setSelectionBackgroundColor(getSelectionBackgroundColor());
            with.setSelectionTextColor(getSelectionTextColor());
            with.setTextBackgroundColor(getTextBackgroundColor());
            with.setTextColor(getTextColor());
            with.setUseDefaultColors(isUseDefaultColors());
        }
    }
}
