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
package org.exbin.framework.bined;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditMode;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.MultiEditorUndoHandler;
import org.exbin.framework.gui.editor.action.CloseAllFileAction;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.action.CloseOtherFileAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorPopupMenu;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.file.api.FileActionsApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.FileTypes;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.file.api.FileHandler;
import org.exbin.framework.gui.undo.api.UndoFileHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/10/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider implements MultiEditorProvider, BinEdEditorProvider, UndoFileHandler {

    private XBApplication application;
    private FileTypes fileTypes;
    private final MultiEditorPanel multiEditorPanel = new MultiEditorPanel();
    private int lastIndex = 0;
    private int lastNewFileIndex = 0;
    private final Map<Integer, Integer> newFilesMap = new HashMap<>();
    private final List<ActiveFileChangeListener> activeFileChangeListeners = new ArrayList<>();

    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;
    private JPopupMenu codeAreaPopupMenu;
//    private PropertyChangeListener propertyChangeListener;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private EditorModificationListener editorModificationListener;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi textEncodingStatusApi;
    private MultiEditorUndoHandler undoHandler = new MultiEditorUndoHandler();
    private Optional<FileHandler> activeFileCache = Optional.empty();

    public BinaryMultiEditorProvider(XBApplication application, ResourceBundle resourceBundle) {
        init(application, resourceBundle);
    }

    private void init(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        multiEditorPanel.setControl(new MultiEditorPanel.Control() {
            @Override
            public void activeIndexChanged(int index) {
                activeFileChanged();
            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                if (index < 0) {
                    return;
                }

                FileHandler fileHandler = multiEditorPanel.getFileHandler(index);
                GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
                JPopupMenu fileTabPopupMenu = new EditorPopupMenu(fileHandler);
                CloseFileAction closeFileAction = (CloseFileAction) editorModule.getCloseFileAction();
                JMenuItem closeMenuItem = new JMenuItem(closeFileAction);
                fileTabPopupMenu.add(closeMenuItem);
                CloseAllFileAction closeAllFileAction = (CloseAllFileAction) editorModule.getCloseAllFileAction();
                JMenuItem closeAllMenuItem = new JMenuItem(closeAllFileAction);
                fileTabPopupMenu.add(closeAllMenuItem);
                CloseOtherFileAction closeOtherFileAction = (CloseOtherFileAction) editorModule.getCloseOtherFileAction();
                JMenuItem closeOtherMenuItem = new JMenuItem(closeOtherFileAction);
                fileTabPopupMenu.add(closeOtherMenuItem);
                fileTabPopupMenu.show(component, positionX, positionY);
            }
        });
        fileTypes = new FileTypes() {
            @Override
            public boolean allowAllFiles() {
                return true;
            }

            @Override
            public Optional<FileType> getFileType(String fileTypeId) {
                return Optional.empty();
            }

            @Override
            public List<FileType> getFileTypes() {
                return new ArrayList<>();
            }
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });

    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return activeFileCache;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
//        this.propertyChangeListener = propertyChangeListener;
//        ((BinEdComponentPanel) getComponent()).setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
        ((BinEdComponentPanel) getComponent()).setModificationListener(editorModificationListener);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        return activeFile == null ? "" : ((BinEdFileHandler) activeFile).getWindowTitle(parentTitle);
    }

    @Nullable
    private BinEdComponentPanel getComponent() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        return activeFile == null ? null : (BinEdComponentPanel) activeFile.getComponent();
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        BinEdFileHandler newFile = createFileHandler(fileIndex);
        newFile.newFile();
        setupFile(newFile);
        multiEditorPanel.addFileHandler(newFile, getFileName(newFile));
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        BinEdFileHandler file = createFileHandler(++lastIndex);
        file.loadFromFile(fileUri, fileType);
        setupFile(file);
        multiEditorPanel.addFileHandler(file, file.getFileName().orElse(""));
    }

    @Nonnull
    private BinEdFileHandler createFileHandler(int id) {
        BinEdFileHandler fileHandler = new BinEdFileHandler(id);
        fileHandler.getUndoHandler().addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                undoHandler.notifyUndoUpdate();
                updateCurrentDocumentSize();
                // notifyModified();

            }

            @Override
            public void undoCommandAdded(Command cmnd) {
                undoHandler.notifyUndoCommandAdded(cmnd);
                updateCurrentDocumentSize();
                // notifyModified();
            }
        });
        ExtCodeArea codeArea = fileHandler.getCodeArea();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, fileHandler.getCodeAreaUndoHandler());
        codeArea.setCommandHandler(commandHandler);
        initCodeArea(codeArea);

        return fileHandler;
    }

    private void initCodeArea(ExtCodeArea codeArea) {
        codeArea.addSelectionChangedListener(() -> {
            updateClipboardActionsStatus();
        });

        codeArea.addDataChangedListener(() -> {
//            if (binarySearchPanelVisible) {
//                binarySearchPanel.dataChanged();
//            }
            updateCurrentDocumentSize();
        });
        // TODO use listener in code area component instead
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });
    }

    @Override
    public void openFile() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        FileActionsApi fileActions = fileModule.getFileActions();
        FileActionsApi.OpenFileResult openFileResult = fileActions.showOpenFileDialog(fileTypes);
        if (openFileResult.dialogResult == JFileChooser.APPROVE_OPTION) {
            openFile(CodeAreaUtils.requireNonNull(openFileResult.selectedFile).toURI(), openFileResult.fileType);
        }
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        openFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        openFile(fileUri, fileType);
    }

    @Override
    public void saveFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        if (activeFile.getFileUri().isPresent()) {
            ((BinEdFileHandler) activeFile).saveFile();
        } else {
            saveAsFile();
        }
    }

    @Override
    public void saveAsFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.getFileActions().saveAsFile(activeFile, fileTypes);
    }

    @Override
    public boolean canSave() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            return false;
        }

        return ((BinEdFileHandler) activeFile).isSaveSupported() && ((BinEdFileHandler) activeFile).isEditable();
    }

    private void activeFileChanged() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        activeFileCache = Optional.ofNullable(activeFile);
        undoHandler.setActiveFile(activeFile);

        for (ActiveFileChangeListener listener : activeFileChangeListeners) {
            listener.activeFileChanged(activeFile);
        }

        if (clipboardActionsUpdateListener != null) {
            updateClipboardActionsStatus();
        }

        if (binaryStatus != null) {
            updateCurrentDocumentSize();
            updateCurrentCaretPosition();
            updateCurrentSelectionRange();
            updateCurrentMemoryMode();
        }

