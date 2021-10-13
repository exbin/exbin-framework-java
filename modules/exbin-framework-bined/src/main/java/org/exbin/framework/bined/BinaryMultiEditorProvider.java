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
import java.awt.event.ActionEvent;
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
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.MultiEditorUndoHandler;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.file.api.FileActionsApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.FileTypes;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.file.api.FileHandler;
import org.exbin.framework.gui.undo.api.UndoFileHandler;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/10/13
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
    private PropertyChangeListener propertyChangeListener;
    private EditorModificationListener editorModificationListener;
    private BinaryStatusApi binaryStatusApi;
    private TextEncodingStatusApi textEncodingStatusApi;
    private MultiEditorUndoHandler undoHandler = new MultiEditorUndoHandler();

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
                GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
                JPopupMenu fileTabPopupMenu = new JPopupMenu();
                CloseFileAction closeFileAction = (CloseFileAction) editorModule.getCloseFileAction();
                JMenuItem closeMenuItem = new JMenuItem(closeFileAction);
                closeMenuItem.addActionListener((ActionEvent e) -> {
                    FileHandler fileHandler = multiEditorPanel.getFileHandler(index);
                    if (releaseFile(fileHandler)) {
                        multiEditorPanel.removeFileHandler(fileHandler);
                        closeFile(fileHandler);
                    }
                });
                fileTabPopupMenu.add(closeMenuItem);
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
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.ofNullable(multiEditorPanel.getActiveFile());
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
        ((BinEdComponentPanel) getComponent()).setPropertyChangeListener(propertyChangeListener);
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
        fileHandler.getComponent().registerBinaryStatus(new BinaryStatusWrapper());
        fileHandler.getComponent().registerEncodingStatus(new TextEncodingStatusWrapper());
        fileHandler.getUndoHandler().addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                undoHandler.notifyUndoUpdate();
            }

            @Override
            public void undoCommandAdded(Command cmnd) {
                undoHandler.notifyUndoCommandAdded(cmnd);
            }
        });
        return fileHandler;
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

    private void activeFileChanged() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        undoHandler.setActiveFile(activeFile);

        for (ActiveFileChangeListener listener : activeFileChangeListeners) {
            listener.activeFileChanged(activeFile);
        }
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
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        closeFile(activeFile);
    }

    @Override
    public void closeFile(FileHandler file) {

    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        this.binaryStatusApi = binaryStatus;
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        this.textEncodingStatusApi = encodingStatus;
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

    @ParametersAreNonnullByDefault
    private class BinaryStatusWrapper implements BinaryStatusApi {

        @Override
        public void setCursorPosition(CodeAreaCaretPosition cursorPosition) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setCursorPosition(cursorPosition);
            }
        }

        @Override
        public void setSelectionRange(SelectionRange selectionRange) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setSelectionRange(selectionRange);
            }
        }

        @Override
        public void setEditMode(EditMode mode, EditOperation operation) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setEditMode(mode, operation);
            }
        }

        @Override
        public void setControlHandler(BinaryStatusApi.StatusControlHandler statusControlHandler) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setControlHandler(statusControlHandler);
            }
        }

        @Override
        public void setCurrentDocumentSize(long documentSize, long initialDocumentSize) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setCurrentDocumentSize(documentSize, initialDocumentSize);
            }
        }

        @Override
        public void setMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
            if (binaryStatusApi != null) {
                binaryStatusApi.setMemoryMode(memoryMode);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private class TextEncodingStatusWrapper implements TextEncodingStatusApi {

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
}
