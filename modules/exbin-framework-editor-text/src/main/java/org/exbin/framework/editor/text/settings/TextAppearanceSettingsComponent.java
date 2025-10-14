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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.settings.gui.TextAppearanceSettingsPanel;
import org.exbin.framework.editor.text.service.TextAppearanceService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Text appearance settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceSettingsComponent implements SettingsComponentProvider<TextAppearanceOptions> {

    private TextAppearanceService textAppearanceService;

    public void setTextAppearanceService(TextAppearanceService textAppearanceService) {
        this.textAppearanceService = textAppearanceService;
    }

    @Nonnull
    @Override
    public SettingsComponent<TextAppearanceOptions> createComponent() {
        return new TextAppearanceSettingsPanel();
    }

    /* @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextAppearanceSettingsPanel.class);
    }

    @Nonnull
    @Override
    public TextAppearanceSettings createOptions() {
        return new TextAppearanceSettings(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextAppearanceSettings options) {
        new TextAppearanceSettings(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextAppearanceSettings options) {
        options.copyTo(new TextAppearanceSettings(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextAppearanceSettings options) {
        textAppearanceService.setWordWrapMode(options.isWordWrapping());
    } */
}
