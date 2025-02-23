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
package org.exbin.framework.editor.text;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.DefaultFileTypes;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Text editor provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEditorProvider implements EditorProvider {

    private TextFileHandler activeFile;
    private FileTypes fileTypes;

    private EditorModificationListener editorModificationListener;
    private ComponentActivationListener componentActivationListener;

    private PropertyChangeListener propertyChangeListener;
    @Nullable
    private File lastUsedDirectory;

    public TextEditorProvider() {
        this(new TextFileHandler());
    }

    public TextEditorProvider(TextFileHandler activeFile) {
        this.activeFile = activeFile;
        init();
    }

    private void init() {
        EditorTextModule editorTextModule = App.getModule(EditorTextModule.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();

        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileTypes = new DefaultFileTypes(fileModule.getFileTypes());
        TextPanel textPanel = activeFile.getComponent();
        textPanel.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (propertyChangeListener != null) {
                propertyChangeListener.propertyChange(evt);
            }
        });
        textPanel.setPopupMenu(editorTextModule.createPopupMenu(textPanel));

        activeFileChanged();
    }

    private void activeFileChanged() {
        componentActivationListener.updated(FileHandler.class, activeFile);
        if (activeFile instanceof TextFileHandler) {
            ((TextFileHandler) activeFile).componentActivated(componentActivationListener);
        }
        componentActivationListener.updated(FileOperations.class, this);
    }

    public void registerUndoHandler() {
        activeFile.registerUndoHandler();
    }

    @Nonnull
    @Override
    public TextPanel getEditorComponent() {
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

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
        activeFile.getComponent().setEditorModificationListener(editorModificationListener);
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
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        activeFile.loadFromFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
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
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseFile(activeFile);
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
    public void updateRecentFilesList(URI fileUri, @Nullable FileType fileType) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }
}
