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
package org.exbin.framework.editor;

import java.awt.Component;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.ComponentActivationProvider;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.editor.action.EditorActions;
import org.exbin.framework.editor.api.EditorFileHandler;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.editor.gui.MultiEditorPanel;
import org.exbin.framework.file.api.AllFileTypes;
import org.exbin.framework.file.api.FileActionsApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.file.api.EditableFileHandler;

/**
 * Default multi editor provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public abstract class DefaultMultiEditorProvider implements MultiEditorProvider {

    public static final String FILE_CONTEXT_MENU_ID = "fileContextMenu";

    protected final List<FileHandler> fileHandlers = new ArrayList<>();
    protected FileTypes fileTypes = new AllFileTypes();
    protected final MultiEditorPanel multiEditorPanel = new MultiEditorPanel();
    protected int lastIndex = 0;
    protected int lastNewFileIndex = 0;
    protected final Map<Integer, Integer> newFilesMap = new HashMap<>();
    protected EditorModificationListener editorModificationListener;

    @Nullable
    protected FileHandler activeFile = null;
    @Nullable
    protected File lastUsedDirectory;

    public DefaultMultiEditorProvider() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
        actionModule.registerMenu(FILE_CONTEXT_MENU_ID, EditorModule.MODULE_ID);
        actionModule.registerMenuItem(FILE_CONTEXT_MENU_ID, EditorModule.MODULE_ID, editorModule.createCloseFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(FILE_CONTEXT_MENU_ID, EditorModule.MODULE_ID, editorModule.createCloseAllFilesAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(FILE_CONTEXT_MENU_ID, EditorModule.MODULE_ID, editorModule.createCloseOtherFilesAction(), new MenuPosition(PositionMode.TOP));

        multiEditorPanel.setController(new MultiEditorPanel.Controller() {
            @Override
            public void activeIndexChanged(int index) {
                activeFileChanged();
            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                if (index < 0) {
                    return;
                }

                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ComponentActivationService componentActivationService = frameModule.getFrameHandler().getComponentActivationService();
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                JPopupMenu fileContextPopupMenu = new JPopupMenu();
                actionModule.buildMenu(fileContextPopupMenu, FILE_CONTEXT_MENU_ID, componentActivationService);
                fileContextPopupMenu.show(component, positionX, positionY);
                // TODO dispose?
            }
        });

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.getFrameHandler().getComponentActivationListener().updated(EditorProvider.class, this);
        activeFileChanged();
    }

    public void activeFileChanged() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ComponentActivationListener componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();

        if (activeFile instanceof EditorFileHandler) {
            ((EditorFileHandler) activeFile).componentDeactivated(componentActivationListener); // componentActivationService.getFileActivationListener(activeFile));
        }

        updateActiveFile();
        componentActivationListener.updated(FileHandler.class, activeFile);
//        ComponentActivationService fileComponentActivationService = activeFile instanceof ComponentActivationProvider ? ((ComponentActivationProvider) activeFile).getComponentActivationService() : null;
//        fileComponentActivationService.requestUpdate();
//        fileComponentActivationService.passRequestUpdate(fileComponentActivationService);
        if (activeFile instanceof EditorFileHandler) {
            ((EditorFileHandler) activeFile).componentActivated(componentActivationListener); // componentActivationService.getFileActivationListener(activeFile));
        }
    }

    /**
     * TODO: Temporary method, rework provider later
     */
    public void updateActiveFile() {
        int activeIndex = multiEditorPanel.getActiveIndex();
        activeFile = activeIndex >= 0 ? fileHandlers.get(activeIndex) : null;
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.ofNullable(activeFile);
    }

    @Nonnull
    public abstract EditableFileHandler createFileHandler(int id);

    @Nonnull
    @Override
    public List<FileHandler> getFileHandlers() {
        return fileHandlers;
    }

    @Nonnull
    @Override
    public String getName(FileHandler fileHandler) {
        String name = fileHandler.getTitle();
        if (!name.isEmpty()) {
            return name;
        }

        return getNewFileTitlePrefix() + " " + newFilesMap.get(fileHandler.getId());
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        EditableFileHandler newFile = createFileHandler(fileIndex);
        initFileHandler(newFile);
        newFile.clearFile();
        fileHandlers.add(newFile);

        String title = getNewFileTitlePrefix() + " " + newFilesMap.get(newFile.getId());
        multiEditorPanel.addFileHandler(newFile, title);
    }

    @Override
    public void openFile() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        FileActionsApi fileActions = fileModule.getFileActions();
        FileActionsApi.OpenFileResult openFileResult = fileActions.showOpenFileDialog(fileTypes, this);
        if (openFileResult.dialogResult == JFileChooser.APPROVE_OPTION) {
            openFile(Objects.requireNonNull(openFileResult.selectedFile).toURI(), openFileResult.fileType);
        }
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        FileHandler fileHandler = createFileHandler(++lastIndex);
        initFileHandler(fileHandler);
        fileHandler.loadFromFile(fileUri, fileType);
        fileHandlers.add(fileHandler);
        multiEditorPanel.addFileHandler(fileHandler, fileHandler.getTitle());
    }

    public void initFileHandler(FileHandler fileHandler) {
        if (fileHandler instanceof ComponentActivationProvider) {
//            ComponentActivationService fileComponentActivationService = ((ComponentActivationProvider) fileHandler).getComponentActivationService();
//            fileComponentActivationService.registerListener(new ComponentActivationListener() {
//                @Override
//                public <T> void updated(Class<T> instanceClass, T instance) {
//                    if (fileHandler == activeFile) {
//                        componentActivationService.passUpdate(instanceClass, instance);
//                    }
//                }
//            });
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
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveFile(activeFile);
    }

    @Override
    public void saveFile(FileHandler fileHandler) {
        if (!(fileHandler instanceof EditableFileHandler)) {
            throw new IllegalStateException("Unable to save file" + fileHandler == null ? "" : " " + fileHandler.getTitle());
        }

        if (fileHandler.getFileUri().isPresent()) {
            ((EditableFileHandler) fileHandler).saveFile();
        } else {
            saveAsFile(fileHandler);
        }
    }

    @Override
    public void saveAsFile() {
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveAsFile(activeFile);
    }

    @Override
    public void saveAsFile(FileHandler fileHandler) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.getFileActions().saveAsFile(fileHandler, fileTypes, this);
    }

    @Override
    public boolean canSave() {
        if (!(activeFile instanceof EditableFileHandler)) {
            return false;
        }

        return ((EditableFileHandler) activeFile).canSave();
    }

    @Override
    public void saveAllFiles() {
        if (fileHandlers.isEmpty()) {
            return;
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return;
        }

        EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Override
    public void closeFile() {
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        closeFile(activeFile);
    }

    @Override
    public void closeFile(FileHandler fileHandler) {
        if (releaseFile(fileHandler)) {
            int index = fileHandlers.indexOf(fileHandler);
            if (index >= 0) {
                fileHandlers.remove(index);
                multiEditorPanel.removeFileHandlerAt(index);
                newFilesMap.remove(fileHandler.getId());
            }
        }
    }

    @Override
    public void closeOtherFiles(FileHandler exceptHandler) {
        if (releaseOtherFiles(exceptHandler)) {
            int exceptIndex = fileHandlers.indexOf(exceptHandler);
            removeAllFileHandlersExceptFile(exceptHandler);
            multiEditorPanel.removeAllFileHandlersExceptFile(exceptIndex);
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
            fileHandlers.clear();
            multiEditorPanel.removeAllFileHandlers();
            newFilesMap.clear();
        }
    }

    public void removeAllFileHandlersExceptFile(FileHandler exceptHandler) {
        for (int i = fileHandlers.size() - 1; i >= 0; i--) {
            if (fileHandlers.get(i) != exceptHandler) {
                fileHandlers.remove(i);
            }
        }
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        if (activeFile == null) {
            return parentTitle;
        }

        Optional<URI> fileUri = activeFile.getFileUri();
        if (fileUri.isPresent()) {
            String path = fileUri.get().getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + parentTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + parentTitle;
        }

        return parentTitle;
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseOtherFiles(null);
    }

    private boolean releaseOtherFiles(@Nullable FileHandler excludedFile) {
        if (fileHandlers.isEmpty()) {
            return true;
        }

        if (fileHandlers.size() == 1) {
            return (activeFile == excludedFile) || releaseFile(activeFile);
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified() && fileHandler != excludedFile) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return true;
        }

        EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        return editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Nonnull
    @Override
    public Optional<File> getLastUsedDirectory() {
        return Optional.ofNullable(lastUsedDirectory);
    }

    @Override
    public void setLastUsedDirectory(File directory) {
        lastUsedDirectory = directory;
    }

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    @Nonnull
    public String getNewFileTitlePrefix() {
        // TODO
        return "NewFile";
    }
}
