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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.context.api.ContextActivable;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.docking.api.SidePanelDocking;
import org.exbin.framework.docking.gui.DockingPanel;
import org.exbin.framework.document.api.ComponentDocument;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentManagement;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.document.api.DocumentSource;
import org.exbin.framework.document.api.EditableDocument;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.SaveModifiedResult;
import org.exbin.framework.utils.WindowClosingListener;

/**
 * Default implementation of the document docking supporting single document
 * only.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSingleDocking implements ContextDocking, SidePanelDocking, DocumentDocking, WindowClosingListener {

    protected final DockingPanel docking = new DockingPanel();
    protected Document currentDocument = null;
    protected ActiveContextManagement contextManager = null;

    @Nonnull
    @Override
    public Component getComponent() {
        return docking;
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
        contextManager.changeActiveState(ContextDocument.class, (ContextDocument) currentDocument);
        if (currentDocument instanceof ContextActivable) {
            ((ContextActivable) currentDocument).notifyActivated(contextManager);
        }
    }

    @Override
    public void notifyDeactivated(ActiveContextManagement contextManager) {
        if (currentDocument instanceof ContextActivable) {
            ((ContextActivable) currentDocument).notifyDeactivated(contextManager);
        }
        contextManager.changeActiveState(ContextDocument.class, null);
        contextManager.changeActiveState(ContextDocking.class, null);
        this.contextManager = null;
    }

    @Nonnull
    @Override
    public Optional<Document> getActiveDocument() {
        return Optional.ofNullable(currentDocument);
    }

    @Nonnull
    @Override
    public Document openNewDocument() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        return documentManager.createDefaultDocument();
    }

    @Override
    public void openDocument(Document document) {
        if (currentDocument != null) {
            if (!releaseDocument(document)) {
                return;
            }
        }

        currentDocument = document;
        docking.setContentComponent(((ComponentDocument) document).getComponent());
        ((ContextActivable) currentDocument).notifyActivated(contextManager);
    }

    @Override
    public void closeDocument(Document document) {
        if (releaseDocument(document)) {
            currentDocument = null;
        }
    }

    @Override
    public boolean releaseDocument(Document document) {
        if (document instanceof EditableDocument && ((EditableDocument) document).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            SaveModifiedResult result = fileModule.showSaveModified(docking);
            switch (result) {
                case SAVE:
                    DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
                    Optional<DocumentSource> documentSource = documentModule.getMainDocumentManager().saveDocumentAs(document);
                    if (documentSource.isPresent()) {
                        ((EditableDocument) document).saveTo(documentSource.get());
                        return true;
                    }
                    return false;
                case DISCARD:
                    return true;
                case CANCEL:
                    return false;
            }

            return false;
        }

        return true;
    }

    public boolean releaseDocument() {
        if (currentDocument == null) {
            return true;
        }

        return releaseDocument(currentDocument);
    }

    @Override
    public boolean windowClosing() {
        return releaseDocument();
    }
}
