/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.ui.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.language.api.LanguageProvider;
import org.exbin.jaguif.ui.settings.gui.LanguageSettingsPanel;
import org.exbin.jaguif.ui.model.LanguageRecord;
import org.exbin.jaguif.options.settings.api.SettingsComponent;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;

/**
 * Language settings component.
 */
@ParametersAreNonnullByDefault
public class LanguageSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "language";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        LanguageSettingsPanel panel = new LanguageSettingsPanel();
        ResourceBundle resourceBundle = panel.getResourceBundle();
        List<LanguageRecord> languageLocales = new ArrayList<>();
        languageLocales.add(new LanguageRecord(Locale.ROOT, null));
        languageLocales.add(new LanguageRecord(Locale.US, new ImageIcon(getClass().getResource(resourceBundle.getString("locale.englishFlag")))));

        List<LanguageProvider> languagePlugins = App.getModule(LanguageModuleApi.class).getLanguagePlugins();
        for (LanguageProvider languageProvider : languagePlugins) {
            ImageIcon flag = null;
            try {
                flag = languageProvider.getFlag().orElse(null);
            } catch (Throwable ex) {
                Logger.getLogger(LanguageSettingsComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            languageLocales.add(new LanguageRecord(languageProvider.getLocale(), flag, null));
        }

        panel.setLanguageLocales(languageLocales);
        panel.setDefaultLocaleName("<" + resourceBundle.getString("locale.defaultLanguage") + ">");
        return panel;
    }
}
