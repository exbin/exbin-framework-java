/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.docking.multi;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.gui.DockingPanel;
import org.exbin.jaguif.docking.multi.api.MultiDocking;
import org.exbin.jaguif.docking.multi.gui.MultiDocumentPanel;
import org.exbin.jaguif.document.api.ComponentDocument;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.document.api.Document;
import org.exbin.jaguif.document.api.DocumentManagement;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.context.api.ContextActivable;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.docking.api.SidePanelDocking;
import org.exbin.jaguif.docking.multi.api.DockingMultiModuleApi;
import org.exbin.jaguif.document.api.DocumentSource;
import org.exbin.jaguif.document.api.EditableDocument;
import org.exbin.jaguif.document.api.LoadableDocument;
import org.exbin.jaguif.file.api.FileDocument;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.api.FileSourceIdentifier;
import org.exbin.jaguif.file.api.SaveModifiedResult;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.utils.WindowClosingListener;
import org.exbin.jaguif.document.api.EmptyDocumentSource;

/**
 * Default implementation of the document docking supporting multiple documents.
 */
@ParametersAreNonnullByDefault
public class DefaultMultiDocking implements MultiDocking, SidePanelDocking, WindowClosingListener {

    protected final List<Document> openDocuments = new ArrayList<>();
    protected final DockingPanel docking = new DockingPanel();
    protected final MultiDocumentPanel documentPanel = new MultiDocumentPanel();
    protected Document lastActiveDocument = null;
    protected ActiveContextManagement contextManager = null;

