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

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.StateChangeType;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentType;
import org.exbin.framework.utils.ComponentProvider;

/**
 * Interface for document docking.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DocumentDocking extends SidePanelDocking, ComponentProvider {

    /**
     * Returns active document.
     *
     * @return active document
     */
    @Nonnull
    Optional<Document> getActiveDocument();

    /**
     * Opens document.
     *
     * @param document document
     */
    void openDocument(Document document);

    /**
     * Closes document.
     *
     * @param document document
     */
    void closeDocument(Document document);

    /**
     * Opens new document of the default type.
     *
     * @return newly opened document
     */
    @Nonnull
    Document openNewDocument();

    /**
     * Opens new document of the given type.
     *
     * @param documentType document type
     * @return newly opened document
     */
    @Nonnull
    Document openNewDocument(DocumentType documentType);

    public enum ChangeType implements StateChangeType {
        DOCUMENT_LIST
    }
}
