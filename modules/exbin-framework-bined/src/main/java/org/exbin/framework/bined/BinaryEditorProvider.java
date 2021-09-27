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
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileHandlerApi;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/09/27
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
    public FileHandlerApi getActiveFile() {
        return activeFile;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return activeFile.getComponent();
    }

    @Nonnull
    private BinEdComponentPanel getBinEdComponentPanel() {
        return (BinEdComponentPanel) activeFile.getComponent();
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        getBinEdComponentPanel().setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        getBinEdComponentPanel().setModificationListener(editorModificationListener);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        return activeFile.getWindowTitle(parentTitle);
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        getBinEdComponentPanel().registerBinaryStatus(binaryStatus);
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        getBinEdComponentPanel().registerEncodingStatus(encodingStatus);
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return getBinEdComponentPanel().getCurrentColors();
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return getBinEdComponentPanel().getDefaultColors();
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        getBinEdComponentPanel().setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return getBinEdComponentPanel().isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        getBinEdComponentPanel().setWordWrapMode(mode);
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return getBinEdComponentPanel().getCharset();
    }

    @Override
    public int getId() {
        return activeFile.getId();
    }

    @Override
    public void setCharset(Charset charset) {
        getBinEdComponentPanel().setCharset(charset);
    }

    @Override
    public boolean isShowNonprintables() {
        return getBinEdComponentPanel().isShowNonprintables();
    }

    @Override
    public void setShowNonprintables(boolean show) {
        getBinEdComponentPanel().setShowNonprintables(show);
    }

    @Override
    public boolean isShowValuesPanel() {
        return getBinEdComponentPanel().isShowValuesPanel();
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        getBinEdComponentPanel().setShowValuesPanel(show);
    }

    @Override
    public boolean changeLineWrap() {
        return getBinEdComponentPanel().changeLineWrap();
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponentPanel() {
        return (BinEdComponentPanel) activeFile.getComponent();
    }

    @Nonnull
    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        return getBinEdComponentPanel().getUndoHandler();
    }

    @Nonnull
    @Override
    public ExtCodeArea getCodeArea() {
        return getBinEdComponentPanel().getCodeArea();
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        getBinEdComponentPanel().setFileHandlingMode(fileHandlingMode);
    }
}
