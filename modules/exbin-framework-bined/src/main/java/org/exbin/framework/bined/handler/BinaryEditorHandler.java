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
package org.exbin.framework.bined.handler;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.editor.tab.api.EditorViewHandling;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.bined.BinEdEditorProvider;

/**
 * Binary editor provider.
 *
 * @version 0.2.1 2020/11/23
 * @author ExBin Project (http://exbin.org)
 */
public class BinaryEditorHandler { //implements BinaryEditorControl, MultiEditorProvider, ClipboardActionsHandler, TextFontApi {
/*
    private BinaryPanelInit binaryPanelInit = null;
    private final List<BinEdFileHandler> files = new ArrayList<>();
    private EditorViewHandling editorViewHandling = null;
    private SegmentsRepository segmentsRepository;
    private BinEdFileHandler activeFile = null;
    private int lastIndex = 0;
    private BinaryStatusApi binaryStatus = null;
    private TextEncodingStatusApi encodingStatus;
    private EditorModificationListener editorModificationListener = null;
    private final EditorModificationListener multiModificationListener;
    private final List<BinaryDataUndoUpdateListener> undoListeners = new ArrayList<>();
    private final BinaryDataUndoUpdateListener multiUndoUpdateListener;
    private ClipboardActionsUpdateListener clipboardUpdateListener = null;
    private final ClipboardActionsUpdateListener multiClipboardUpdateListener;
    private MultiEditorPanel multiEditorPanel = new MultiEditorPanel();

    public BinaryEditorHandler() {
        multiModificationListener = () -> {
            if (editorModificationListener != null) {
                editorModificationListener.modified();
            }
            if (editorViewHandling != null) {
                editorViewHandling.updateEditorView(activeFile);
            }
        };

        multiUndoUpdateListener = new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                notifyUndoChanged();
            }

            @Override
            public void undoCommandAdded(BinaryDataCommand cmnd) {
                undoListeners.forEach((listener) -> {
                    listener.undoCommandAdded(cmnd);
                });
            }
        };

        multiClipboardUpdateListener = this::notifyClipboardStateChanged;
    }

    @Override
    public void setPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        activeFile.setPropertyChangeListener((PropertyChangeEvent evt) -> {
            editorViewHandling.addEditorView(activeFile);
            propertyChangeListener.propertyChange(evt);
        });
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        return activeFile.getWindowTitle(frameTitle);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        BinEdFileHandler createdFile = createNewFile();
        createdFile.newFile();
        createdFile.loadFromFile(fileUri, fileType);
        editorViewHandling.updateEditorView(createdFile);
        activeFile = createdFile;
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        activeFile.saveToFile(fileUri, fileType);
        editorViewHandling.updateEditorView(activeFile);
    }

    @Override
    public void newFile() {
        BinEdFileHandler createdFile = createNewFile();
        createdFile.newFile();
        activeFile = createdFile;
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        if (!files.isEmpty()) {
            files.forEach((panel) -> {
                panel.registerBinaryStatus(binaryStatusApi);
            });
        }
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        if (!files.isEmpty()) {
            files.forEach((panel) -> {
                panel.registerEncodingStatus(encodingStatusApi);
            });
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    private synchronized BinEdFileHandler createNewFile() {
        BinEdFileHandler createdFile = new BinEdFileHandler(lastIndex);
        createdFile.setSegmentsRepository(segmentsRepository);
        lastIndex++;
        files.add(createdFile);
        if (binaryPanelInit != null) {
            binaryPanelInit.init(createdFile);
        }
        if (binaryStatus != null) {
            createdFile.registerBinaryStatus(binaryStatus);
            createdFile.registerEncodingStatus(encodingStatus);
        }
        if (editorViewHandling != null) {
            editorViewHandling.addEditorView(createdFile);
        }
        createdFile.setModificationListener(multiModificationListener);
        createdFile.getUndoHandler().addUndoUpdateListener(multiUndoUpdateListener);
        createdFile.setUpdateListener(multiClipboardUpdateListener);

        return createdFile;
    }

    public void init() {
        activeFile = createNewFile();
        activeFile.newFile();
    }

    public BinaryPanelInit getBinaryPanelInit() {
        return binaryPanelInit;
    }

    public void setBinaryPanelInit(BinaryPanelInit binaryPanelInit) {
        this.binaryPanelInit = binaryPanelInit;
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        activeFile.setFileHandlingMode(fileHandlingMode);
    }

    @Nullable
    public EditorViewHandling getEditorViewHandling() {
        return editorViewHandling;
    }

    public void setEditorViewHandling(EditorViewHandling editorViewHandling) {
        this.editorViewHandling = editorViewHandling;
        editorViewHandling.setMultiEditorProvider(this);
    }

    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return activeFile.getCurrentColors();
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return activeFile.getDefaultColors();
    }

    @Nonnull
    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        activeFile.setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return ((RowWrappingCapable) activeFile.getCodeArea()).getRowWrapping() == RowWrappingMode.WRAPPING;
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        ((RowWrappingCapable) activeFile.getCodeArea()).setRowWrapping(mode ? RowWrappingMode.WRAPPING : RowWrappingMode.NO_WRAPPING);
    }

    @Override
    public Charset getCharset() {
        return activeFile.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        activeFile.setCharset(charset);
    }

    @Override
    public boolean isShowNonprintables() {
        return activeFile.isShowNonprintables();
    }

    @Override
    public void setShowNonprintables(boolean show) {
        activeFile.setShowNonprintables(show);
    }

    @Override
    public boolean isShowValuesPanel() {
        return activeFile.isShowValuesPanel();
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean changeLineWrap() {
        return activeFile.changeLineWrap();
    }

    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Override
    public ExtCodeArea getCodeArea() {
        return activeFile.getCodeArea();
    }

    @Override
    public void printFile() {
        activeFile.printFile();
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {
        if (editorProvider instanceof BinEdFileHandler) {
            BinEdFileHandler newActiveFile = (BinEdFileHandler) editorProvider;
            activeFile = newActiveFile;
            // TODO newActiveFile.notifyListeners();
            notifyUndoChanged();
            notifyClipboardStateChanged();
        }
    }

    @Override
    public void closeFile() {
        closeFile(activeFile);
    }

    @Override
    public void closeFile(FileHandlerApi closedFile) {
        files.remove((BinEdFileHandler) closedFile);
        editorViewHandling.removeEditorView((EditorProvider) closedFile);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
    }

    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        return new BinaryDataUndoHandler() {
            @Override
            public boolean canRedo() {
                return activeFile.getBinaryUndoHandler().canRedo();
            }

            @Override
            public boolean canUndo() {
                return activeFile.getBinaryUndoHandler().canUndo();
            }

            @Override
            public void clear() {
                activeFile.getBinaryUndoHandler().clear();
            }

            @Override
            public void doSync() throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().doSync();
            }

            @Override
            public void execute(BinaryDataCommand cmnd) throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().execute(cmnd);
            }

            @Override
            public void addCommand(BinaryDataCommand cmnd) {
                activeFile.getBinaryUndoHandler().addCommand(cmnd);
            }

            @Override
            public List<BinaryDataCommand> getCommandList() {
                return activeFile.getBinaryUndoHandler().getCommandList();
            }

            @Override
            public long getCommandPosition() {
                return activeFile.getBinaryUndoHandler().getCommandPosition();
            }

            @Override
            public long getMaximumUndo() {
                return activeFile.getBinaryUndoHandler().getMaximumUndo();
            }

            @Override
            public long getSyncPoint() {
                return activeFile.getBinaryUndoHandler().getSyncPoint();
            }

            @Override
            public long getUndoMaximumSize() {
                return activeFile.getBinaryUndoHandler().getUndoMaximumSize();
            }

            @Override
            public long getUsedSize() {
                return activeFile.getBinaryUndoHandler().getUsedSize();
            }

            @Override
            public void performRedo() throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().performRedo();
            }

            @Override
            public void performRedo(int i) throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().performRedo(i);
            }

            @Override
            public void performUndo() throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().performUndo();
            }

            @Override
            public void performUndo(int i) throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().performUndo(i);
            }

            @Override
            public void setCommandPosition(long l) throws BinaryDataOperationException {
                activeFile.getBinaryUndoHandler().setCommandPosition(l);
            }

            @Override
            public void setSyncPoint(long l) {
                activeFile.getBinaryUndoHandler().setSyncPoint(l);
            }

            @Override
            public void setSyncPoint() {
                activeFile.getBinaryUndoHandler().setSyncPoint();
            }

            @Override
            public void addUndoUpdateListener(BinaryDataUndoUpdateListener xl) {
                undoListeners.add(xl);
            }

            @Override
            public void removeUndoUpdateListener(BinaryDataUndoUpdateListener xl) {
                undoListeners.remove(xl);
            }
        };
    }

    private void notifyUndoChanged() {
        undoListeners.forEach((listener) -> {
            listener.undoCommandPositionChanged();
        });
        if (editorViewHandling != null) {
            editorViewHandling.updateEditorView(activeFile);
        }
    }

    private void notifyClipboardStateChanged() {
        if (clipboardUpdateListener != null) {
            clipboardUpdateListener.stateChanged();
        }
    }
*/
}
