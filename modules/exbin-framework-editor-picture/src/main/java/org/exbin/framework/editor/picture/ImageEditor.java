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
package org.exbin.framework.editor.picture;

import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.DefaultFileTypes;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Image editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ImageEditor implements EditorProvider {

    private ImageFileHandler activeFile;
    private FileTypes fileTypes;

    private EditorModificationListener editorModificationListener;
    private JPopupMenu popupMenu;
    private MouseMotionListener mouseMotionListener;
    @Nullable
    private File lastUsedDirectory;
    private ComponentActivationListener componentActivationListener;

    public ImageEditor() {
        init();
    }

    private void init() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
        activeFile = new ImageFileHandler();
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileTypes = new DefaultFileTypes(fileModule.getFileTypes());
        componentActivationListener.updated(EditorProvider.class, this);
        activeFileChanged();
    }

    private void activeFileChanged() {
        componentActivationListener.updated(FileHandler.class, activeFile);
    }

    public void registerUndoHandler() {
        activeFile.registerUndoHandler();
    }

    @Nonnull
    @Override
    public ImagePanel getEditorComponent() {
        return activeFile.getComponent();
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.of(activeFile);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        URI fileUri = activeFile.getFileUri().orElse(null);
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + parentTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + parentTitle;
        }

        return parentTitle;
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        activeFile.loadFromFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
    }

    @Override
    public void newFile() {
        if (releaseAllFiles()) {
            activeFile.clearFile();
        }
    }

    @Override
    public void openFile() {
        if (releaseAllFiles()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            fileModule.getFileActions().openFile(activeFile, fileTypes, this);
        }
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public void saveFile() {
        Optional<URI> fileUri = activeFile.getFileUri();
        if (fileUri.isPresent()) {
            activeFile.saveToFile(fileUri.get(), activeFile.getFileType().orElse(null));
        } else {
            saveAsFile();
        }
    }

    @Override
    public void saveAsFile() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.getFileActions().saveAsFile(activeFile, fileTypes, this);
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseFile(activeFile);
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes, this);
        }

        return true;
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
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        ImagePanel imagePanel = (ImagePanel) activeFile.getComponent();
        imagePanel.setPopupMenu(popupMenu);
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
        ImagePanel imagePanel = (ImagePanel) activeFile.getComponent();
        imagePanel.attachCaretListener(mouseMotionListener);
    }
}
