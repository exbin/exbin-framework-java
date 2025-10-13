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
package org.exbin.framework.text.font.settings;

import java.awt.Font;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.text.font.gui.TextFontPanel;
import org.exbin.framework.text.font.settings.gui.TextFontSettingsPanel;
import org.exbin.framework.text.font.service.TextFontService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsData;

/**
 * Text font options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontSettingsComponent implements SettingsComponentProvider<TextFontSettings> {

    private TextFontSettingsPanel panel;
    private TextFontService textFontService;

    public void setTextFontService(TextFontService textFontService) {
        this.textFontService = textFontService;
    }

    @Nonnull
    @Override
    public SettingsComponent<TextFontSettings> createComponent() {
        if (panel == null) {
            panel = new TextFontSettingsPanel();
            panel.setTextFontService(textFontService);
            panel.setFontChangeAction(new TextFontSettingsPanel.FontChangeAction() {
                @Override
                public Font changeFont(Font currentFont) {
                    final Result result = new Result();
                    WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                    final TextFontPanel fontPanel = new TextFontPanel();
                    fontPanel.setStoredFont(currentFont);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    final WindowHandler dialog = windowModule.createDialog(fontPanel, controlPanel);
                    windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
                    windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
                    controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                        if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                            if (actionType == DefaultControlController.ControlActionType.OK) {
                                OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
                                TextFontSettings textFontParameters = new TextFontSettings(preferencesModule.getAppOptions());
                                textFontParameters.setUseDefaultFont(true);
                                textFontParameters.setFont(fontPanel.getStoredFont());
                            }
                            result.font = fontPanel.getStoredFont();
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(panel);

                    return result.font;
                }

                class Result {

                    Font font;
                }
            });
        }
        return panel;
    }

    /* @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextFontSettingsPanel.class);
    }

    @Nonnull
    @Override
    public TextFontSettings createOptions() {
        return new TextFontSettings(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextFontSettings options) {
        new TextFontSettings(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextFontSettings options) {
        options.copyTo(new TextFontSettings(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextFontSettings options) {
        textFontService.setCurrentFont(options.isUseDefaultFont() ? textFontService.getDefaultFont() : options.getFont(textFontService.getDefaultFont()));
    } */
}
