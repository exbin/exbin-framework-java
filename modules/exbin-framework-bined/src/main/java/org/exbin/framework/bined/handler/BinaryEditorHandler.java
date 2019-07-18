/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.bined.handler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.panel.BinaryPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.docking.api.EditorViewHandling;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;

/**
 * Hexadecimal editor provider.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
public class BinaryEditorHandler implements BinaryEditorProvider, MultiEditorProvider, ClipboardActionsHandler {

    private BinaryPanelInit binaryPanelInit = null;
    private final List<BinaryPanel> panels = new ArrayList<>();
    private EditorViewHandling editorViewHandling = null;
    private SegmentsRepository segmentsRepository;
    private BinaryPanel activePanel = null;
    private int lastIndex = 0;
    private BinaryStatusApi binaryStatus = null;
    private TextEncodingStatusApi encodingStatus;
    private EditorModificationListener editorModificationListener = null;
    private final EditorModificationListener multiModificationListener;
    private final List<BinaryDataUndoUpdateListener> undoListeners = new ArrayList<>();
    private final BinaryDataUndoUpdateListener multiUndoUpdateListener;
    private ClipboardActionsUpdateListener clipboardUpdateListener = null;
    private final ClipboardActionsUpdateListener multiClipboardUpdateListener;

    public BinaryEditorHandler() {
        multiModificationListener = () -> {
            if (editorModificationListener != null) {
                editorModificationListener.modified();
            }
            if (editorViewHandling != null) {
                editorViewHandling.updateEditorView(activePanel);
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
    public JPanel getPanel() {
        return activePanel.getPanel();
    }

    @Override
    public void setPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        activePanel.setPropertyChangeListener((PropertyChangeEvent evt) -> {
            editorViewHandling.addEditorView(activePanel);
            propertyChangeListener.propertyChange(evt);
        });
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        return activePanel.getWindowTitle(frameTitle);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        BinaryPanel panel = createNewPanel();
        panel.newFile();
        panel.loadFromFile(fileUri, fileType);
        editorViewHandling.updateEditorView(panel);
        activePanel = panel;
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        activePanel.saveToFile(fileUri, fileType);
        editorViewHandling.updateEditorView(activePanel);
    }

    @Override
    public URI getFileUri() {
        return activePanel.getFileUri();
    }

    @Override
    public String getFileName() {
        return activePanel.getName();
    }

    @Override
    public FileType getFileType() {
        return activePanel.getFileType();
    }

    @Override
    public void setFileType(FileType fileType) {
        activePanel.setFileType(fileType);
    }

    @Override
    public void newFile() {
        BinaryPanel panel = createNewPanel();
        panel.newFile();
        activePanel = panel;
    }

    @Override
    public boolean isModified() {
        return activePanel.isModified();
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        if (!panels.isEmpty()) {
            panels.forEach((panel) -> {
                panel.registerBinaryStatus(binaryStatusApi);
            });
        }
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        if (!panels.isEmpty()) {
            panels.forEach((panel) -> {
                panel.registerEncodingStatus(encodingStatusApi);
            });
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    private synchronized BinaryPanel createNewPanel() {
        BinaryPanel panel = new BinaryPanel(lastIndex);
        panel.setSegmentsRepository(segmentsRepository);
        lastIndex++;
        panels.add(panel);
        if (binaryPanelInit != null) {
            binaryPanelInit.init(panel);
        }
        if (binaryStatus != null) {
            panel.registerBinaryStatus(binaryStatus);
            panel.registerEncodingStatus(encodingStatus);
        }
        editorViewHandling.addEditorView(panel);
        panel.setModificationListener(multiModificationListener);
        panel.getBinaryUndoHandler().addUndoUpdateListener(multiUndoUpdateListener);
        panel.setUpdateListener(multiClipboardUpdateListener);

        return panel;
    }

    public void init() {
        activePanel = createNewPanel();
        activePanel.newFile();
    }

    public BinaryPanelInit getBinaryPanelInit() {
        return binaryPanelInit;
    }

    public void setBinaryPanelInit(BinaryPanelInit binaryPanelInit) {
        this.binaryPanelInit = binaryPanelInit;
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        activePanel.setFileHandlingMode(fileHandlingMode);
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
        return activePanel.getCurrentColors();
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return activePanel.getDefaultColors();
    }

    @Nonnull
    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        activePanel.setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return ((RowWrappingCapable) activePanel.getCodeArea()).getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING;
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        ((RowWrappingCapable) activePanel.getCodeArea()).setRowWrapping(mode ? RowWrappingCapable.RowWrappingMode.WRAPPING : RowWrappingCapable.RowWrappingMode.NO_WRAPPING);
    }

    @Override
    public Charset getCharset() {
        return activePanel.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        activePanel.setCharset(charset);
    }

    @Override
    public boolean changeShowNonprintables() {
        return activePanel.changeShowNonprintables();
    }

    @Override
    public boolean changeLineWrap() {
        return activePanel.changeLineWrap();
    }

    @Override
    public void showValuesPanel() {
        activePanel.showValuesPanel();
    }

    @Override
    public void hideValuesPanel() {
        activePanel.hideValuesPanel();
    }

    @Override
    public boolean isValuesPanelVisible() {
        return activePanel.isValuesPanelVisible();
    }

    @Override
    public BinaryPanel getDocument() {
        return activePanel;
    }

    @Override
    public ExtCodeArea getCodeArea() {
        return activePanel.getCodeArea();
    }

    @Override
    public void printFile() {
        activePanel.printFile();
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {
        if (editorProvider instanceof BinaryPanel) {
            BinaryPanel binaryPanel = (BinaryPanel) editorProvider;
            activePanel = binaryPanel;
            binaryPanel.notifyListeners();
            notifyUndoChanged();
            notifyClipboardStateChanged();
        }
    }

    @Override
    public void closeFile() {
        closeFile(activePanel);
    }

    @Override
    public void closeFile(FileHandlerApi panel) {
        panels.remove((BinaryPanel) panel);
        editorViewHandling.removeEditorView((EditorProvider) panel);
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
                return activePanel.getBinaryUndoHandler().canRedo();
            }

            @Override
            public boolean canUndo() {
                return activePanel.getBinaryUndoHandler().canUndo();
            }

            @Override
            public void clear() {
                activePanel.getBinaryUndoHandler().clear();
            }

            @Override
            public void doSync() throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().doSync();
            }

            @Override
            public void execute(BinaryDataCommand cmnd) throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().execute(cmnd);
            }

            @Override
            public void addCommand(BinaryDataCommand cmnd) {
                activePanel.getBinaryUndoHandler().addCommand(cmnd);
            }

            @Override
            public List<BinaryDataCommand> getCommandList() {
                return activePanel.getBinaryUndoHandler().getCommandList();
            }

            @Override
            public long getCommandPosition() {
                return activePanel.getBinaryUndoHandler().getCommandPosition();
            }

            @Override
            public long getMaximumUndo() {
                return activePanel.getBinaryUndoHandler().getMaximumUndo();
            }

            @Override
            public long getSyncPoint() {
                return activePanel.getBinaryUndoHandler().getSyncPoint();
            }

            @Override
            public long getUndoMaximumSize() {
                return activePanel.getBinaryUndoHandler().getUndoMaximumSize();
            }

            @Override
            public long getUsedSize() {
                return activePanel.getBinaryUndoHandler().getUsedSize();
            }

            @Override
            public void performRedo() throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().performRedo();
            }

            @Override
            public void performRedo(int i) throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().performRedo(i);
            }

            @Override
            public void performUndo() throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().performUndo();
            }

            @Override
            public void performUndo(int i) throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().performUndo(i);
            }

            @Override
            public void setCommandPosition(long l) throws BinaryDataOperationException {
                activePanel.getBinaryUndoHandler().setCommandPosition(l);
            }

            @Override
            public void setSyncPoint(long l) {
                activePanel.getBinaryUndoHandler().setSyncPoint(l);
            }

            @Override
            public void setSyncPoint() {
                activePanel.getBinaryUndoHandler().setSyncPoint();
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
            editorViewHandling.updateEditorView(activePanel);
        }
    }

    private void notifyClipboardStateChanged() {
        if (clipboardUpdateListener != null) {
            clipboardUpdateListener.stateChanged();
        }
    }

    @Override
    public void performCut() {
        activePanel.performCut();
    }

    @Override
    public void performCopy() {
        activePanel.performCopy();
    }

    @Override
    public void performPaste() {
        activePanel.performPaste();
    }

    @Override
    public void performDelete() {
        activePanel.performDelete();
    }

    @Override
    public void performSelectAll() {
        activePanel.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return activePanel.isSelection();
    }

    @Override
    public boolean isEditable() {
        return activePanel.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return activePanel.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return activePanel.canPaste();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        this.clipboardUpdateListener = updateListener;
    }

    /**
     * Method for initialization of new hexadecimal panel.
     */
    public static interface BinaryPanelInit {

        void init(BinaryPanel panel);
    }
}
