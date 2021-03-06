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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.type.XBData;

/**
 * File handling mode.
 *
 * @version 0.2.0 2019/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileHandler implements BinaryEditorProvider, BinEdComponentFileApi, ClipboardActionsHandler, TextFontApi {

    private SegmentsRepository segmentsRepository;

    private final BinEdComponentPanel componentPanel;
    private CodeAreaUndoHandler undoHandler;
    private int id = 0;
    private URI fileUri = null;

    public BinEdFileHandler() {
        componentPanel = new BinEdComponentPanel();
        undoHandler = new CodeAreaUndoHandler(componentPanel.getCodeArea());
        componentPanel.setUndoHandler(undoHandler);
        init();
    }

    private void init() {
        componentPanel.setFileApi(this);
        componentPanel.setContentData(new ByteArrayData());
    }

    public BinEdFileHandler(int id) {
        this();
        this.id = id;
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        if (!file.isFile()) {
            JOptionPane.showOptionDialog(this.getComponentPanel(),
                    "File not found",
                    "Unable to load file",
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, null, null);
            return;
        }

        try {
            BinaryData oldData = Objects.requireNonNull(componentPanel.getContentData());
            FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();
            if (fileHandlingMode == FileHandlingMode.DELTA) {
                FileDataSource openFileSource = segmentsRepository.openFileSource(file);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                componentPanel.setContentData(document);
                this.fileUri = fileUri;
                oldData.dispose();
            } else {
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    BinaryData data = componentPanel.getContentData();
                    if (!(data instanceof XBData)) {
                        data = new XBData();
                        oldData.dispose();
                    }
                    ((EditableBinaryData) data).loadFromStream(fileStream);
                    componentPanel.setContentData(data);
                    this.fileUri = fileUri;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.clear();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            BinaryData contentData = componentPanel.getContentData();
            if (contentData == null) {
                newFile();
                contentData = componentPanel.getContentData();
            }
            if (contentData instanceof DeltaDocument) {
                // TODO freeze window / replace with progress bar
                DeltaDocument document = (DeltaDocument) contentData;
                FileDataSource fileSource = document.getFileSource();
                if (fileSource == null || !file.equals(fileSource.getFile())) {
                    fileSource = segmentsRepository.openFileSource(file);
                    document.setFileSource(fileSource);
                }
                segmentsRepository.saveDocument(document);
                this.fileUri = fileUri;
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    Objects.requireNonNull(contentData).saveToStream(outputStream);
                    this.fileUri = fileUri;
                }
            }
            // TODO
//            documentOriginalSize = codeArea.getDataSize();
//            updateCurrentDocumentSize();
//            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.setSyncPoint();
    }

    public void loadFromStream(InputStream stream) throws IOException {
        BinaryData contentData = componentPanel.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
            // TODO: stream to binary data
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.loadFromStream(stream);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        BinaryData contentData = componentPanel.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.clear();
        data.insert(0, stream, dataSize);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        BinaryData data = Objects.requireNonNull((BinaryData) componentPanel.getContentData());
        data.saveToStream(stream);
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Override
    public void newFile() {
        closeData();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            segmentsRepository.dropDocument(Objects.requireNonNull((DeltaDocument) codeArea.getContentData()));
        }
        setNewData();
        fileUri = null;
        undoHandler.clear();
    }

    @Override
    public int getId() {
        return id;
    }

    @Nonnull
    @Override
    public Optional<String> getFileName() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            return Optional.of(lastSegment < 0 ? path : path.substring(lastSegment + 1));
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.empty();
    }

    @Override
    public void printFile() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
//                PrintJob myJob = imageArea.getToolkit().getPrintJob(null, fileName, null);
//                if (myJob != null) {
                job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
                    codeArea.print(graphics);
                    if (pageIndex == 0) {
                        return Printable.PAGE_EXISTS;
                    }
                    return Printable.NO_SUCH_PAGE;
                });
                job.print();
//                }
            } catch (PrinterException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setFileType(FileType fileType) {
    }

    @Override
    public String getWindowTitle(String windowTitle) {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + windowTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + windowTitle;
        }

        return windowTitle;
    }

    public void saveFile() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            File file = new File(fileUri);
            try (OutputStream stream = new FileOutputStream(file)) {
                BinaryData contentData = codeArea.getContentData();
                if (contentData != null) {
                    contentData.saveToStream(stream);
                }
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void closeData() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        componentPanel.setContentData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            if (fileSource != null) {
                segmentsRepository.detachFileSource(fileSource);
                segmentsRepository.closeFileSource(fileSource);
            }
        } else {
            if (data != null) {
                data.dispose();
            }
        }
    }

    @Override
    public void saveDocument() {
        if (fileUri == null) {
            return;
        }

        saveFile();
    }

    @Override
    public void switchFileHandlingMode(FileHandlingMode handlingMode) {
        FileHandlingMode oldFileHandlingMode = componentPanel.getFileHandlingMode();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        if (handlingMode != oldFileHandlingMode) {
            // Switch memory mode
            if (fileUri != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        loadFromFile(fileUri, null);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                        componentPanel.setFileHandlingMode(handlingMode);
                    }
                } else {
                    componentPanel.setFileHandlingMode(handlingMode);
                    loadFromFile(fileUri, null);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                BinaryData oldData = Objects.requireNonNull(codeArea.getContentData());
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    componentPanel.setContentData(document);
                } else {
                    XBData data = new XBData();
                    data.insert(0, codeArea.getContentData());
                    componentPanel.setContentData(data);
                }

                undoHandler.clear();
                oldData.dispose();
                componentPanel.setFileHandlingMode(handlingMode);
            }
        }
    }

    private void setNewData() {
        FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            componentPanel.setContentData(segmentsRepository.createDocument());
        } else {
            componentPanel.setContentData(new XBData());
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void setModifiedChangeListener(BinEdComponentPanel.ModifiedStateListener modifiedChangeListener) {
        componentPanel.setModifiedChangeListener(modifiedChangeListener);
    }

    public void requestFocus() {
        componentPanel.getCodeArea().requestFocus();
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        componentPanel.registerBinaryStatus(binaryStatus);
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        componentPanel.registerEncodingStatus(encodingStatus);
    }

    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return componentPanel.getCurrentColors();
    }

    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return componentPanel.getDefaultColors();
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        componentPanel.setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return componentPanel.isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        componentPanel.setWordWrapMode(mode);
    }

    @Override
    public Charset getCharset() {
        return componentPanel.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        componentPanel.setCharset(charset);
    }

    @Override
    public boolean isShowNonprintables() {
        return componentPanel.isShowNonprintables();
    }

    @Override
    public void setShowNonprintables(boolean show) {
        componentPanel.setShowNonprintables(show);
    }

    @Override
    public boolean isShowValuesPanel() {
        return componentPanel.isShowValuesPanel();
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        componentPanel.setShowValuesPanel(show);
    }

    @Override
    public boolean changeLineWrap() {
        return componentPanel.changeLineWrap();
    }

    @Override
    public BinEdComponentPanel getComponentPanel() {
        return componentPanel;
    }

    public CodeAreaUndoHandler getUndoHandler() {
        return undoHandler;
    }

    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        return undoHandler;
    }

    @Override
    public ExtCodeArea getCodeArea() {
        return componentPanel.getCodeArea();
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        componentPanel.setFileHandlingMode(fileHandlingMode);
    }

    @Override
    public JPanel getPanel() {
        return componentPanel;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        componentPanel.setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener modificationListener) {
        componentPanel.setModificationListener(modificationListener);
    }

    @Override
    public boolean isModified() {
        return componentPanel.isModified();
    }

    public boolean releaseFile() {
        return componentPanel.releaseFile();
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    @Override
    public void performCut() {
        componentPanel.performCut();
    }

    @Override
    public void performCopy() {
        componentPanel.performCopy();
    }

    @Override
    public void performPaste() {
        componentPanel.performPaste();
    }

    @Override
    public void performDelete() {
        componentPanel.performDelete();
    }

    @Override
    public void performSelectAll() {
        componentPanel.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return componentPanel.isSelection();
    }

    @Override
    public boolean isEditable() {
        return componentPanel.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return componentPanel.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return componentPanel.canPaste();
    }

    @Override
    public boolean canDelete() {
        return componentPanel.canDelete();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        componentPanel.setUpdateListener(updateListener);
    }

    @Override
    public Font getCurrentFont() {
        return componentPanel.getCurrentFont();
    }

    @Override
    public Font getDefaultFont() {
        return componentPanel.getDefaultFont();
    }

    @Override
    public void setCurrentFont(Font font) {
        componentPanel.setCurrentFont(font);
    }
}
