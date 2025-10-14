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
package org.exbin.framework.text.encoding.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Text editor encodings options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingOptions implements SettingsOptions {

    public static final String KEY_TEXT_ENCODING_PREFIX = "textEncoding.";
    public static final String KEY_TEXT_ENCODING_DEFAULT = KEY_TEXT_ENCODING_PREFIX + "default";
    public static final String KEY_TEXT_ENCODING_SELECTED = "selectedEncoding";

    private final OptionsStorage storage;

    public TextEncodingOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getDefaultEncoding() {
        return storage.get(KEY_TEXT_ENCODING_DEFAULT, EncodingsHandler.ENCODING_UTF8);
    }

    public void setDefaultEncoding(String encodingName) {
        storage.put(KEY_TEXT_ENCODING_DEFAULT, encodingName);
    }

    @Nonnull
    public String getSelectedEncoding() {
        return storage.get(KEY_TEXT_ENCODING_SELECTED, EncodingsHandler.ENCODING_UTF8);
    }

    public void setSelectedEncoding(String encodingName) {
        storage.put(KEY_TEXT_ENCODING_SELECTED, encodingName);
    }

    @Nonnull
    public List<String> getEncodings() {
        List<String> encodings = new ArrayList<>();
        Optional<String> value;
        int i = 0;
        do {
            value = storage.get(KEY_TEXT_ENCODING_PREFIX + Integer.toString(i));
            if (value.isPresent()) {
                encodings.add(value.get());
                i++;
            }
        } while (value.isPresent());

        return encodings;
    }

    public void setEncodings(List<String> encodings) {
        for (int i = 0; i < encodings.size(); i++) {
            storage.put(KEY_TEXT_ENCODING_PREFIX + Integer.toString(i), encodings.get(i));
        }
        storage.remove(KEY_TEXT_ENCODING_PREFIX + Integer.toString(encodings.size()));
    }

    @Override
    public void copyTo(SettingsOptions options) {
        TextEncodingOptions with = (TextEncodingOptions) options;
        with.setDefaultEncoding(getDefaultEncoding());
        with.setEncodings(getEncodings());
        with.setSelectedEncoding(getSelectedEncoding());
    }
}
