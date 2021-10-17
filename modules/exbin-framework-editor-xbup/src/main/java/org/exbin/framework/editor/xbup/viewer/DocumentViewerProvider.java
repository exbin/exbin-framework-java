/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.xbup.viewer;

import java.awt.datatransfer.Clipboard;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.CopyItemAction;
import org.exbin.framework.editor.xbup.action.CutItemAction;
import org.exbin.framework.editor.xbup.action.DeleteItemAction;
import org.exbin.framework.editor.xbup.action.PasteItemAction;
import org.exbin.framework.editor.xbup.gui.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.gui.XBDocTreeTransferHandler;
import org.exbin.framework.editor.xbup.gui.XBDocumentPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentTab.ActivationListener;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.ClipboardUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.operation.Operation;
import org.exbin.xbup.operation.OperationEvent;
import org.exbin.xbup.operation.OperationListener;
import org.exbin.xbup.operation.XBTDocOperation;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Viewer provider.
 *
 * @version 0.2.1 2020/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentViewerProvider implements EditorProvider, ClipboardActionsHandler {

    private XBACatalog catalog;
    private PropertyChangeListener propertyChangeListener = null;

    private final XBDocumentPanel documentPanel;

    private XBTBlock selectedItem = null;
    private ViewerTab selectedTab;
    private ClipboardActionsHandler activeHandler;

    private final TreeDocument treeDocument;
    private final SortedMap<ViewerTab, DocumentTab> tabs = new TreeMap<>();

    private XBApplication application;
    private XBUndoHandler undoHandler;
    private XBPluginRepository pluginRepository;
    private final List<DocumentItemSelectionListener> itemSelectionListeners = new ArrayList<>();
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private FileHandler activeFile;

    public DocumentViewerProvider(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;

        treeDocument = new TreeDocument(null);
        tabs.put(ViewerTab.VIEW, new ViewerDocumentTab());
        tabs.put(ViewerTab.PROPERTIES, new PropertiesDocumentTab());
        tabs.put(ViewerTab.TEXT, new TextDocumentTab());
        tabs.put(ViewerTab.BINARY, new BinaryDocumentTab());

        documentPanel = new XBDocumentPanel();
        documentPanel.addTabSwitchListener(this::setSelectedTab);
        documentPanel.addItemSelectionListener((item) -> {
            this.selectedItem = item;
            notifySelectedItem();
            notifyItemSelectionChanged();
        });

        treeDocument.setActivationListener(() -> {
            activeHandler = treeDocument;
            notifyActiveChanged();
        });

        documentPanel.setMainDoc(treeDocument);

        tabs.values().forEach(tab -> {
            tab.setActivationListener(() -> {
                activeHandler = tab;
                notifyActiveChanged();
            });
        });

        for (Map.Entry<ViewerTab, DocumentTab> entry : tabs.entrySet()) {
            documentPanel.addTabComponent(entry.getKey(), entry.getValue());
        }

        selectedTab = ViewerTab.VIEW;
        activeHandler = treeDocument;

        activeFile = new FileHandler() {
            private URI fileUri = null;
            private FileType fileType = null;

            @Override
            public int getId() {
                return -1;
            }

            @Nonnull
            @Override
            public JComponent getComponent() {
                return documentPanel;
            }

            @Override
            public void loadFromFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    getDoc().fromStreamUB(fileStream);
                    getDoc().processSpec();
                    reportStructureChange((XBTTreeNode) getDoc().getRootBlock().get());
                    performSelectAll();
                    undoHandler.clear();
                    this.fileUri = fileUri;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
                } catch (XBProcessingException ex) {
                    Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
                    throw new UnsupportedOperationException("Not supported yet.");
                    // TODO JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void saveToFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    getDoc().toStreamUB(fileOutputStream);
                    undoHandler.setSyncPoint();
                    getDoc().setModified(false);
                    this.fileUri = fileUri;
                } catch (IOException ex) {
                    Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Nonnull
            @Override
            public Optional<URI> getFileUri() {
                return Optional.ofNullable(fileUri);
            }

            @Override
            public void newFile() {
                undoHandler.clear();
                getDoc().clear();
                reportStructureChange(null);
//        updateItem();
            }

            @Nonnull
            @Override
            public Optional<String> getFileName() {
                if (fileUri != null) {
                    String path = fileUri.getPath();
                    int lastSegment = path.lastIndexOf("/");
                    return Optional.of(lastSegment < 0 ? path : path.substring(lastSegment + 1));
                }

                return Optional.empty();
            }

            @Nonnull
            @Override
            public Optional<FileType> getFileType() {
                return Optional.ofNullable(fileType);
            }

            @Override
            public void setFileType(FileType fileType) {
                this.fileType = fileType;
            }

            @Override
            public boolean isModified() {
                return getDoc().wasModified();
            }
        };
    }

    @Nonnull
    @Override
    public JPanel getEditorComponent() {
        return documentPanel;
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.of(activeFile);
    }

    public XBTTreeDocument getDoc() {
        return treeDocument;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        documentPanel.setCatalog(catalog);
        treeDocument.setCatalog(catalog);
        treeDocument.processSpec();

        tabs.values().forEach(tab -> {
            tab.setCatalog(catalog);
        });
    }

    public XBApplication getApplication() {
        return application;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        documentPanel.setApplication(application);
        tabs.values().forEach(tab -> {
            tab.setApplication(application);
        });
    }

    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        documentPanel.setPluginRepository(pluginRepository);
        tabs.values().forEach(tab -> {
            tab.setPluginRepository(pluginRepository);
        });
    }

    public void setDevMode(boolean devMode) {
        PropertiesDocumentTab tab = (PropertiesDocumentTab) tabs.get(ViewerTab.PROPERTIES);
        tab.setDevMode(devMode);
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
        documentPanel.setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        if (!"".equals(getDoc().getFileName())) {
            int pos;
            int newpos = 0;
            do {
                pos = newpos;
                newpos = getDoc().getFileName().indexOf(File.separatorChar, pos) + 1;
            } while (newpos > 0);
            return getDoc().getFileName().substring(pos) + " - " + frameTitle;
        }

        return frameTitle;
    }

    @Override
    public void performCut() {
        activeHandler.performCut();
    }

    @Override
    public void performCopy() {
        activeHandler.performCopy();
    }

    @Override
    public void performPaste() {
        activeHandler.performPaste();
    }

    @Override
    public void performDelete() {
        activeHandler.performDelete();
    }

    @Override
    public void performSelectAll() {
        activeHandler.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return activeHandler.isSelection();
    }

    @Override
    public boolean isEditable() {
        return activeHandler.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return activeHandler.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return activeHandler.canPaste();
    }

    @Override
    public boolean canDelete() {
        return activeHandler.canDelete();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        treeDocument.setUpdateListener(updateListener);
        tabs.values().forEach(tab -> {
            tab.setUpdateListener(updateListener);
        });
    }

    public void postWindowOpened() {
        documentPanel.postWindowOpened();
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        // TODO
    }

    public void reportStructureChange(XBTBlock block) {
        documentPanel.reportStructureChange(block);
    }

    @Nonnull
    public Optional<XBTBlock> getSelectedItem() {
        return Optional.ofNullable(selectedItem);
    }

    public void setSelectedTab(ViewerTab selectedTab) {
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab;
            notifySelectedItem();
            notifyItemSelectionChanged();
            DocumentTab currentTab = getCurrentTab();
            if (activeHandler != currentTab && activeHandler != treeDocument) {
                activeHandler = treeDocument;
                notifyActiveChanged();
            }

//            mainFrame.getEditFindAction().setEnabled(mode != PanelMode.TREE);
//            mainFrame.getEditFindAgainAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getEditGotoAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getEditReplaceAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getItemAddAction().setEnabled(false);
//            mainFrame.getItemModifyAction().setEnabled(false);
//            showPanel();
//            updateItem();
//            updateActionStatus(null);
//            if (clipboardActionsUpdateListener != null) {
//                clipboardActionsUpdateListener.stateChanged();
//            }
        }
    }

    public void switchToTab(ViewerTab selectedTab) {
        documentPanel.switchToTab(selectedTab);
    }

    private void notifySelectedItem() {
        DocumentTab currentTab = getCurrentTab();
        try {
            currentTab.setSelectedItem(selectedItem);
        } catch (Exception ex) {
            Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DocumentTab getCurrentTab() {
        return tabs.get(selectedTab);
    }

    private void notifyActiveChanged() {
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }
    }

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        panel.setBlock(getSelectedItem().orElse(null));
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(null);
    }

    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void itemWasModified(XBTTreeNode newNode) {
        reportStructureChange(newNode);
        treeDocument.setModified(true);
        treeDocument.processSpec();
        // TODO updateItemStatus();
    }

    public void addItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.add(listener);
    }

    public void removeItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.remove(listener);
    }

    public void notifyItemSelectionChanged() {
        itemSelectionListeners.forEach(listener -> {
            listener.itemSelected(selectedItem);
        });
    }

    @Override
    public void newFile() {
        activeFile.newFile();
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void openFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAsFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseAllFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @ParametersAreNonnullByDefault
    private class TreeDocument extends XBTTreeDocument implements OperationListener, ClipboardActionsHandler {

        public TreeDocument() {
            super((XBCatalog) null);
        }

        public TreeDocument(@Nullable XBCatalog catalog) {
            super(catalog);
        }

        @Override
        public void notifyChange(OperationEvent event) {
            Operation operation = event.getOperation();
            // TODO Consolidate
            processSpec();
            reportStructureChange(null);
            // getDoc().setModified(true);
//            updateItem();
//            updateActionStatus(null);
//            if (clipboardActionsUpdateListener != null) {
//                clipboardActionsUpdateListener.stateChanged();
//            }

            if (operation instanceof XBTDocOperation) {
                setSelectedTab(ViewerTab.VIEW);
            } else {
                // TODO
            }
        }

        public void setActivationListener(final ActivationListener listener) {
            documentPanel.addTreeFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    listener.activated();
                }
            });
        }

        @Override
        public void performCut() {
            CutItemAction action = new CutItemAction();
            action.setup(DocumentViewerProvider.this);
            action.actionPerformed(null);
        }

        @Override
        public void performCopy() {
            CopyItemAction action = new CopyItemAction();
            action.setup(DocumentViewerProvider.this);
            action.actionPerformed(null);
        }

        @Override
        public void performPaste() {
            PasteItemAction action = new PasteItemAction();
            action.setup(DocumentViewerProvider.this);
            action.actionPerformed(null);
        }

        @Override
        public void performDelete() {
            DeleteItemAction action = new DeleteItemAction();
            action.setup(DocumentViewerProvider.this);
            action.actionPerformed(null);
        }

        @Override
        public void performSelectAll() {
            documentPanel.performSelectAll();
        }

        @Override
        public boolean isSelection() {
            return documentPanel.hasSelection();
        }

        @Override
        public boolean isEditable() {
            return documentPanel.hasSelection();
        }

        @Override
        public boolean canSelectAll() {
            return true;
        }

        @Override
        public boolean canPaste() {
            Clipboard clipboard = ClipboardUtils.getClipboard();
            return clipboard.isDataFlavorAvailable(XBDocTreeTransferHandler.XB_DATA_FLAVOR);
        }

        @Override
        public boolean canDelete() {
            return documentPanel.hasSelection();
        }

        @Override
        public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
            documentPanel.addUpdateListener((e) -> {
                updateListener.stateChanged();
            });
        }
    }

    @ParametersAreNonnullByDefault
    public enum ViewerTab {
        VIEW("Viewer"),
        PROPERTIES("Properties"),
        TEXT("Text"),
        BINARY("Binary");

        private final String tabName;

        private ViewerTab(String tabName) {
            this.tabName = tabName;
        }

        @Nonnull
        public String getTabName() {
            return tabName;
        }
    }
}
