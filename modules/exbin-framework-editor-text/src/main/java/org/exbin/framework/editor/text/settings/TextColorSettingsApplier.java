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

import java.awt.Color;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsProvider;

/**
 * Text color options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorSettingsApplier implements SettingsApplier {

    @Override
    public void applySettings(Object instance, SettingsProvider settingsProvider) {
        TextColorOptions options = settingsProvider.getSettings(TextColorOptions.class);
        TextColorService textColorService = null;
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
