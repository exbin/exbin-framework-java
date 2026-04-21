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
package org.exbin.jaguif.document.text.settings;

import java.awt.Color;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.document.text.TextColorState;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.context.api.ContextStateProvider;

/**
 * Text color options.
 */
@ParametersAreNonnullByDefault
public class TextColorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "textColor";

    @Override
    public void applySettings(ContextStateProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        if (!(instance instanceof TextColorState)) {
            return;
        }

        TextColorOptions options = settingsProvider.getSettingsOptions(TextColorOptions.class);
        TextColorState textColorState = (TextColorState) instance;
        if (options.isUseDefaultColors()) {
            textColorState.setCurrentTextColors(textColorState.getDefaultTextColors());
        } else {
            Color[] colors = new Color[5];
            colors[0] = intToColor(options.getTextColor());
            colors[1] = intToColor(options.getTextBackgroundColor());
            colors[2] = intToColor(options.getSelectionTextColor());
            colors[3] = intToColor(options.getSelectionBackgroundColor());
            colors[4] = intToColor(options.getFoundBackgroundColor());
            textColorState.setCurrentTextColors(colors);
        }
    }

    @Nullable
    private Color intToColor(@Nullable Integer intValue) {
        return intValue == null ? null : new Color(intValue);
    }
}
