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
package org.exbin.framework.docking.multi;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.context.api.ContextComponentProvider;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.docking.gui.DockingPanel;
import org.exbin.framework.docking.multi.api.MultiDocking;
import org.exbin.framework.docking.multi.gui.MultiDocumentPanel;
import org.exbin.framework.document.api.ComponentDocument;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentManagement;
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
    protected final DockingPanel docking = new DockingPanel();
    protected final MultiDocumentPanel documentPanel = new MultiDocumentPanel();
    protected Document lastActiveDocument = null;
    protected ActiveContextManagement contextManager = null;

    public DefaultMultiDocking() {
        docking.setContentComponent(documentPanel);
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

    @Nonnull
    @Override
    public Document openNewDocument() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        Document document = documentManager.createDefaultDocument();
        openDocuments.add(document);
        documentPanel.addDocument((ComponentDocument) document, "TODO");
        notifyActivated(contextManager);
        return document;
    }

    @Nonnull
    @Override
    public Document openNewDocument(DocumentType documentType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openDocument(Document document) {
        openDocuments.add(document);
        documentPanel.addDocument((ComponentDocument) document, "OPEN");
        notifyActivated(contextManager);
    }

    @Override
    public void closeDocument(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public List<Document> getDocuments() {
        return openDocuments;
    }

    @Override
    public void closeAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeOtherDocuments(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAllDocuments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasOpenedDocuments() {
        return !openDocuments.isEmpty();
    }

    @Override
    public void setSideToolBar(@Nullable Component sideToolBar) {
        docking.setSideToolBar(sideToolBar);
    }

    @Override
    public void setSideComponent(@Nullable Component sideComponent) {
        docking.setSideComponent(sideComponent);
    }

    @Override
    public boolean isSidePanelVisible() {
        return docking.isSidePanelVisible();
    }

    @Override
    public void setSidePanelVisible(boolean visible) {
        docking.setSidePanelVisible(visible);
    }

    @Override
    public void notifyActivated(ActiveContextManagement contextManager) {
        this.contextManager = contextManager;
        contextManager.changeActiveState(ContextDocking.class, this);
        Document document = getDocument();
        contextManager.changeActiveState(ContextDocument.class, (ContextDocument) document);
        Optional<Document> optActiveDocument = getActiveDocument();
        if (optActiveDocument.isPresent()) {
            Document activeDocument = optActiveDocument.get();
            if (activeDocument instanceof ContextComponentProvider) {
                ((ContextComponentProvider) activeDocument).notifyActivated(contextManager);
            }
        }
    }

    @Override
    public void notifyDeactivated(ActiveContextManagement contextManager) {
        contextManager.changeActiveState(ContextDocking.class, null);
        contextManager.changeActiveState(ContextDocument.class, null);
        Optional<Document> optActiveDocument = getActiveDocument();
        if (optActiveDocument.isPresent()) {
            Document activeDocument = optActiveDocument.get();
            if (activeDocument instanceof ContextComponentProvider) {
                ((ContextComponentProvider) activeDocument).notifyDeactivated(contextManager);
            }
        }
        this.contextManager = null;
    }

    @Nullable
    private Document getDocument() {
        int activeIndex = documentPanel.getActiveIndex();
        if (activeIndex < 0) {
            return null;
        }

        return openDocuments.get(activeIndex);
    }
}
