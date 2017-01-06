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
package org.exbin.framework.deltahex.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
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
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.exbin.deltahex.CaretMovedListener;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.DataChangedListener;
import org.exbin.deltahex.EditationAllowed;
import org.exbin.deltahex.EditationMode;
import org.exbin.deltahex.EditationModeChangedListener;
import org.exbin.deltahex.Section;
import org.exbin.deltahex.SelectionChangedListener;
import org.exbin.deltahex.SelectionRange;
import org.exbin.deltahex.delta.DeltaDocument;
import org.exbin.deltahex.delta.FileDataSource;
import org.exbin.deltahex.delta.SegmentsRepository;
import org.exbin.deltahex.highlight.swing.HighlightCodeAreaPainter;
import org.exbin.deltahex.operation.BinaryDataCommand;
import org.exbin.deltahex.operation.swing.CodeAreaUndoHandler;
import org.exbin.deltahex.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.deltahex.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.CodeAreaPopupMenuHandler;
import org.exbin.framework.deltahex.EncodingStatusHandler;
import org.exbin.framework.deltahex.HexEditorProvider;
import org.exbin.framework.deltahex.HexStatusApi;
import org.exbin.framework.editor.text.TextCharsetApi;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.panel.TextEncodingPanel;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.gui.menu.api.ClipboardActionsUpdateListener;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.xbup.core.type.XBData;

/**
 * Hexadecimal editor panel.
 *
 * @version 0.2.0 2017/01/05
 * @author ExBin Project (http://exbin.org)
 */
public class HexPanel extends javax.swing.JPanel implements HexEditorProvider, ClipboardActionsHandler, TextCharsetApi, TextFontApi, HexSearchPanelApi {

    private int id = 0;
    private SegmentsRepository segmentsRepository;
    private CodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private URI fileUri = null;
    private Color foundTextBackgroundColor;
    private Font defaultFont;
    private Map<HexColorType, Color> defaultColors;
    private HexStatusApi hexStatus = null;
    private TextEncodingStatusApi encodingStatus = null;
    private boolean deltaMemoryMode = false;

    private HexSearchPanel hexSearchPanel;
    private boolean findTextPanelVisible = false;
    private Action goToLineAction = null;
    private Action copyAsCode = null;
    private Action pasteFromCode = null;
    private EncodingStatusHandler encodingStatusHandler;
    private long documentOriginalSize;

    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private ReleaseFileMethod releaseFileMethod = null;
    private XBApplication application;

    public HexPanel() {
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
        initComponents();
        init();
    }

    public HexPanel(int id) {
        this();
        this.id = id;
    }

