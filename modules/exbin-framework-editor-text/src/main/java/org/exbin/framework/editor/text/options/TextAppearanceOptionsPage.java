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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.options.gui.TextAppearanceOptionsPanel;
import org.exbin.framework.editor.text.service.TextAppearanceService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Text appearance options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceOptionsPage implements DefaultOptionsPage<TextAppearanceOptions> {

    public static final String PAGE_ID = "textAppearance";

    private TextAppearanceOptionsPanel panel;
    private TextAppearanceService textAppearanceService;

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    public void setTextAppearanceService(TextAppearanceService textAppearanceService) {
        this.textAppearanceService = textAppearanceService;
    }

    @Nonnull
    @Override
    public OptionsComponent<TextAppearanceOptions> createPanel() {
        if (panel == null) {
            panel = new TextAppearanceOptionsPanel();
        }
        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextAppearanceOptionsPanel.class);
    }

    @Nonnull
    @Override
    public TextAppearanceOptions createOptions() {
        return new TextAppearanceOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextAppearanceOptions options) {
        new TextAppearanceOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextAppearanceOptions options) {
        options.copyTo(new TextAppearanceOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextAppearanceOptions options) {
        textAppearanceService.setWordWrapMode(options.isWordWrapping());
    }
}
