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

import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/09/28
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorProvider implements EditorProvider, BinaryEditorControl {

    private BinEdFileHandler activeFile;

    public BinaryEditorProvider(BinEdFileHandler activeFile) {
        this.activeFile = activeFile;
    }

    @Nonnull
    @Override
    public BinEdFileHandler getActiveFile() {
        return activeFile;
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponentPanel() {
        return (BinEdComponentPanel) activeFile.getComponent();
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getEditorComponent() {
        return activeFile.getComponent();
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        getEditorComponent().setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        getEditorComponent().setModificationListener(editorModificationListener);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        return activeFile.getWindowTitle(parentTitle);
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        getEditorComponent().registerBinaryStatus(binaryStatus);
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        getEditorComponent().registerEncodingStatus(encodingStatus);
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return getEditorComponent().getCurrentColors();
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return getEditorComponent().getDefaultColors();
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        getEditorComponent().setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return getEditorComponent().isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        getEditorComponent().setWordWrapMode(mode);
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return getEditorComponent().getCharset();
    }

    @Override
    public int getId() {
        return activeFile.getId();
    }

    @Override
    public void setCharset(Charset charset) {
        getEditorComponent().setCharset(charset);
    }

    @Override
    public boolean isShowNonprintables() {
        return getEditorComponent().isShowNonprintables();
    }

    @Override
    public void setShowNonprintables(boolean show) {
        getEditorComponent().setShowNonprintables(show);
    }

    @Override
    public boolean isShowValuesPanel() {
        return getEditorComponent().isShowValuesPanel();
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        getEditorComponent().setShowValuesPanel(show);
    }

    @Override
    public boolean changeLineWrap() {
        return getEditorComponent().changeLineWrap();
    }

    @Nonnull
    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        return getEditorComponent().getUndoHandler();
    }

    @Nonnull
    @Override
    public ExtCodeArea getCodeArea() {
        return getEditorComponent().getCodeArea();
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        getEditorComponent().setFileHandlingMode(fileHandlingMode);
    }

    @Override
    public void newFile() {
        activeFile.newFile();
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
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
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAsFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseAllFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
