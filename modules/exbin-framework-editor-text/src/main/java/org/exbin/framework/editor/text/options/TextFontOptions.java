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
package org.exbin.framework.editor.text.options;

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.preferences.TextFontParameters;

/**
 * Text font options.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontOptions {

    private boolean useDefaultFont = true;
    @Nullable
    private Map<TextAttribute, Object> fontAttributes = null;

    public boolean isUseDefaultFont() {
        return useDefaultFont;
    }

    public void setUseDefaultFont(boolean useDefaultFont) {
        this.useDefaultFont = useDefaultFont;
    }

    @Nullable
    public Map<TextAttribute, Object> getFontAttributes() {
        return fontAttributes;
    }

    public void setFontAttributes(@Nullable Map<TextAttribute, Object> fontAttributes) {
        this.fontAttributes = fontAttributes;
    }

    public void loadFromParameters(TextFontParameters parameters) {
        useDefaultFont = parameters.isUseDefaultFont();
        fontAttributes = parameters.getFontAttribs();
    }

    public void saveToParameters(TextFontParameters parameters) {
        parameters.setUseDefaultFont(useDefaultFont);
        parameters.setFontAttribs(fontAttributes);
    }

    public void setOptions(TextFontOptions options) {
        useDefaultFont = options.useDefaultFont;
        fontAttributes = new HashMap<>();
        fontAttributes.putAll(options.fontAttributes);
    }
}
