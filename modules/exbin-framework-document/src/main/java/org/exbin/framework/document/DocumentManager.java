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
package org.exbin.framework.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentManagement;
import org.exbin.framework.document.api.DocumentProvider;
import org.exbin.framework.document.api.DocumentType;
import org.exbin.framework.document.api.SourceIdentifier;
import org.exbin.framework.document.api.DocumentSource;

/**
 * Document manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentManager implements DocumentManagement {

    protected final List<DocumentProvider> documentProviders = new ArrayList<>();
    protected final List<DocumentType> documentTypes = new ArrayList<>();

    @Override
    public void registerDocumentProvider(DocumentProvider documentProvider) {
        // TODO
        documentProviders.add(documentProvider);
    }

    @Override
    public void registerDocumentType(DocumentType documentType) {
        documentTypes.add(documentType);
    }

    @Nonnull
    @Override
    public Document createDefaultDocument() {
        return documentTypes.get(0).createDefaultDocument();
    }

    @Nonnull
    @Override
    public Document openDocument(SourceIdentifier sourceIdentifier) {
        for (DocumentProvider documentProvider : documentProviders) {
            Optional<DocumentSource> documentData = documentProvider.createDocumentSource(sourceIdentifier);
            if (documentData.isPresent()) {
                Optional<Document> document = documentTypes.get(0).createDocument(documentData.get());
                if (document.isPresent()) {
                    return document.get();
                }
            }
        }

        throw new IllegalStateException("Unsupported document source");
    }

    @Nonnull
    @Override
    public Optional<Document> openDefaultDocument() {
        Optional<DocumentSource> documentSource = documentProviders.get(0).performOpenDefaultDocument();
        if (!documentSource.isPresent()) {
            return Optional.empty();
        }

        Optional<Document> document = documentTypes.get(0).createDocument(documentSource.get());
        return document;
    }

    @Override
    public Optional<DocumentSource> saveDocumentAs(Document document) {
        return documentProviders.get(0).performSaveAsDefaultDocument(document);
    }
}