    public DefaultMultiDocking() {
        docking.setContentComponent(documentPanel);
        documentPanel.setController(new MultiDocumentPanel.Controller() {
            @Override
            public void activeIndexChanged(int index) {
                notifyActivated(contextManager);
            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                if (index < 0) {
                    return;
                }

                ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
                ActiveContextManagement popupContextManager = contextModule.createChildContextManager(contextManager);
                Document refDocument = openDocuments.get(index);
                popupContextManager.changeActiveState(ContextDocument.class, (ContextDocument) refDocument);

//                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                JPopupMenu fileContextPopupMenu = menuModule.getMenuBuilder().createPopupMenu();
                ContextRegistration contextRegistrar = contextModule.createContextRegistrator(contextManager);
                menuModule.buildMenu(fileContextPopupMenu, DockingMultiModule.FILE_CONTEXT_MENU_ID, contextRegistrar);
                fileContextPopupMenu.show(component, positionX, positionY);
                // TODO dispose?
            }
        });
        documentPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Object transferData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    List<?> droppedFiles = (List) transferData;
                    DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
                    DocumentManagement documentManager = documentModule.getMainDocumentManager();
                    for (Object droppedFile : droppedFiles) {
                        File file = (File) droppedFile;
                        Document document = documentManager.createDocumentForSource(new FileSourceIdentifier(file.toURI()));
                        openDocument(document);
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(DefaultMultiDocking.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
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
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        Document document = documentManager.createDefaultDocument();
        openDocuments.add(document);
        documentPanel.addDocument((ComponentDocument) document, getDocumentTitle(document));
        if (document instanceof ContextActivable) {
            ((ContextActivable) document).notifyActivated(contextManager);
        }
    }

    @Override
    public void openDocument(Document document) {
        openDocuments.add(document);
        documentPanel.addDocument((ComponentDocument) document, getDocumentTitle(document));
        if (document instanceof ContextActivable) {
            ((ContextActivable) document).notifyActivated(contextManager);
        }
    }

    @Override
    public void closeDocument(Document document) {
        Document activeDocument = getDocument();
        if (activeDocument == document) {
            if (activeDocument instanceof ContextActivable) {
                ((ContextActivable) activeDocument).notifyDeactivated(contextManager);
            }
        }
        int index = openDocuments.indexOf(document);
        if (index >= 0) {
            openDocuments.remove(index);
            documentPanel.removeDocumentAtIndex(index);
        }
        activeDocument = getDocument();
        if (activeDocument != null) {
            contextManager.changeActiveState(ContextDocument.class, (ContextDocument) activeDocument);
            if (activeDocument instanceof ContextActivable) {
                ((ContextActivable) activeDocument).notifyActivated(contextManager);
            }
        }
    }

    @Nonnull
    @Override
    public List<Document> getDocuments() {
        return openDocuments;
    }

    @Override
    public void closeAllDocuments() {
        if (releaseAllDocuments()) {
            List<Document> allDocuments = new ArrayList<>();
            allDocuments.addAll(openDocuments);
            for (Document document : allDocuments) {
                closeDocument(document);
            }
        }
    }

    @Override
    public void closeOtherDocuments(Document exceptionDocument) {
        if (releaseOtherDocuments(exceptionDocument)) {
            List<Document> allDocuments = new ArrayList<>();
            allDocuments.addAll(openDocuments);
            for (int i = allDocuments.size() - 1; i >= 0; i--) {
                if (allDocuments.get(i) != exceptionDocument) {
                    openDocuments.remove(i);
                    documentPanel.removeDocumentAtIndex(i);
                }
            }
        }
    }

    @Override
    public void saveAllDocuments() {
        if (openDocuments.isEmpty()) {
            return;
        }

        for (Document document : openDocuments) {
            if (document instanceof EditableDocument && ((EditableDocument) document).isModified()) {
                Optional<DocumentSource> optDocumentSource = ((EditableDocument) document).getDocumentSource();
                if (optDocumentSource.isPresent()) {
                    DocumentSource documentSource = optDocumentSource.get();
                    if (!(documentSource instanceof EmptyDocumentSource)) {
                        ((EditableDocument) document).saveTo(documentSource);
                    }
                }
            }
        }

        releaseAllDocuments();
    }

    @Override
    public boolean hasOpenedDocuments() {
        return !openDocuments.isEmpty();
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

    public boolean releaseAllDocuments() {
        return releaseOtherDocuments(null);
    }

    private boolean releaseOtherDocuments(@Nullable Document exceptionDocument) {
        if (openDocuments.isEmpty()) {
            return true;
        }

        if (openDocuments.size() == 1) {
            if (openDocuments.get(0) == exceptionDocument) {
                return true;
            }

            return releaseDocument(openDocuments.get(0));
        }

        List<Document> modifiedDocuments = new ArrayList<>();
        for (Document document : openDocuments) {
            if (document instanceof EditableDocument && ((EditableDocument) document).isModified() && document != exceptionDocument) {
                modifiedDocuments.add(document);
            }
        }

        if (modifiedDocuments.isEmpty()) {
            return true;
        }

        DockingMultiModule dockingMultiModule = (DockingMultiModule) App.getModule(DockingMultiModuleApi.class);
        return dockingMultiModule.showAskForSaveDialog(modifiedDocuments, documentPanel);
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
            if (activeDocument instanceof ContextActivable) {
                ((ContextActivable) activeDocument).notifyActivated(contextManager);
            }
        }
    }

    @Override
    public void notifyDeactivated(ActiveContextManagement contextManager) {
        Optional<Document> optActiveDocument = getActiveDocument();
        if (optActiveDocument.isPresent()) {
            Document activeDocument = optActiveDocument.get();
            if (activeDocument instanceof ContextActivable) {
                ((ContextActivable) activeDocument).notifyDeactivated(contextManager);
            }
        }
        contextManager.changeActiveState(ContextDocument.class, null);
        contextManager.changeActiveState(ContextDocking.class, null);
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

    @Nonnull
    private String getDocumentTitle(Document document) {
        if (!(document instanceof FileDocument)) {
            return "";
        }
        FileDocument fileDocument = (FileDocument) document;
        URI fileUri = fileDocument.getFileUri().orElse(null);
        if (fileUri == null) {
            LoadableDocument loadableDocument = (LoadableDocument) document;
            Optional<DocumentSource> optDocumentSource = loadableDocument.getDocumentSource();
            if (optDocumentSource.isPresent()) {
                DocumentSource documentSource = optDocumentSource.get();
                if (documentSource instanceof EmptyDocumentSource) {
                    return ((EmptyDocumentSource) documentSource).getDocumentTitle();
                }
            }
            return "";
        }
        String path = fileUri.getPath();
        int lastSegment = path.lastIndexOf("/");
        String fileName = lastSegment < 0 ? path : path.substring(lastSegment + 1);
        return fileName == null ? "" : fileName;
    }

    @Override
    public boolean windowClosing() {
        return releaseAllDocuments();
    }
}
