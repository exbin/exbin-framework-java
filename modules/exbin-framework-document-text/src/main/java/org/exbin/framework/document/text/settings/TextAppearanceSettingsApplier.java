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
package org.exbin.framework.document.text.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.document.text.TextAppearanceState;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Text appearance settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceSettingsApplier implements SettingsApplier {

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsProvider) {
        if (!(instance instanceof TextAppearanceState)) {
            return;
        }

        TextAppearanceOptions options = settingsProvider.getSettingsOptions(TextAppearanceOptions.class);
        ((TextAppearanceState) instance).setWordWrapMode(options.isWordWrapping());
    }
}
