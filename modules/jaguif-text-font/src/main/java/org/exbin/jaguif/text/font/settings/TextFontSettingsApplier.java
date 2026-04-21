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
package org.exbin.jaguif.text.font.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.text.font.TextFontState;
import org.exbin.jaguif.context.api.ContextStateProvider;

/**
 * Text font settings applier.
 */
@ParametersAreNonnullByDefault
public class TextFontSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "textFont";

    @Override
    public void applySettings(ContextStateProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        if (!(instance instanceof TextFontState)) {
            return;
        }

        TextFontOptions options = settingsProvider.getSettingsOptions(TextFontOptions.class);
        TextFontState textFontState = (TextFontState) instance;
        textFontState.setCurrentFont(options.isUseDefaultFont() ? textFontState.getDefaultFont() : options.getFont(textFontState.getDefaultFont()));
    }
}
