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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.CopyItemAction;
import org.exbin.framework.editor.xbup.action.CutItemAction;
import org.exbin.framework.editor.xbup.action.DeleteItemAction;
import org.exbin.framework.editor.xbup.action.PasteItemAction;
import org.exbin.framework.editor.xbup.gui.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.gui.XBDocTreeTransferHandler;
import org.exbin.framework.editor.xbup.gui.XBDocumentPanel;
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

/**
 * Viewer provider.
 *
 * @version 0.2.1 2020/09/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentViewerProvider implements EditorProvider, ClipboardActionsHandler {

    private URI fileUri = null;
    private FileType fileType = null;
    private XBACatalog catalog;
    private final TreeDocument mainDoc;
    private PropertyChangeListener propertyChangeListener = null;

    private final XBDocumentPanel documentPanel;

    private XBTBlock selectedItem = null;
    private ViewerTab viewerTab;
    private DocumentViewer activeViewer;

    private final MainDocumentViewer mainViewer;
    private final BinaryDocumentViewer binaryViewer;
    private final TextDocumentViewer textViewer;
    private final PropertiesDocumentViewer propertiesViewer;

    private XBApplication application;
    private XBUndoHandler undoHandler;
    private XBPluginRepository pluginRepository;
    private final List<DocumentItemSelectionListener> itemSelectionListeners = new ArrayList<>();

    public DocumentViewerProvider(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;

        mainViewer = new MainDocumentViewer();
        propertiesViewer = new PropertiesDocumentViewer();
        binaryViewer = new BinaryDocumentViewer();
        textViewer = new TextDocumentViewer();

        documentPanel = new XBDocumentPanel();
        documentPanel.addTabSwitchListener(this::setViewerTab);
        documentPanel.addItemSelectionListener((item) -> {
            this.selectedItem = item;
            if (activeViewer != null) {
                activeViewer.setSelectedItem(item);
            }

            notifyItemSelectionChanged();
        });
        mainDoc = new TreeDocument(null);
        documentPanel.setMainDoc(mainDoc);
        documentPanel.setMainTabComponent(mainViewer.getComponent());
        documentPanel.setPropertiesTabComponent(propertiesViewer.getComponent());
        documentPanel.setBinaryTabComponent(binaryViewer.getComponent());
        documentPanel.setTextTabComponent(textViewer.getComponent());

        viewerTab = ViewerTab.MAIN;
        activeViewer = mainViewer;
    }

    @Nonnull
    @Override
    public JPanel getPanel() {
        return documentPanel;
    }

    public XBTTreeDocument getDoc() {
        return mainDoc;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        documentPanel.setCatalog(catalog);
        mainDoc.setCatalog(catalog);
        mainDoc.processSpec();
        mainViewer.setCatalog(catalog);
        propertiesViewer.setCatalog(catalog);
        textViewer.setCatalog(catalog);
    }

    public XBApplication getApplication() {
        return application;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        propertiesViewer.setApplication(application);
        documentPanel.setApplication(application);
    }

    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        mainViewer.setPluginRepository(pluginRepository);
        propertiesViewer.setPluginRepository(pluginRepository);
        documentPanel.setPluginRepository(pluginRepository);
    }

    @Override
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

    @Override
    public URI getFileUri() {
        return fileUri;
    }

    @Override
    public void newFile() {
        undoHandler.clear();
        getDoc().clear();
        reportStructureChange(null);
//        updateItem();
    }

    @Override
    public String getFileName() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            return lastSegment < 0 ? path : path.substring(lastSegment + 1);
        }

        return null;
    }

    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean isModified() {
        return getDoc().wasModified();
    }

    @Override
    public void performCut() {
        if (documentPanel.isActive()) {
            CutItemAction action = new CutItemAction(this);
            action.actionPerformed(null);
            return;
        }

        activeViewer.performCut();
    }

    @Override
    public void performCopy() {
        if (documentPanel.isActive()) {
            CopyItemAction action = new CopyItemAction(this);
            action.actionPerformed(null);
            return;
        }

        activeViewer.performCopy();
    }

    @Override
    public void performPaste() {
        if (documentPanel.isActive()) {
            PasteItemAction action = new PasteItemAction(this);
            action.actionPerformed(null);
            return;
        }

        activeViewer.performPaste();
    }

    @Override
    public void performDelete() {
        if (documentPanel.isActive()) {
            DeleteItemAction action = new DeleteItemAction(this);
            action.actionPerformed(null);
            return;
        }

        activeViewer.performDelete();
    }

    @Override
    public void performSelectAll() {
        if (documentPanel.isActive()) {
            documentPanel.performSelectAll();
            return;
        }

        activeViewer.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        if (documentPanel.isActive()) {
            return documentPanel.hasSelection();
        }

        return activeViewer.isSelection();
    }

    @Override
    public boolean isEditable() {
        if (documentPanel.isActive()) {
            return documentPanel.hasSelection();
        }

        return activeViewer.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        if (documentPanel.isActive()) {
            return true;
        }

        return activeViewer.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        if (documentPanel.isActive()) {
            Clipboard clipboard = ClipboardUtils.getClipboard();
            return clipboard.isDataFlavorAvailable(XBDocTreeTransferHandler.XB_DATA_FLAVOR);
        }

        return activeViewer.canPaste();
    }

    @Override
    public boolean canDelete() {
        if (documentPanel.isActive()) {
            return documentPanel.hasSelection();
        }

        return activeViewer.canDelete();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
//        clipboardActionsUpdateListener = updateListener;
        documentPanel.addUpdateListener((e) -> {
            updateListener.stateChanged();
        });
        mainViewer.setUpdateListener(updateListener);
        binaryViewer.setUpdateListener(updateListener);
        textViewer.setUpdateListener(updateListener);
        propertiesViewer.setUpdateListener(updateListener);
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

//    public void updateItem() {
//        documentPanel.updateItem();
//    }
    public void setViewerTab(ViewerTab viewerTab) {
        if (this.viewerTab != viewerTab) {
            this.viewerTab = viewerTab;
            if (activeViewer != null) {
                activeViewer.setSelectedItem(null);
            }
            // Save changes first
            // TODO: Replace stupid buffer copy later
//                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                    try {
//                        binaryViewer.saveToStream(buffer);
//                        mainDoc.fromStreamUB(new ByteArrayInputStream(buffer.toByteArray()));
//                    } catch (XBProcessingException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                        throw new UnsupportedOperationException("Not supported yet.");
//                        // JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
//                    } catch (IOException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                    }
            switch (viewerTab) {
                case MAIN: {
                    activeViewer = mainViewer;
                    break;
                }
                case PROPERTIES: {
                    activeViewer = propertiesViewer;
                    break;
                }
                case TEXT: {
                    activeViewer = textViewer;
                    break;
                }
                case BINARY: {
                    activeViewer = binaryViewer;
                    // TODO: Replace stupid buffer copy later
//                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                    try {
//                        mainDoc.toStreamUB(buffer);
//                        binaryViewer.loadFromStream(new ByteArrayInputStream(buffer.toByteArray()), buffer.size());
//                    } catch (IOException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    break;
                }
                default:
                    throw new InternalError("Unknown mode");
            }
            activeViewer.setSelectedItem(selectedItem);

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

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
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
        mainDoc.setModified(true);
        mainDoc.processSpec();
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

//    private void updateActiveViewer() {
//        int selectedIndex = mainTabbedPane.getSelectedIndex();
//        switch (selectedIndex) {
//            case 0: {
//                activeViewer = propertiesViewer;
//                mode = PanelMode.PROPERTIES;
//                break;
//            }
//            case 1: {
//                activeViewer = textViewer;
//                mode = PanelMode.TEXT;
//                break;
//            }
//            case 2: {
//                activeViewer = binaryViewer;
//                mode = PanelMode.BINARY;
//                break;
//            }
//        }
//    }
    @ParametersAreNonnullByDefault
    private class TreeDocument extends XBTTreeDocument implements OperationListener {

        public TreeDocument(@Nullable XBCatalog catalog) {
            super(catalog);
        }

        @Override
        public void notifyChange(OperationEvent event) {
            Operation operation = event.getOperation();
            // TODO Consolidate
            mainDoc.processSpec();
            reportStructureChange(null);
            // getDoc().setModified(true);
//            updateItem();
//            updateActionStatus(null);
//            if (clipboardActionsUpdateListener != null) {
//                clipboardActionsUpdateListener.stateChanged();
//            }

            if (operation instanceof XBTDocOperation) {
                setViewerTab(ViewerTab.MAIN);
            } else {
                // TODO
            }
        }
    }

    public enum ViewerTab {
        MAIN,
        PROPERTIES,
        TEXT,
        BINARY
    }
}
