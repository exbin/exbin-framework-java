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
import java.nio.charset.Charset;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/09/27
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider implements MultiEditorProvider, BinaryEditorControl {

    public BinaryMultiEditorProvider() {
    }

    @Nonnull
    @Override
    public BinEdFileHandler getActiveFile() {
        return null;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return getActiveFile().getComponent();
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        ((BinEdComponentPanel) getComponent()).setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        ((BinEdComponentPanel) getComponent()).setModificationListener(editorModificationListener);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        return getActiveFile().getWindowTitle(parentTitle);
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return getActiveFile().getComponent();
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        getActiveFile().loadFromFile(fileUri, fileType);
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        getActiveFile().saveToFile(fileUri, fileType);
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return getActiveFile().getFileUri();
    }

    @Nonnull
    @Override
    public Optional<String> getFileName() {
        return getActiveFile().getFileName();
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.empty();
    }

    @Override
    public void setFileType(FileType fileType) {
    }

    @Override
    public void newFile() {
        getActiveFile().newFile();
    }

    @Override
    public boolean isModified() {
        return ((BinEdComponentPanel) getComponent()).isModified();
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {

    }

    @Override
    public void closeFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeFile(FileHandlerApi file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWordWrapMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Charset getCharset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCharset(Charset charset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isShowNonprintables() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setShowNonprintables(boolean show) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isShowValuesPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean changeLineWrap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BinEdComponentPanel getComponentPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtCodeArea getCodeArea() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
