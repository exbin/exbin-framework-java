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

import java.awt.Font;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.options.gui.TextFontOptionsPanel;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;

/**
 * Text font options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontOptionsPage implements DefaultOptionsPage<TextFontOptions> {

    public static final String PAGE_ID = "textFont";

    private TextFontOptionsPanel panel;
    private TextFontService textFontService;

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    public void setTextFontService(TextFontService textFontService) {
        this.textFontService = textFontService;
    }

    @Nonnull
    @Override
    public OptionsComponent<TextFontOptions> createPanel() {
        if (panel == null) {
            panel = new TextFontOptionsPanel();
            panel.setTextFontService(textFontService);
            panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
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
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                                TextFontOptions textFontParameters = new TextFontOptions(preferencesModule.getAppPreferences());
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

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextFontOptionsPanel.class);
    }

    @Nonnull
    @Override
    public TextFontOptions createOptions() {
        return new TextFontOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextFontOptions options) {
        new TextFontOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextFontOptions options) {
        options.copyTo(new TextFontOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextFontOptions options) {
        textFontService.setCurrentFont(options.isUseDefaultFont() ? textFontService.getDefaultFont() : options.getFont(textFontService.getDefaultFont()));
    }
}
