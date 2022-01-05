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
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileHandler;

/**
 * Text editor.
 *
 * @version 0.2.2 2021/11/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEditor implements EditorProvider {

    private TextFileHandler activeFile;

    private EditorModificationListener editorModificationListener;
    private PropertyChangeListener propertyChangeListener;
    @Nullable
    private File lastUsedDirectory;

    public TextEditor() {
        init();
    }

    private void init() {
        activeFile = new TextFileHandler();

        activeFile.getComponent().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (propertyChangeListener != null) {
                propertyChangeListener.propertyChange(evt);
            }
        });
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        activeFile.newFile();
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
    }
}
