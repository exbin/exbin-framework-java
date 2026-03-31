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
package org.exbin.framework.document.api;

import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;

/**
 * Interface for stream access document source.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface StreamDocumentSource extends DocumentSource {

    /**
     * Returns document title.
     *
     * @return document title
     */
    @Nonnull
    String getDocumentTitle();

    /**
     * Opens input stream for data reading.
     *
     * @return input stream
     */
    @Nonnull
    InputStream openInputStream();

    /**
     * Opens output stream for data writing.
     *
     * @return output stream
     */
    @Nonnull
    OutputStream openOutputStream();
}
