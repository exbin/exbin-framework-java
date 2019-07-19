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
package org.exbin.framework.editor.text.options;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.preferences.TextColorParameters;

/**
 * Text color options.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorOptions {

    private Integer textColor;
    private Integer textBackgroundColor;
    private Integer selectionTextColor;
    private Integer selectionBackgroundColor;
    private Integer foundBackgroundColor;

    @Nullable
    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(@Nullable Integer textColor) {
        this.textColor = textColor;
    }

    @Nullable
    public Integer getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public void setTextBackgroundColor(@Nullable Integer textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
    }

    @Nullable
    public Integer getSelectionTextColor() {
        return selectionTextColor;
    }

    public void setSelectionTextColor(@Nullable Integer selectionTextColor) {
        this.selectionTextColor = selectionTextColor;
    }

    @Nullable
    public Integer getSelectionBackgroundColor() {
        return selectionBackgroundColor;
    }

    public void setSelectionBackgroundColor(@Nullable Integer selectionBackgroundColor) {
        this.selectionBackgroundColor = selectionBackgroundColor;
    }

    @Nullable
    public Integer getFoundBackgroundColor() {
        return foundBackgroundColor;
    }

    public void setFoundBackgroundColor(@Nullable Integer foundBackgroundColor) {
        this.foundBackgroundColor = foundBackgroundColor;
    }

    public void loadFromParameters(TextColorParameters parameters) {
        textColor = parameters.getTextColor();
        textBackgroundColor = parameters.getTextBackgroundColor();
        selectionTextColor = parameters.getSelectionTextColor();
        selectionBackgroundColor = parameters.getSelectionBackgroundColor();
        foundBackgroundColor = parameters.getFoundBackgroundColor();
    }

    public void saveToParameters(TextColorParameters parameters) {
        parameters.setTextColor(textColor);
        parameters.setTextBackgroundColor(textBackgroundColor);
        parameters.setSelectionTextColor(selectionTextColor);
        parameters.setSelectionBackgroundColor(selectionBackgroundColor);
        parameters.setFoundBackgroundColor(foundBackgroundColor);
    }

    public void setOptions(TextColorOptions options) {
        textColor = options.textColor;
        textBackgroundColor = options.textBackgroundColor;
        selectionTextColor = options.selectionTextColor;
        selectionBackgroundColor = options.selectionBackgroundColor;
        foundBackgroundColor = options.foundBackgroundColor;
    }
}
