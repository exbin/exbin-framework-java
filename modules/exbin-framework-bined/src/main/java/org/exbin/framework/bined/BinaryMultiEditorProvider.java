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
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.file.api.FileActionsApi;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.FileTypes;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider implements MultiEditorProvider, BinEdEditorProvider {

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

    public BinaryMultiEditorProvider(XBApplication application, ResourceBundle resourceBundle) {
        init(application, resourceBundle);
    }

    private void init(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        multiEditorPanel.setControl(new MultiEditorPanel.Control() {
            @Override
            public void activeIndexChanged(int index) {
                FileHandlerApi activeFile = multiEditorPanel.getActiveFile();

                for (ActiveFileChangeListener listener : activeFileChangeListeners) {
                    listener.activeFileChanged(activeFile);
                }
            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
                JPopupMenu fileTabPopupMenu = new JPopupMenu();
                CloseFileAction closeFileAction = (CloseFileAction) editorModule.getCloseFileAction();
                JMenuItem closeMenuItem = new JMenuItem(closeFileAction);
                closeMenuItem.addActionListener((ActionEvent e) -> {
                    FileHandlerApi fileHandler = multiEditorPanel.getFileHandler(index);
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
    public Optional<FileHandlerApi> getActiveFile() {
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
        FileHandlerApi activeFile = multiEditorPanel.getActiveFile();
        return activeFile == null ? "" : ((BinEdFileHandler) activeFile).getWindowTitle(parentTitle);
    }

    @Nullable
    private BinEdComponentPanel getComponent() {
        FileHandlerApi activeFile = multiEditorPanel.getActiveFile();
        return activeFile == null ? null : (BinEdComponentPanel) activeFile.getComponent();
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        BinEdFileHandler newFile = new BinEdFileHandler(fileIndex);
        newFile.newFile();
        setupFile(newFile);
        multiEditorPanel.addFileHandler(newFile, getFileName(newFile));
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        BinEdFileHandler file = new BinEdFileHandler(++lastIndex);
        file.loadFromFile(fileUri, fileType);
        setupFile(file);
        multiEditorPanel.addFileHandler(file, file.getFileName().orElse(""));
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
        FileHandlerApi activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        if (activeFile.getFileUri().isEmpty()) {
            saveAsFile();
        } else {
            ((BinEdFileHandler) activeFile).saveFile();
        }
    }

    @Override
    public void saveAsFile() {
        FileHandlerApi activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.getFileActions().saveAsFile(activeFile, fileTypes);
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

    public boolean releaseFile(FileHandlerApi fileHandler) {
        if (fileHandler.isModified()) {
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes);
        }

        return true;
    }

    @Nonnull
    private String getFileName(FileHandlerApi fileHandler) {
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
        FileHandlerApi activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        closeFile(activeFile);
    }

    @Override
    public void closeFile(FileHandlerApi file) {

    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        getComponent().registerBinaryStatus(binaryStatus);
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        getComponent().registerEncodingStatus(encodingStatus);
    }

    @Override
    public void addActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.add(listener);
    }

    @Override
    public void removeActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.remove(listener);
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
}