//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//        encodingStatus.setEncoding(codeArea.getCharset().name());
    }

    @Override
    public boolean releaseAllFiles() {
        int fileHandlersCount = multiEditorPanel.getFileHandlersCount();
        if (fileHandlersCount == 0) {
            return true;
        }

        if (fileHandlersCount == 1) {
            return releaseFile(getActiveFile().get());
        }

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler.isModified()) {
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes);
        }

        return true;
    }

    @Nonnull
    private String getFileName(FileHandler fileHandler) {
        Optional<String> fileName = fileHandler.getFileName();
        if (!fileName.isPresent()) {
            return "New File " + newFilesMap.get(fileHandler.getId());
        }

        return fileName.orElse("");
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeFile() {
        if (activeFileCache.isEmpty()) {
            throw new IllegalStateException();
        }

        closeFile(activeFileCache.get());
    }

    @Override
    public void closeFile(FileHandler file) {
        if (releaseFile(file)) {
            multiEditorPanel.removeFileHandler(file);
        }
    }

    @Override
    public void closeOtherFiles(FileHandler fileHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeAllFiles() {
        if (releaseAllFiles()) {
            multiEditorPanel.removeAllFileHandlers();
        }
    }

    @Override
    public void saveAllFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        this.binaryStatus = binaryStatus;
//        attachCaretListener((CodeAreaCaretPosition caretPosition) -> {
//            binaryStatus.setCursorPosition(caretPosition);
//        });
//        codeArea.addSelectionChangedListener(() -> {
//            binaryStatus.setSelectionRange(codeArea.getSelection());
//        });
//
//        attachEditModeChangedListener((EditMode mode, EditOperation operation) -> {
//            binaryStatus.setEditMode(mode, operation);
//        });
//        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());

        updateCurrentMemoryMode();
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        this.textEncodingStatusApi = encodingStatus;
    }

    public void setClipboardActionsUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        updateClipboardActionsStatus();
    }

    @Override
    public void addActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.add(listener);
    }

    @Override
    public void removeActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.remove(listener);
    }

    @Nonnull
    @Override
    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        this.codeAreaPopupMenuHandler = codeAreaPopupMenuHandler;
    }

    private void setupFile(BinEdFileHandler newFile) {
        if (codeAreaPopupMenu == null) {
            String popupMenuId = BinedModule.BINARY_POPUP_MENU_ID + ".multi";

            codeAreaPopupMenu = new JPopupMenu() {
                @Override
                public void show(Component invoker, int x, int y) {
                    if (codeAreaPopupMenuHandler == null || invoker == null) {
                        return;
                    }

                    int clickedX = x;
                    int clickedY = y;
                    if (invoker instanceof JViewport) {
                        clickedX += ((JViewport) invoker).getParent().getX();
                        clickedY += ((JViewport) invoker).getParent().getY();
                    }

                    ExtCodeArea codeArea = invoker instanceof ExtCodeArea ? (ExtCodeArea) invoker
                            : (ExtCodeArea) ((JViewport) invoker).getParent().getParent();

                    JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                    popupMenu.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                        }
                    });
                    popupMenu.show(invoker, x, y);
                }
            };
        }
        newFile.getComponent().getCodeArea().setComponentPopupMenu(codeAreaPopupMenu);
    }

    private void updateClipboardActionsStatus() {
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }

