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
package org.exbin.jaguif.ui.theme.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;

/**
 * UI theme settings applier.
 */
@ParametersAreNonnullByDefault
public class ThemeSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "theme";

    @Override
    public void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        ThemeOptions options = settingsProvider.getSettingsOptions(ThemeOptions.class);
        String selectedTheme = options.getLookAndFeel();
        // TODO application.applyLookAndFeel(selectedTheme);

        // TODO This would require rebuild of the window to force icons reload
        // App.getModule(LanguageModuleApi.class).switchToIconSet(options.getIconSet());
    }
}
