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
package org.exbin.framework.text.font.settings;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Text font options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontOptions implements SettingsOptions {

    public static final String KEY_TEXT_FONT_PREFIX = "textFont.";
    public static final String KEY_TEXT_FONT_DEFAULT = KEY_TEXT_FONT_PREFIX + "default";
    public static final String KEY_TEXT_FONT_FAMILY = KEY_TEXT_FONT_PREFIX + "family";
    public static final String KEY_TEXT_FONT_SIZE = KEY_TEXT_FONT_PREFIX + "size";
    public static final String KEY_TEXT_FONT_UNDERLINE = KEY_TEXT_FONT_PREFIX + "underline";
    public static final String KEY_TEXT_FONT_STRIKETHROUGH = KEY_TEXT_FONT_PREFIX + "strikethrough";
    public static final String KEY_TEXT_FONT_STRONG = KEY_TEXT_FONT_PREFIX + "strong";
    public static final String KEY_TEXT_FONT_ITALIC = KEY_TEXT_FONT_PREFIX + "italic";
    public static final String KEY_TEXT_FONT_SUBSCRIPT = KEY_TEXT_FONT_PREFIX + "subscript";
    public static final String KEY_TEXT_FONT_SUPERSCRIPT = KEY_TEXT_FONT_PREFIX + "superscript";

    private final OptionsStorage storage;

    public TextFontOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isUseDefaultFont() {
        return storage.getBoolean(KEY_TEXT_FONT_DEFAULT, true);
    }

    public void setUseDefaultFont(boolean defaultFont) {
        storage.putBoolean(KEY_TEXT_FONT_DEFAULT, defaultFont);
    }

    @Nonnull
    public Font getFont(Font initialFont) {
        Map<TextAttribute, Object> attribs = getFontAttributes();
        Font font = initialFont.deriveFont(attribs);
        return font;
    }

    @Nonnull
    public Map<TextAttribute, Object> getFontAttributes() {
        Map<TextAttribute, Object> attribs = new HashMap<>();
        Optional<String> fontFamily = storage.get(KEY_TEXT_FONT_FAMILY);
        if (fontFamily.isPresent()) {
            attribs.put(TextAttribute.FAMILY, fontFamily.get());
        }
        Optional<String> fontSize = storage.get(KEY_TEXT_FONT_SIZE);
        if (fontSize.isPresent()) {
            attribs.put(TextAttribute.SIZE, Integer.valueOf(fontSize.get()).floatValue());
        }
        if (storage.getBoolean(KEY_TEXT_FONT_UNDERLINE, false)) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (storage.getBoolean(KEY_TEXT_FONT_STRIKETHROUGH, false)) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (storage.getBoolean(KEY_TEXT_FONT_STRONG, false)) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (storage.getBoolean(KEY_TEXT_FONT_ITALIC, false)) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (storage.getBoolean(KEY_TEXT_FONT_SUBSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (storage.getBoolean(KEY_TEXT_FONT_SUPERSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        return attribs;
    }

    public void setFont(@Nullable Font font) {
        if (font != null) {
            Map<TextAttribute, ?> attribs = font.getAttributes();
            setFontAttributes(attribs);
        } else {
            storage.remove(KEY_TEXT_FONT_FAMILY);
            storage.remove(KEY_TEXT_FONT_SIZE);
            storage.remove(KEY_TEXT_FONT_UNDERLINE);
            storage.remove(KEY_TEXT_FONT_STRIKETHROUGH);
            storage.remove(KEY_TEXT_FONT_STRONG);
            storage.remove(KEY_TEXT_FONT_ITALIC);
            storage.remove(KEY_TEXT_FONT_SUBSCRIPT);
            storage.remove(KEY_TEXT_FONT_SUPERSCRIPT);
        }
    }

    public void setFontAttributes(Map<TextAttribute, ?> attribs) {
        String value = (String) attribs.get(TextAttribute.FAMILY);
        if (value != null) {
            storage.put(KEY_TEXT_FONT_FAMILY, value);
        } else {
            storage.remove(KEY_TEXT_FONT_FAMILY);
        }
        Float fontSize = (Float) attribs.get(TextAttribute.SIZE);
        if (fontSize != null) {
            storage.put(KEY_TEXT_FONT_SIZE, Integer.toString((int) (float) fontSize));
        } else {
            storage.remove(KEY_TEXT_FONT_SIZE);
        }
        storage.putBoolean(KEY_TEXT_FONT_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE)));
        storage.putBoolean(KEY_TEXT_FONT_STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH)));
        storage.putBoolean(KEY_TEXT_FONT_STRONG, TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT)));
        storage.putBoolean(KEY_TEXT_FONT_ITALIC, TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE)));
        storage.putBoolean(KEY_TEXT_FONT_SUBSCRIPT, TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
        storage.putBoolean(KEY_TEXT_FONT_SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
    }

    @Override
    public void copyTo(SettingsOptions options) {
        TextFontOptions with = (TextFontOptions) options;
        with.setFontAttributes(getFontAttributes());
        with.setUseDefaultFont(isUseDefaultFont());
    }
}