    private void init() {
        codeArea = new CodeArea();
        codeArea.setPainter(new HighlightCodeAreaPainter(codeArea));
        setNewData();
        codeArea.setHandleClipboard(false);
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionRange selection) {
                updateClipboardActionsStatus();
            }
        });
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeArea.addDataChangedListener(new DataChangedListener() {
            @Override
            public void dataChanged() {
                if (hexSearchPanel.isVisible()) {
                    hexSearchPanel.dataChanged();
                }
                updateCurrentDocumentSize();
            }
        });
        // TODO use listener in code area component instead
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                updateClipboardActionsStatus();
            }
        });

        add(codeArea);
        foundTextBackgroundColor = Color.YELLOW;
        codeArea.setCharset(Charset.forName(TextEncodingPanel.ENCODING_UTF8));
        defaultFont = codeArea.getFont();

        defaultColors = getCurrentColors();

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (propertyChangeListener != null) {
                    propertyChangeListener.propertyChange(evt);
                }
            }
        });

        hexSearchPanel = new HexSearchPanel(this);
        hexSearchPanel.setClosePanelListener(new HexSearchPanel.ClosePanelListener() {
            @Override
            public void panelClosed() {
                hideSearchPanel();
            }
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        hexSearchPanel.setApplication(application);
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void showSearchPanel(boolean replace) {
        if (!findTextPanelVisible) {
            add(hexSearchPanel, BorderLayout.SOUTH);
            revalidate();
            findTextPanelVisible = true;
            hexSearchPanel.requestSearchFocus();
        }
        hexSearchPanel.switchReplaceMode(replace);
    }

    public void hideSearchPanel() {
        if (findTextPanelVisible) {
            hexSearchPanel.cancelSearch();
            hexSearchPanel.clearSearch();
            HexPanel.this.remove(hexSearchPanel);
            HexPanel.this.revalidate();
            findTextPanelVisible = false;
        }
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    @Override
    public boolean changeLineWrap() {
        codeArea.setWrapMode(!codeArea.isWrapMode());
        return codeArea.isWrapMode();
    }

    @Override
    public boolean changeShowNonprintables() {
        codeArea.setShowUnprintableCharacters(!codeArea.isShowUnprintableCharacters());
        return codeArea.isShowUnprintableCharacters();
    }

    @Override
    public boolean isWordWrapMode() {
        return codeArea.isWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        if (codeArea.isWrapMode() != mode) {
            changeLineWrap();
        }
    }

    public void findAgain() {
        // TODO hexSearchPanel.f
    }

    @Override
    public void performFind(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        hexSearchPanel.clearStatus();
        if (condition.isEmpty()) {
            painter.clearMatches();
            codeArea.repaint();
            return;
        }

        long position;
        if (searchParameters.isSearchFromCursor()) {
            position = codeArea.getCaretPosition().getDataPosition();
        } else {
            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    position = 0;
                    break;
                }
                case BACKWARD: {
                    position = codeArea.getDataSize() - 1;
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal search type " + searchParameters.getSearchDirection().name());
            }
        }
        searchParameters.setStartPosition(position);

        switch (condition.getSearchMode()) {
            case TEXT: {
                searchForText(searchParameters);
                break;
            }
            case BINARY: {
                searchForBinaryData(searchParameters);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected search mode " + condition.getSearchMode().name());
        }
    }

    @Override
    public void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters) {
        SearchCondition replaceCondition = replaceParameters.getCondition();
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        if (currentMatch != null) {
            EditableBinaryData editableData = ((EditableBinaryData) codeArea.getData());
            editableData.remove(currentMatch.getPosition(), currentMatch.getLength());
            if (replaceCondition.getSearchMode() == SearchCondition.SearchMode.BINARY) {
                editableData.insert(currentMatch.getPosition(), replaceCondition.getBinaryData());
            } else {
                editableData.insert(currentMatch.getPosition(), replaceCondition.getSearchText().getBytes(codeArea.getCharset()));
            }
            painter.getMatches().remove(currentMatch);
            codeArea.repaint();
        }
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

    /**
     * Performs search by binary data.
     */
    private void searchForBinaryData(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        long position = codeArea.getCaretPosition().getDataPosition();
        HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();

        if (currentMatch != null) {
            if (currentMatch.getPosition() == position) {
                position++;
            }
            painter.clearMatches();
        } else if (!searchParameters.isSearchFromCursor()) {
            position = 0;
        }

        BinaryData searchData = condition.getBinaryData();
        BinaryData data = codeArea.getData();

        List<HighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        long dataSize = data.getDataSize();
        while (position < dataSize - searchData.getDataSize()) {
            int matchLength = 0;
            while (matchLength < searchData.getDataSize()) {
                if (data.getByte(position + matchLength) != searchData.getByte(matchLength)) {
                    break;
                }
                matchLength++;
            }

            if (matchLength == searchData.getDataSize()) {
                HighlightCodeAreaPainter.SearchMatch match = new HighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(searchData.getDataSize());
                foundMatches.add(match);

                if (foundMatches.size() == 100 || !searchParameters.isMultipleMatches()) {
                    break;
                }
            }

            position++;
        }

        painter.setMatches(foundMatches);
        if (foundMatches.size() > 0) {
            painter.setCurrentMatchIndex(0);
            HighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(firstMatch.getPosition(), codeArea.getActiveSection());
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    /**
     * Performs search by text/characters.
     */
    private void searchForText(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();

        long position = searchParameters.getStartPosition();
        String findText;
        if (searchParameters.isMatchCase()) {
            findText = condition.getSearchText();
        } else {
            findText = condition.getSearchText().toLowerCase();
        }
        BinaryData data = codeArea.getData();

        List<HighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        Charset charset = codeArea.getCharset();
        CharsetEncoder encoder = charset.newEncoder();
        int maxBytesPerChar = (int) encoder.maxBytesPerChar();
        byte[] charData = new byte[maxBytesPerChar];
        long dataSize = data.getDataSize();
        while (position <= dataSize - findText.length()) {
            int matchCharLength = 0;
            int matchLength = 0;
            while (matchCharLength < findText.length()) {
                long searchPosition = position + matchLength;
                int bytesToUse = maxBytesPerChar;
                if (position + bytesToUse > dataSize) {
                    bytesToUse = (int) (dataSize - position);
                }
                data.copyToArray(searchPosition, charData, 0, bytesToUse);
                char singleChar = new String(charData, charset).charAt(0);
                String singleCharString = String.valueOf(singleChar);
                int characterLength = singleCharString.getBytes(charset).length;

                if (searchParameters.isMatchCase()) {
                    if (singleChar != findText.charAt(matchCharLength)) {
                        break;
                    }
                } else if (singleCharString.toLowerCase().charAt(0) != findText.charAt(matchCharLength)) {
                    break;
                }
                matchCharLength++;
                matchLength += characterLength;
            }

            if (matchCharLength == findText.length()) {
                HighlightCodeAreaPainter.SearchMatch match = new HighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(matchLength);
                foundMatches.add(match);

                if (foundMatches.size() == 100 || !searchParameters.isMultipleMatches()) {
                    break;
                }
            }

            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    position++;
                    break;
                }
                case BACKWARD: {
                    position--;
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal search type " + searchParameters.getSearchDirection().name());
            }
        }

        painter.setMatches(foundMatches);
        if (foundMatches.size() > 0) {
            painter.setCurrentMatchIndex(0);
            HighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(firstMatch.getPosition(), codeArea.getActiveSection());
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    @Override
    public Map<HexColorType, Color> getCurrentColors() {
        Map<HexColorType, Color> colors = new HashMap<>();
        for (HexColorType colorType : HexColorType.values()) {
            Color color = colorType.getColorFromCodeArea(codeArea);
            colors.put(colorType, color);
        }
        return colors;
    }

    @Override
    public Map<HexColorType, Color> getDefaultColors() {
        return defaultColors;
    }

    @Override
    public void setCurrentColors(Map<HexColorType, Color> colors) {
        for (Map.Entry<HexColorType, Color> entry : colors.entrySet()) {
            entry.getKey().setColorToCodeArea(codeArea, entry.getValue());
        }
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
                job.setPrintable(new Printable() {

                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        codeArea.print(graphics);
                        if (pageIndex == 0) {
                            return Printable.PAGE_EXISTS;
                        }
                        return Printable.NO_SUCH_PAGE;
                    }
                });
                job.print();
//                }
            } catch (PrinterException ex) {
                Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
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

        setInheritsPopupMenu(true);
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    @Override
    public CodeAreaUndoHandler getHexUndoHandler() {
        return undoHandler;
    }

    public void setHexUndoHandler(CodeAreaUndoHandler hexUndoHandler) {
        this.undoHandler = hexUndoHandler;
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
            BinaryData oldData = codeArea.getData();
            if (deltaMemoryMode) {
                FileDataSource openFileSource = segmentsRepository.openFileSource(file);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                codeArea.setData(document);
                this.fileUri = fileUri;
                oldData.dispose();
            } else {
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    BinaryData data = codeArea.getData();
                    if (!(data instanceof XBData)) {
                        data = new XBData();
                        oldData.dispose();
                    }
                    ((EditableBinaryData) data).loadFromStream(fileStream);
                    codeArea.setData(data);
                    this.fileUri = fileUri;
                }
            }

            documentOriginalSize = codeArea.getDataSize();
            updateCurrentDocumentSize();
            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.clear();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            if (codeArea.getData() instanceof DeltaDocument) {
                // TODO freeze window / replace with progress bar
                DeltaDocument document = (DeltaDocument) codeArea.getData();
                if (!document.getFileSource().getFile().equals(file)) {
                    FileDataSource fileSource = segmentsRepository.openFileSource(file);
                    document.setFileSource(fileSource);
                }
                segmentsRepository.saveDocument(document);
                this.fileUri = fileUri;
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    codeArea.getData().saveToStream(outputStream);
                    this.fileUri = fileUri;
                }
            }
            documentOriginalSize = codeArea.getDataSize();
            updateCurrentDocumentSize();
            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.setSyncPoint();
    }

    @Override
    public URI getFileUri() {
        return fileUri;
    }

    @Override
    public void newFile() {
        if (codeArea.getData() instanceof DeltaDocument) {
            segmentsRepository.dropDocument((DeltaDocument) codeArea.getData());
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
        ((EditableBinaryData) codeArea.getData()).loadFromStream(stream);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        ((EditableBinaryData) codeArea.getData()).clear();
        ((EditableBinaryData) codeArea.getData()).insert(0, stream, dataSize);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        codeArea.getData().saveToStream(stream);
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
    public void registerHexStatus(HexStatusApi hexStatusApi) {
        this.hexStatus = hexStatusApi;
        attachCaretListener(new CaretMovedListener() {
            @Override
            public void caretMoved(CaretPosition caretPosition, Section section) {
                String position = String.valueOf(caretPosition.getDataPosition());
                position += ":" + caretPosition.getCodeOffset();
                hexStatus.setCursorPosition(position);
            }
        });

        attachEditationModeChangedListener(new EditationModeChangedListener() {
            @Override
            public void editationModeChanged(EditationMode mode) {
                hexStatus.setEditationMode(mode);
            }
        });
        hexStatus.setEditationMode(codeArea.getEditationMode());

        hexStatus.setControlHandler(new HexStatusApi.StatusControlHandler() {
            @Override
            public void changeEditationMode(EditationMode editationMode) {
                codeArea.setEditationMode(editationMode);
            }

            @Override
            public void changeCursorPosition() {
                if (goToLineAction != null) {
                    goToLineAction.actionPerformed(null);
                }
            }

            @Override
            public void cycleEncodings() {
                if (encodingStatusHandler != null) {
                    encodingStatusHandler.cycleEncodings();
                }
            }

            @Override
            public void popupEncodingsMenu(MouseEvent mouseEvent) {
                if (encodingStatusHandler != null) {
                    encodingStatusHandler.popupEncodingsMenu(mouseEvent);
                }
            }

            @Override
            public void changeMemoryMode(HexStatusApi.MemoryMode memoryMode) {
                boolean newDeltaMode = memoryMode == HexStatusApi.MemoryMode.DELTA_MODE;
                if (newDeltaMode != deltaMemoryMode) {
                    // Switch memory mode
                    if (fileUri != null) {
                        // If document is connected to file, attempt to release first if modified and then simply reload
                        if (isModified()) {
                            if (releaseFileMethod != null && releaseFileMethod.execute()) {
                                deltaMemoryMode = newDeltaMode;
                                loadFromFile(fileUri, null);
                                codeArea.clearSelection();
                                codeArea.setCaretPosition(0);
                            }
                        } else {
                            deltaMemoryMode = newDeltaMode;
                            loadFromFile(fileUri, null);
                        }
                    } else {
                        // If document unsaved in memory, switch data in code area
                        BinaryData oldData = codeArea.getData();
                        if (codeArea.getData() instanceof DeltaDocument) {
                            XBData data = new XBData();
                            data.insert(0, codeArea.getData());
                            codeArea.setData(data);
                        } else {
                            DeltaDocument document = segmentsRepository.createDocument();
                            document.insert(0, oldData);
                            codeArea.setData(document);
                        }
                        undoHandler.clear();
                        oldData.dispose();
                        codeArea.notifyDataChanged();
                        updateCurrentMemoryMode();
                        deltaMemoryMode = newDeltaMode;
                    }
                    deltaMemoryMode = newDeltaMode;
                }
            }
        });

        if (deltaMemoryMode) {
            setNewData();
        }
        updateCurrentMemoryMode();
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        setCharsetChangeListener(new HexPanel.CharsetChangeListener() {
            @Override
            public void charsetChanged() {
                encodingStatus.setEncoding(getCharset().name());
            }
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

    @Override
    public void setMatchPosition(int matchPosition) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        painter.setCurrentMatchIndex(matchPosition);
        HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        codeArea.revealPosition(currentMatch.getPosition(), codeArea.getActiveSection());
        codeArea.repaint();
    }

    @Override
    public void updatePosition() {
        hexSearchPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getDataSize());
    }

    private void updateCurrentDocumentSize() {
        long dataSize = codeArea.getData().getDataSize();
        long difference = dataSize - documentOriginalSize;
        hexStatus.setCurrentDocumentSize(dataSize + " (" + (difference > 0 ? "+" + difference : difference) + ")");
    }

    public void setDeltaMemoryMode(boolean deltaMemoryMode) {
        this.deltaMemoryMode = deltaMemoryMode;
    }

    private void updateCurrentMemoryMode() {
        HexStatusApi.MemoryMode memoryMode = HexStatusApi.MemoryMode.RAM_MEMORY;
        if (codeArea.getEditationAllowed() == EditationAllowed.READ_ONLY) {
            memoryMode = HexStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getData() instanceof DeltaDocument) {
            memoryMode = HexStatusApi.MemoryMode.DELTA_MODE;
        }

        if (hexStatus != null) {
            hexStatus.setMemoryMode(memoryMode);
        }
    }

    @Override
    public void clearMatches() {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        painter.clearMatches();
    }

    public void setGoToLineAction(Action goToLineAction) {
        this.goToLineAction = goToLineAction;
    }

    public void setEncodingStatusHandler(EncodingStatusHandler encodingStatusHandler) {
        this.encodingStatusHandler = encodingStatusHandler;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler hexCodePopupMenuHandler) {
        hexSearchPanel.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
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
    public HexPanel getDocument() {
        return this;
    }

    /**
     * Helper method for notifying listeners, that HexPanel tab was switched.
     */
    public void notifyListeners() {
        if (charsetChangeListener != null) {
            charsetChangeListener.charsetChanged();
        }
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }

        hexStatus.setEditationMode(codeArea.getEditationMode());
        CaretPosition caretPosition = codeArea.getCaretPosition();
        String position = String.valueOf(caretPosition.getDataPosition());
        position += ":" + caretPosition.getCodeOffset();
        hexStatus.setCursorPosition(position);
        encodingStatus.setEncoding(codeArea.getCharset().name());
    }

    @Override
    public void setModificationListener(final EditorModificationListener editorModificationListener) {
        codeArea.addDataChangedListener(new DataChangedListener() {
            @Override
            public void dataChanged() {
                editorModificationListener.modified();
            }
        });
    }

    private void setNewData() {
        if (deltaMemoryMode) {
            codeArea.setData(segmentsRepository.createDocument());
        } else {
            codeArea.setData(new XBData());
        }
    }

    public static interface CharsetChangeListener {

        public void charsetChanged();
    }

    public static interface ReleaseFileMethod {

        public boolean execute();
    }
}
