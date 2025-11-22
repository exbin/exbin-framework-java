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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.docking.api.MultiDocking;
import org.exbin.framework.docking.gui.MultiDocumentPanel;
import org.exbin.framework.document.api.ComponentDocument;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.w3c.dom.DocumentType;

/**
 * Default implementation of the document docking supporting multiple documents.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMultiDocking implements MultiDocking {

    protected final List<Document> openDocuments = new ArrayList<>();
    protected final MultiDocumentPanel docking = new MultiDocumentPanel();

    @Nonnull
    @Override
    public Component getDockingComponent() {
        return docking;
    }

    @Nonnull
    @Override
    public Component getComponent() {
        return docking;
    }

    @Nonnull
    @Override
    public Optional<Document> getActiveDocument() {
        return Optional.ofNullable(getDocument());
    }

    @Override
    public void openNewDocument() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        Document document = documentModule.createDefaultDocument();
        docking.addDocument((ComponentDocument) document, "TODO");
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
    @Override
    public List<Document> getDocuments() {
        return openDocuments;
    }

    /**
     * Close all documents.
     */
    @Override
    public void closeAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Close other documents.
     *
     * @param document exception document
     */
    @Override
    public void closeOtherDocuments(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Save all documents.
     */
    @Override
    public void saveAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns true if any document is opened.
     *
     * @return true if there is at least one opened document
     */
    @Override
    public boolean hasOpenedDocuments() {
        return !openDocuments.isEmpty();
    }

    @Override
    public void notifyActivated(ActiveContextManagement contextManager) {
        contextManager.changeActiveState(ContextDocking.class, this);
        Document document = getDocument();
        contextManager.changeActiveState(ContextDocument.class, (ContextDocument) document);
    }

    @Override
    public void notifyDeactivated(ActiveContextManagement contextManager) {
        contextManager.changeActiveState(ContextDocking.class, null);
        contextManager.changeActiveState(ContextDocument.class, null);
    }
    
    @Nullable
    private Document getDocument() {
        int activeIndex = docking.getActiveIndex();
        if (activeIndex < 0) {
            return null;
        }
        
        return openDocuments.get(activeIndex);
    }
}
