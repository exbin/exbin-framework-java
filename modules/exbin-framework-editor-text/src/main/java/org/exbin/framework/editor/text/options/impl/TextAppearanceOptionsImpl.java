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
package org.exbin.framework.editor.text.options.impl;

import org.exbin.framework.editor.text.options.TextAppearanceOptions;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.preferences.TextAppearancePreferences;
import org.exbin.framework.gui.options.api.OptionsData;

/**
 * Text appearance options.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceOptionsImpl implements OptionsData, TextAppearanceOptions {

    private boolean wordWrapping;

    @Override
    public boolean isWordWrapping() {
        return wordWrapping;
    }

    @Override
    public void setWordWrapping(boolean wordWrapping) {
        this.wordWrapping = wordWrapping;
    }

    public void loadFromPreferences(TextAppearancePreferences preferences) {
        wordWrapping = preferences.isWordWrapping();
    }

    public void saveToPreferences(TextAppearancePreferences preferences) {
        preferences.setWordWrapping(wordWrapping);
    }

    public void setOptions(TextAppearanceOptionsImpl options) {
        wordWrapping = options.wordWrapping;
    }
}
