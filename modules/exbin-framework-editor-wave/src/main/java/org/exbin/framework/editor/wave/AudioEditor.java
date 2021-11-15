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
package org.exbin.framework.editor.wave;

import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Audio editor.
 *
 * @version 0.2.2 2021/11/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioEditor implements EditorProvider {

    private AudioFileHandler activeFile;
    private JPopupMenu popupMenu;
    private MouseMotionListener mouseMotionListener;
    private AudioPanel.StatusChangeListener statusChangeListener;
    private AudioPanel.WaveRepaintListener waveRepaintListener;
    private XBUndoHandler undoHandler;
    @Nullable
    private File lastUsedDirectory;

    public AudioEditor() {
        init();
    }

    private void init() {
        activeFile = new AudioFileHandler();
    }

    @Nonnull
    @Override
    public AudioPanel getEditorComponent() {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.setPopupMenu(popupMenu);
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.attachCaretListener(mouseMotionListener);
    }

    public void setStatusChangeListener(AudioPanel.StatusChangeListener statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.addStatusChangeListener(statusChangeListener);
    }

    public void setWaveRepaintListener(AudioPanel.WaveRepaintListener waveRepaintListener) {
        this.waveRepaintListener = waveRepaintListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.addWaveRepaintListener(waveRepaintListener);
    }

    public void setUndoHandler(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.setUndoHandler(undoHandler);
    }
}
