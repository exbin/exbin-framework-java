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
package org.exbin.framework.docking.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.document.api.Document;

/**
 * Interface for editor view handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MultiDocking extends ContextDocking, DocumentDocking {

    /**
     * Returns list of opened documents.
     *
     * @return list of documents
     */
    @Nonnull
    List<Document> getDocuments();

    /**
     * Close all documents.
     */
    void closeAllDocuments();

    /**
     * Close other documents.
     *
     * @param document exception document
     */
    void closeOtherDocuments(Document document);

    /**
     * Save all documents.
     */
    void saveAllDocuments();

    /**
     * Returns true if any document is opened.
     *
     * @return true if there is at least one opened document
     */
    boolean hasOpenedDocuments();
}
