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

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.editor.xbup.gui.BlockPropertiesPanel;
import org.exbin.framework.gui.editor.MultiEditorUndoHandler;
import org.exbin.framework.gui.editor.action.CloseAllFileAction;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.action.CloseOtherFileAction;
import org.exbin.framework.gui.editor.action.EditorActions;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorPopupMenu;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.file.api.AllFileTypes;
import org.exbin.framework.gui.file.api.FileActionsApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.gui.file.api.FileHandler;
import org.exbin.framework.gui.file.api.FileTypes;

/**
 * Multi editor provider.
 *
 * @version 0.2.1 2021/12/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XbupMultiEditorProvider implements XbupEditorProvider, MultiEditorProvider, ClipboardActionsHandler {

    private final XBApplication application;
    private FileTypes fileTypes;
    private final MultiEditorPanel multiEditorPanel = new MultiEditorPanel();
    private XBACatalog catalog;
    private PropertyChangeListener propertyChangeListener = null;

    private int lastIndex = 0;
    private int lastNewFileIndex = 0;
    private final Map<Integer, Integer> newFilesMap = new HashMap<>();
    private FileHandlingMode defaultFileHandlingMode = FileHandlingMode.MEMORY;
    private final List<ActiveFileChangeListener> activeFileChangeListeners = new ArrayList<>();

    private ClipboardActionsHandler activeHandler;

    private XBPluginRepository pluginRepository;
    private final List<DocumentItemSelectionListener> itemSelectionListeners = new ArrayList<>();
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private MultiEditorUndoHandler undoHandler = new MultiEditorUndoHandler();
    private Optional<FileHandler> activeFileCache = Optional.empty();
    private boolean devMode = false;
    @Nullable
    private File lastUsedDirectory;

    public XbupMultiEditorProvider(XBApplication application) {
        this.application = application;
        init();
    }

    private void init() {
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
        fileTypes = new AllFileTypes();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            // TODO updateClipboardActionsStatus();
        });
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return activeFileCache;
    }

    @Nonnull
    @Override
    public Optional<File> getLastUsedDirectory() {
        return Optional.ofNullable(lastUsedDirectory);
    }

    @Override
    public void setLastUsedDirectory(@Nullable File directory) {
        lastUsedDirectory = directory;
    }

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    private void activeFileChanged() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        activeFileCache = Optional.ofNullable(activeFile);
        undoHandler.setActiveFile(activeFile);

        for (ActiveFileChangeListener listener : activeFileChangeListeners) {
            listener.activeFileChanged(activeFile);
        }

        if (clipboardActionsUpdateListener != null) {
            // TODO updateClipboardActionsStatus();
        }
    }

    @Override
    public void setModificationListener(EditorProvider.EditorModificationListener editorModificationListener) {
        // TODO
    }

    @Override
    public XBACatalog getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        if (activeFileCache.isPresent()) {
            ((XbupFileHandler) activeFileCache.get()).setCatalog(catalog);
        }
    }

    @Override
    public XBApplication getApplication() {
        return application;
    }

    @Override
    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    @Override
    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        // activeFile.setPluginRepository(pluginRepository);
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        // activeFile.setDevMode(devMode);
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
        // activeFile.getComponent().setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        XbupFileHandler activeFile = (XbupFileHandler) multiEditorPanel.getActiveFile();
        XBTTreeDocument treeDocument = activeFile.getDoc();
        String fileName = treeDocument.getFileName();
        if (!"".equals(fileName)) {
            int pos;
            int newpos = 0;
            do {
                pos = newpos;
                newpos = fileName.indexOf(File.separatorChar, pos) + 1;
            } while (newpos > 0);
            return fileName.substring(pos) + " - " + frameTitle;
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
        // activeFile.setUpdateListener(updateListener);
    }

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        XbupFileHandler activeFile = (XbupFileHandler) multiEditorPanel.getActiveFile();
        if (activeFile != null) {
            panel.setBlock(activeFile.getSelectedItem().orElse(null));
        }
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(null);
    }

    @Override
    public void addItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.add(listener);
    }

    @Override
    public void removeItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.remove(listener);
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        XbupFileHandler newFile = createFileHandler(fileIndex);
        newFile.newFile();
        multiEditorPanel.addFileHandler(newFile, getName(newFile));
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        XbupFileHandler file = createFileHandler(++lastIndex);
        file.loadFromFile(fileUri, fileType);
        multiEditorPanel.addFileHandler(file, file.getFileName().orElse(""));
    }

    @Nonnull
    private XbupFileHandler createFileHandler(int id) {
        XbupFileHandler fileHandler = new XbupFileHandler(id);
        fileHandler.setItemSelectionListener((block) -> {
            itemSelectionListeners.forEach(listener -> {
                listener.itemSelected(block);
            });
        });
        fileHandler.setUndoHandler(undoHandler);
        fileHandler.setCatalog(catalog);

        return fileHandler;
    }

    @Override
    public void openFile() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        FileActionsApi fileActions = fileModule.getFileActions();
        FileActionsApi.OpenFileResult openFileResult = fileActions.showOpenFileDialog(fileTypes, this);
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
    public boolean canSave() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            return false;
        }

        return ((XbupFileHandler) activeFile).isSaveSupported() && ((XbupFileHandler) activeFile).isEditable();
    }

    @Override
    public void saveFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveFile(activeFile);
    }

    @Override
    public void saveFile(FileHandler fileHandler) {
        if (fileHandler.getFileUri().isPresent()) {
            ((BinEdFileHandler) fileHandler).saveFile();
        } else {
            saveAsFile(fileHandler);
        }
    }

    @Override
    public void saveAsFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveAsFile(activeFile);
    }

    @Override
    public void saveAsFile(FileHandler fileHandler) {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.getFileActions().saveAsFile(fileHandler, fileTypes, this);
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler.isModified()) {
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, null, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseOtherFiles(null);
    }

    private boolean releaseOtherFiles(@Nullable FileHandler excludedFile) {
        int fileHandlersCount = multiEditorPanel.getFileHandlersCount();
        if (fileHandlersCount == 0) {
            return true;
        }

        if (fileHandlersCount == 1) {
            FileHandler activeFile = getActiveFile().get();
            return (activeFile == excludedFile) || releaseFile(activeFile);
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (int i = 0; i < fileHandlersCount; i++) {
            FileHandler fileHandler = multiEditorPanel.getFileHandler(i);
            if (fileHandler.isModified() && fileHandler != excludedFile) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return true;
        }

        GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        return editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Override
    public List<FileHandler> getFileHandlers() {
        List<FileHandler> fileHandlers = new ArrayList<>();
        for (int i = 0; i < multiEditorPanel.getFileHandlersCount(); i++) {
            fileHandlers.add(multiEditorPanel.getFileHandler(i));
        }
        return fileHandlers;
    }

    @Nonnull
    @Override
    public String getName(FileHandler fileHandler) {
        Optional<String> fileName = fileHandler.getFileName();
        if (fileName.isPresent()) {
            return fileName.get();
        }

        return "New File " + newFilesMap.get(fileHandler.getId());
    }

    @Override
    public void closeFile() {
        if (!activeFileCache.isPresent()) {
            throw new IllegalStateException();
        }

        closeFile(activeFileCache.get());
    }

    @Override
    public void closeFile(FileHandler file) {
        if (releaseFile(file)) {
            multiEditorPanel.removeFileHandler(file);
            newFilesMap.remove(file.getId());
        }
    }

    @Override
    public void closeOtherFiles(FileHandler exceptHandler) {
        if (releaseOtherFiles(exceptHandler)) {
            multiEditorPanel.removeAllFileHandlersExceptFile(exceptHandler);
            int exceptionFileId = exceptHandler.getId();
            // I miss List.of()
            List<Integer> list = new ArrayList<>();
            list.add(exceptionFileId);
            newFilesMap.keySet().retainAll(list);
        }
    }

    @Override
    public void closeAllFiles() {
        if (releaseAllFiles()) {
            multiEditorPanel.removeAllFileHandlers();
            newFilesMap.clear();
        }
    }

    @Override
    public void saveAllFiles() {
        int fileHandlersCount = multiEditorPanel.getFileHandlersCount();
        if (fileHandlersCount == 0) {
            return;
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (int i = 0; i < fileHandlersCount; i++) {
            FileHandler fileHandler = multiEditorPanel.getFileHandler(i);
            if (fileHandler.isModified()) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return;
        }

        GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Override
    public void addActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.add(listener);
    }

    @Override
    public void removeActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.remove(listener);
    }

    private class EditorPopupMenu extends JPopupMenu implements MultiEditorPopupMenu {

        @Nullable
        private final FileHandler selectedFile;

        public EditorPopupMenu(@Nullable FileHandler selectedFile) {
            super();
            this.selectedFile = selectedFile;
        }

        @Nonnull
        @Override
        public Optional<FileHandler> getSelectedFile() {
            return Optional.ofNullable(selectedFile);
        }
    }
}