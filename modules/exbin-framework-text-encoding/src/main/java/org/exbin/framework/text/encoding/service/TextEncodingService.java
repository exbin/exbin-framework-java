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
package org.exbin.framework.text.encoding.service;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.text.encoding.TextEncodingStatusApi;
import org.exbin.framework.text.encoding.options.TextEncodingOptions;

/**
 * Text encoding panel API.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface TextEncodingService {

    /**
     * Returns current encodings used in application frame.
     *
     * @return font
     */
    @Nonnull
    List<String> getEncodings();

    /**
     * Gets selected encoding.
     *
     * @return selected encoding
     */
    @Nonnull
    String getSelectedEncoding();

    /**
     * Sets current encodings used in application frame.
     *
     * @param encodings list of encodings
     */
    void setEncodings(List<String> encodings);

    /**
     * Sets selected encoding.
     *
     * @param encoding encoding
     */
    void setSelectedEncoding(String encoding);

    void setTextEncodingStatus(TextEncodingStatusApi textEncodingStatus);

    void loadFromOptions(TextEncodingOptions preferences);

    void setEncodingChangeListener(EncodingChangeListener listener);

    public interface EncodingChangeListener {

        void encodingListChanged();

        void selectedEncodingChanged();
    }
}
