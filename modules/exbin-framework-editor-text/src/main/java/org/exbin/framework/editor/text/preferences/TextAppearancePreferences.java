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
package org.exbin.framework.editor.text.preferences;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.editor.text.options.TextAppearanceOptions;

/**
 * Text appearance preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearancePreferences implements TextAppearanceOptions {

    public static final String PREFERENCES_TEXT_WORD_WRAPPING = "textAppearance.wordWrap";

    private final Preferences preferences;

    public TextAppearancePreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean isWordWrapping() {
        return preferences.getBoolean(PREFERENCES_TEXT_WORD_WRAPPING, false);
    }

    @Override
    public void setWordWrapping(boolean wordWrap) {
        preferences.putBoolean(PREFERENCES_TEXT_WORD_WRAPPING, wordWrap);
    }
}
