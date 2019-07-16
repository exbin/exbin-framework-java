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
package org.exbin.framework.bined.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.exbin.bined.CaretMovedListener;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationModeChangedListener;
import org.exbin.bined.EditationOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditationModeCapable;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.delta.FileDataSource;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.handler.EncodingStatusHandler;
import org.exbin.framework.editor.text.TextCharsetApi;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.gui.menu.api.ClipboardActionsUpdateListener;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.xbup.core.type.XBData;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.editor.text.preferences.TextEncodingParameters;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.bined.service.impl.BinarySearchServiceImpl;

/**
 * Binary editor panel.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
public class BinaryPanel extends javax.swing.JPanel implements BinaryEditorProvider, ClipboardActionsHandler, TextCharsetApi, TextFontApi {

    private int id = 0;
    private SegmentsRepository segmentsRepository;
    private ExtCodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private URI fileUri = null;
    private Color foundTextBackgroundColor;
    private Font defaultFont;
    private ExtendedCodeAreaColorProfile defaultColors;
    private BinaryStatusApi binaryStatus = null;
    private TextEncodingStatusApi encodingStatus = null;
    private boolean memoryMode = false;

    private BinarySearchPanel binarySearchPanel;
    private boolean binarySearchPanelVisible = false;
    private ValuesPanel valuesPanel;
    private JScrollPane valuesPanelScrollPane;
    private boolean valuesPanelVisible = false;
    private Action goToPositionAction = null;
    private Action copyAsCode = null;
    private Action pasteFromCode = null;
    private EncodingStatusHandler encodingsHandler;
    private long documentOriginalSize;

    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private ReleaseFileMethod releaseFileMethod = null;
    private XBApplication application;

    public BinaryPanel() {
        initComponents();
        init();
    }

    public BinaryPanel(int id) {
        this();
        this.id = id;
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        setNewData();
        codeArea.setHandleClipboard(false);
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.addSelectionChangedListener((SelectionRange selection) -> {
            updateClipboardActionsStatus();
        });

        undoHandler = new CodeAreaUndoHandler(codeArea);
        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
                updateCurrentDocumentSize();
            }

            @Override
            public void undoCommandAdded(BinaryDataCommand cmnd) {
                updateCurrentDocumentSize();
            }
        });

        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeArea.addDataChangedListener(() -> {
            if (binarySearchPanelVisible) {
                binarySearchPanel.dataChanged();
            }
            updateCurrentDocumentSize();
        });
        // TODO use listener in code area component instead
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });

        add(codeArea);
        foundTextBackgroundColor = Color.YELLOW;
        codeArea.setCharset(Charset.forName(TextEncodingParameters.ENCODING_UTF8));
        defaultFont = codeArea.getFont();

        defaultColors = getCurrentColors();

        addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (propertyChangeListener != null) {
                propertyChangeListener.propertyChange(evt);
            }
        });

        binarySearchPanel = new BinarySearchPanel();
        binarySearchPanel.setBinarySearchService(new BinarySearchServiceImpl());
        binarySearchPanel.setClosePanelListener(this::hideSearchPanel);

        valuesPanel = new ValuesPanel();
        valuesPanel.setCodeArea(codeArea, undoHandler);
        valuesPanelScrollPane = new JScrollPane(valuesPanel);
        valuesPanelScrollPane.setBorder(null);
        showValuesPanel();
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        binarySearchPanel.setApplication(application);
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void showSearchPanel(boolean replace) {
        if (!binarySearchPanelVisible) {
            add(binarySearchPanel, BorderLayout.SOUTH);
            revalidate();
            binarySearchPanelVisible = true;
            binarySearchPanel.requestSearchFocus();
        }
        binarySearchPanel.switchReplaceMode(replace);
    }

    public void hideSearchPanel() {
        if (binarySearchPanelVisible) {
            binarySearchPanel.cancelSearch();
            binarySearchPanel.clearSearch();
            BinaryPanel.this.remove(binarySearchPanel);
            BinaryPanel.this.revalidate();
            binarySearchPanelVisible = false;
        }
    }

    @Override
    public void showValuesPanel() {
        if (!valuesPanelVisible) {
            add(valuesPanelScrollPane, BorderLayout.EAST);
            revalidate();
            valuesPanelVisible = true;
            valuesPanel.enableUpdate();
        }
    }

    @Override
    public void hideValuesPanel() {
        if (valuesPanelVisible) {
            valuesPanel.disableUpdate();
            BinaryPanel.this.remove(valuesPanelScrollPane);
            BinaryPanel.this.revalidate();
            valuesPanelVisible = false;
        }
    }

    @Override
    public boolean isValuesPanelVisible() {
        return valuesPanelVisible;
    }

    @Override
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    @Override
    public boolean changeLineWrap() {
        ((RowWrappingCapable) codeArea).setRowWrapping(((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING ? RowWrappingCapable.RowWrappingMode.NO_WRAPPING : RowWrappingCapable.RowWrappingMode.WRAPPING);
        return ((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING;
    }

    @Override
    public boolean changeShowNonprintables() {
        codeArea.setShowUnprintables(!codeArea.isShowUnprintables());
        return codeArea.isShowUnprintables();
    }

    @Override
    public boolean isWordWrapMode() {
        return false;
        // TODO codeArea.isWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        // TODO
//        if (codeArea.isWrapMode() != mode) {
//            changeLineWrap();
//        }
    }

    public void findAgain() {
        // TODO hexSearchPanel.f
    }

    private void updateClipboardActionsStatus() {
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }

        if (copyAsCode != null) {
            copyAsCode.setEnabled(codeArea.hasSelection());
        }
        if (pasteFromCode != null) {
            pasteFromCode.setEnabled(codeArea.canPaste());
        }
    }

    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return (ExtendedCodeAreaColorProfile) codeArea.getColorsProfile();
    }

    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return defaultColors;
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        codeArea.setColorsProfile(colorsProfile);
    }

    public void goToPosition(long position) {
        codeArea.setCaretPosition(position);
        codeArea.revealCursor();
    }

    @Override
    public void performCopy() {
        codeArea.copy();
    }

    public void performCopyAsCode() {
        codeArea.copyAsCode();
    }

    @Override
    public void performCut() {
        codeArea.cut();
    }

    @Override
    public void performDelete() {
        codeArea.delete();
    }

    @Override
    public void performPaste() {
        codeArea.paste();
    }

    public void performPasteFromCode() {
        try {
            codeArea.pasteFromCode();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void performSelectAll() {
        codeArea.selectAll();
    }

    @Override
    public boolean isSelection() {
        return codeArea.hasSelection();
    }

    public int getId() {
        return id;
    }

    @Override
    public void printFile() {
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
                Logger.getLogger(BinaryPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setCurrentFont(Font font) {
        codeArea.setFont(font);
    }

    @Override
    public Font getCurrentFont() {
        return codeArea.getFont();
    }

    public Color getFoundTextBackgroundColor() {
        return foundTextBackgroundColor;
    }

    public void setFoundTextBackgroundColor(Color color) {
        foundTextBackgroundColor = color;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new BinaryPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    @Override
    public CodeAreaUndoHandler getBinaryUndoHandler() {
        return undoHandler;
    }

    public void setBinaryUndoHandler(CodeAreaUndoHandler binaryUndoHandler) {
        this.undoHandler = binaryUndoHandler;
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        if (!file.isFile()) {
            JOptionPane.showOptionDialog(this,
                    "File not found",
                    "Unable to load file",
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, null, null);
            return;
        }

        try {
            BinaryData oldData = codeArea.getContentData();
            if (memoryMode) {
                FileDataSource openFileSource = segmentsRepository.openFileSource(file);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                codeArea.setContentData(document);
                this.fileUri = fileUri;
                oldData.dispose();
            } else {
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    BinaryData data = codeArea.getContentData();
                    if (!(data instanceof XBData)) {
                        data = new XBData();
                        oldData.dispose();
                    }
                    ((EditableBinaryData) data).loadFromStream(fileStream);
                    codeArea.setContentData(data);
                    this.fileUri = fileUri;
                }
            }

            documentOriginalSize = codeArea.getDataSize();
            updateCurrentDocumentSize();
            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(BinaryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.clear();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            if (codeArea.getContentData() instanceof DeltaDocument) {
                // TODO freeze window / replace with progress bar
                DeltaDocument document = (DeltaDocument) codeArea.getContentData();
                if (document == null || !file.equals(document.getFileSource().getFile())) {
                    FileDataSource fileSource = segmentsRepository.openFileSource(file);
                    document.setFileSource(fileSource);
                }
                segmentsRepository.saveDocument(document);
                this.fileUri = fileUri;
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    codeArea.getContentData().saveToStream(outputStream);
                    this.fileUri = fileUri;
                }
            }
            documentOriginalSize = codeArea.getDataSize();
            updateCurrentDocumentSize();
            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(BinaryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.setSyncPoint();
    }

    @Override
    public URI getFileUri() {
        return fileUri;
    }

    @Override
    public void newFile() {
        if (codeArea.getContentData() instanceof DeltaDocument) {
            segmentsRepository.dropDocument(Objects.requireNonNull((DeltaDocument) codeArea.getContentData()));
        }
        setNewData();
        fileUri = null;
        documentOriginalSize = codeArea.getDataSize();
        codeArea.notifyDataChanged();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();
        undoHandler.clear();
        codeArea.repaint();
    }

    @Override
    public String getFileName() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            return lastSegment < 0 ? path : path.substring(lastSegment + 1);
        }

        return null;
    }

    @Override
    public FileType getFileType() {
        return null;
    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
    }

    public void loadFromStream(InputStream stream) throws IOException {
        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.loadFromStream(stream);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.clear();
        data.insert(0, stream, dataSize);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        BinaryData data = Objects.requireNonNull((BinaryData) codeArea.getContentData());
        data.saveToStream(stream);
    }

    public void attachCaretListener(CaretMovedListener listener) {
        codeArea.addCaretMovedListener(listener);
    }

    public void attachEditationModeChangedListener(EditationModeChangedListener listener) {
        codeArea.addEditationModeChangedListener(listener);
    }

    @Override
    public Charset getCharset() {
        return codeArea.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        codeArea.setCharset(charset);
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Override
    public void setFileType(FileType fileType) {
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + frameTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + frameTitle;
        }

        return frameTitle;
    }

    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
        this.charsetChangeListener = charsetChangeListener;
    }

    private void changeCharset(Charset charset) {
        codeArea.setCharset(charset);
        if (charsetChangeListener != null) {
            charsetChangeListener.charsetChanged();
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        attachCaretListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });

        attachEditationModeChangedListener((EditationMode mode, EditationOperation operation) -> {
            binaryStatus.setEditationMode(mode, operation);
        });
        binaryStatus.setEditationMode(codeArea.getEditationMode(), codeArea.getActiveOperation());

        binaryStatus.setControlHandler(new BinaryStatusApi.StatusControlHandler() {
            @Override
            public void changeEditationOperation(EditationOperation editationOperation) {
                codeArea.setEditationOperation(editationOperation);
            }

            @Override
            public void changeCursorPosition() {
                if (goToPositionAction != null) {
                    goToPositionAction.actionPerformed(null);
                }
            }

            @Override
            public void cycleEncodings() {
                if (encodingsHandler != null) {
                    encodingsHandler.cycleEncodings();
                }
            }

            @Override
            public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
                if (encodingsHandler != null) {
                    encodingsHandler.popupEncodingsMenu(mouseEvent);
                }
            }

            @Override
            public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
                boolean newDeltaMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE;
                switchMemoryMode(newDeltaMode);
            }
        });

        if (memoryMode) {
            setNewData();
        }
        updateCurrentMemoryMode();
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        setCharsetChangeListener(() -> {
            encodingStatus.setEncoding(getCharset().name());
        });
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        updateClipboardActionsStatus();
    }

    @Override
    public boolean isEditable() {
        return codeArea.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return codeArea.canPaste();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus != null) {
            long dataSize = codeArea.getDataSize();
            binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
        }
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus != null) {
            CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
            binaryStatus.setCursorPosition(caretPosition);
        }
    }

    public void setMemoryMode(boolean memoryMode) {
        this.memoryMode = memoryMode;
    }

    private void updateCurrentMemoryMode() {
        BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (((EditationModeCapable) codeArea).getEditationMode() == EditationMode.READ_ONLY) {
            newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        if (binaryStatus != null) {
            binaryStatus.setMemoryMode(newMemoryMode);
        }
    }

    public void setGoToPositionAction(Action goToPositionAction) {
        this.goToPositionAction = goToPositionAction;
    }

    public void setEncodingsHandler(EncodingStatusHandler encodingsHandler) {
        this.encodingsHandler = encodingsHandler;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        binarySearchPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
    }

    public void setCopyAsCode(Action copyAsCode) {
        this.copyAsCode = copyAsCode;
    }

    public void setPasteFromCode(Action pasteFromCode) {
        this.pasteFromCode = pasteFromCode;
    }

    public void setReleaseFileMethod(ReleaseFileMethod releaseFileMethod) {
        this.releaseFileMethod = releaseFileMethod;
    }

    @Override
    public BinaryPanel getDocument() {
        return this;
    }

    /**
     * Helper method for notifying listeners, that BinaryPanel tab was switched.
     */
    public void notifyListeners() {
        if (charsetChangeListener != null) {
            charsetChangeListener.charsetChanged();
        }
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }

        if (binaryStatus != null) {
            updateCurrentDocumentSize();
            updateCurrentCaretPosition();
        }

        encodingStatus.setEncoding(codeArea.getCharset().name());
    }

    @Override
    public void setModificationListener(final EditorModificationListener editorModificationListener) {
        codeArea.addDataChangedListener(editorModificationListener::modified);
    }

    private void setNewData() {
        if (memoryMode) {
            codeArea.setContentData(segmentsRepository.createDocument());
        } else {
            codeArea.setContentData(new XBData());
        }
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        switchMemoryMode(fileHandlingMode == FileHandlingMode.DELTA);
    }

    private void switchMemoryMode(boolean newMemoryMode) {
        if (newMemoryMode != memoryMode) {
            // Switch memory mode
            if (fileUri != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFileMethod != null && releaseFileMethod.execute()) {
                        memoryMode = newMemoryMode;
                        loadFromFile(fileUri, null);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                    }
                } else {
                    memoryMode = newMemoryMode;
                    loadFromFile(fileUri, null);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                BinaryData oldData = Objects.requireNonNull(codeArea.getContentData());
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    XBData data = new XBData();
                    data.insert(0, codeArea.getContentData());
                    codeArea.setContentData(data);
                } else {
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    codeArea.setContentData(document);
                }
                undoHandler.clear();
                oldData.dispose();
                codeArea.notifyDataChanged();
                updateCurrentMemoryMode();
                memoryMode = newMemoryMode;
            }
            memoryMode = newMemoryMode;
        }
    }

    public static interface CharsetChangeListener {

        void charsetChanged();
    }

    public static interface ReleaseFileMethod {

        boolean execute();
    }
}
