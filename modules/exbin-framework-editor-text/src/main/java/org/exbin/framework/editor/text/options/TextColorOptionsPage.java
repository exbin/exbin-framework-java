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
package org.exbin.framework.editor.text.options;

import java.awt.Color;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.options.gui.TextColorOptionsPanel;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Text color options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorOptionsPage implements DefaultOptionsPage<TextColorOptions> {

    public static final String PAGE_ID = "textColor";

    private TextColorOptionsPanel panel;
    private TextColorService textColorService;

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    public void setTextColorService(TextColorService textColorService) {
        this.textColorService = textColorService;
    }

    @Nonnull
    @Override
    public OptionsComponent<TextColorOptions> createComponent() {
        if (panel == null) {
            panel = new TextColorOptionsPanel();
            panel.setTextColorService(textColorService);
        }
        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextColorOptionsPanel.class);
    }

    @Nonnull
    @Override
    public TextColorOptions createOptions() {
        return new TextColorOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextColorOptions options) {
        new TextColorOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextColorOptions options) {
        options.copyTo(new TextColorOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextColorOptions options) {
        if (options.isUseDefaultColors()) {
            textColorService.setCurrentTextColors(textColorService.getDefaultTextColors());
        } else {
            Color[] colors = new Color[5];
            colors[0] = intToColor(options.getTextColor());
            colors[1] = intToColor(options.getTextBackgroundColor());
            colors[2] = intToColor(options.getSelectionTextColor());
            colors[3] = intToColor(options.getSelectionBackgroundColor());
            colors[4] = intToColor(options.getFoundBackgroundColor());
            textColorService.setCurrentTextColors(colors);
        }
    }

    @Nullable
    private Color intToColor(@Nullable Integer intValue) {
        return intValue == null ? null : new Color(intValue);
    }
}
