/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.text.preferences;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;

/**
 * Text appearance parameters.
 *
 * @version 0.2.0 2019/06/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceParameters {

    public static final String PREFERENCES_TEXT_WORD_WRAPPING = "textAppearance.wordWrap";

    private final Preferences preferences;

    public TextAppearanceParameters(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isWordWrapping() {
        return preferences.getBoolean(PREFERENCES_TEXT_WORD_WRAPPING, false);
    }

    public void setWordWrapping(boolean wordWrap) {
        preferences.putBoolean(PREFERENCES_TEXT_WORD_WRAPPING, wordWrap);
    }
}
