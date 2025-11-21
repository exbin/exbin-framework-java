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
package org.exbin.framework.docking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.document.api.Document;
import org.w3c.dom.DocumentType;

/**
 * Interface for editor view handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiDocking implements ContextDocking, DocumentDocking {

    protected final List<Document> openDocuments = new ArrayList<>();

    @Nonnull
    @Override
    public Optional<Document> getActiveDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openNewDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openNewDocument(DocumentType documentType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openDocument(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeDocument(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns list of opened documents.
     *
     * @return list of documents
     */
    @Nonnull
    public List<Document> getDocuments() {
        return openDocuments;
    }

    /**
     * Close all documents.
     */
    public void closeAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Close other documents.
     *
     * @param document exception document
     */
    public void closeOtherDocuments(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Save all documents.
     */
    public void saveAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns true if any document is opened.
     *
     * @return true if there is at least one opened document
     */
    public boolean hasOpenedDocuments() {
        return !openDocuments.isEmpty();
    }
}
