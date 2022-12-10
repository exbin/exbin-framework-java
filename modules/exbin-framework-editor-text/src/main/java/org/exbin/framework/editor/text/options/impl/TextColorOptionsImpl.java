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
package org.exbin.framework.editor.text.options.impl;

import org.exbin.framework.editor.text.options.TextColorOptions;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.preferences.TextColorPreferences;
import org.exbin.framework.options.api.OptionsData;

/**
 * Text color options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorOptionsImpl implements OptionsData, TextColorOptions {

    private boolean useDefaultColors;
    private Integer textColor;
    private Integer textBackgroundColor;
    private Integer selectionTextColor;
    private Integer selectionBackgroundColor;
    private Integer foundBackgroundColor;

    @Override
    public boolean isUseDefaultColors() {
        return useDefaultColors;
    }

    @Override
    public void setUseDefaultColors(boolean useDefaultColors) {
        this.useDefaultColors = useDefaultColors;
    }

    @Nullable
    @Override
    public Integer getTextColor() {
        return textColor;
    }

    @Override
    public void setTextColor(@Nullable Integer textColor) {
        this.textColor = textColor;
    }

    @Nullable
    @Override
    public Integer getTextBackgroundColor() {
        return textBackgroundColor;
    }

    @Override
    public void setTextBackgroundColor(@Nullable Integer textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
    }

    @Nullable
    @Override
    public Integer getSelectionTextColor() {
        return selectionTextColor;
    }

    @Override
    public void setSelectionTextColor(@Nullable Integer selectionTextColor) {
        this.selectionTextColor = selectionTextColor;
    }

    @Nullable
    @Override
    public Integer getSelectionBackgroundColor() {
        return selectionBackgroundColor;
    }

    @Override
    public void setSelectionBackgroundColor(@Nullable Integer selectionBackgroundColor) {
        this.selectionBackgroundColor = selectionBackgroundColor;
    }

    @Nullable
    @Override
    public Integer getFoundBackgroundColor() {
        return foundBackgroundColor;
    }

    @Override
    public void setFoundBackgroundColor(@Nullable Integer foundBackgroundColor) {
        this.foundBackgroundColor = foundBackgroundColor;
    }

    public void loadFromPreferences(TextColorPreferences preferences) {
        useDefaultColors = preferences.isUseDefaultColors();
        textColor = preferences.getTextColor();
        textBackgroundColor = preferences.getTextBackgroundColor();
        selectionTextColor = preferences.getSelectionTextColor();
        selectionBackgroundColor = preferences.getSelectionBackgroundColor();
        foundBackgroundColor = preferences.getFoundBackgroundColor();
    }

    public void saveToPreferences(TextColorPreferences preferences) {
        preferences.setUseDefaultColors(useDefaultColors);
        preferences.setTextColor(textColor);
        preferences.setTextBackgroundColor(textBackgroundColor);
        preferences.setSelectionTextColor(selectionTextColor);
        preferences.setSelectionBackgroundColor(selectionBackgroundColor);
        preferences.setFoundBackgroundColor(foundBackgroundColor);
    }

    public void setOptions(TextColorOptionsImpl options) {
        useDefaultColors = options.useDefaultColors;
        textColor = options.textColor;
        textBackgroundColor = options.textBackgroundColor;
        selectionTextColor = options.selectionTextColor;
        selectionBackgroundColor = options.selectionBackgroundColor;
        foundBackgroundColor = options.foundBackgroundColor;
    }
}