//        if (copyAsCode != null) {
//            copyAsCode.setEnabled(codeArea.hasSelection());
//        }
//        if (pasteFromCode != null) {
//            pasteFromCode.setEnabled(codeArea.canPaste());
//        }
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            long documentOriginalSize = ((BinEdFileHandler) activeFile.get()).getDocumentOriginalSize();
            long dataSize = codeArea.getDataSize();
            binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
        }
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
            binaryStatus.setCursorPosition(caretPosition);
        }
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            SelectionRange selectionRange = codeArea.getSelection();
            binaryStatus.setSelectionRange(selectionRange);
        }
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
            if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
                newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
            } else if (codeArea.getContentData() instanceof DeltaDocument) {
                newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
            }

            binaryStatus.setMemoryMode(newMemoryMode);
        }
    }

    @ParametersAreNonnullByDefault
    private class TextEncodingStatusWrapper implements TextEncodingStatusApi {

        @Nonnull
        @Override
        public String getEncoding() {
            if (textEncodingStatusApi != null) {
                return textEncodingStatusApi.getEncoding();
            }
            return "";
        }

        @Override
        public void setEncoding(String encodingName) {
            if (textEncodingStatusApi != null) {
                textEncodingStatusApi.setEncoding(encodingName);
            }
        }
    }

    private class EditorPopupMenu extends JPopupMenu implements MultiEditorPopupMenu {

        @Nullable
        private final FileHandler selectedFile;

        public EditorPopupMenu(@Nullable FileHandler selectedFile) {
            super();
            this.selectedFile = selectedFile;
        }

        @Override
        public Optional<FileHandler> getSelectedFile() {
            return Optional.ofNullable(selectedFile);
        }
    }
}
