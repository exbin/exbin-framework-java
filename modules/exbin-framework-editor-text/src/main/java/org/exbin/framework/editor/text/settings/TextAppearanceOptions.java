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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Text appearance options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceOptions implements SettingsOptions {

    public static final String KEY_TEXT_WORD_WRAPPING = "textAppearance.wordWrap";

    private final OptionsStorage storage;

    public TextAppearanceOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isWordWrapping() {
        return storage.getBoolean(KEY_TEXT_WORD_WRAPPING, false);
    }

    public void setWordWrapping(boolean wordWrap) {
        storage.putBoolean(KEY_TEXT_WORD_WRAPPING, wordWrap);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        ((TextAppearanceOptions) options).setWordWrapping(isWordWrapping());
    }
}
