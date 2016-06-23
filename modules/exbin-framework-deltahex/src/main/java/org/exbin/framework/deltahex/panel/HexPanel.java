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
package org.exbin.framework.deltahex.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
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
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.CodeArea;
import org.exbin.deltahex.delta.BinaryDataSegment;
import org.exbin.deltahex.delta.DataSegment;
import org.exbin.deltahex.delta.DeltaHexadecimalData;
import org.exbin.deltahex.delta.DocumentSegment;
import org.exbin.deltahex.delta.MemoryPagedData;
import org.exbin.deltahex.delta.list.DefaultDoublyLinkedList;
import org.exbin.deltahex.highlight.HighlightCodeAreaPainter;
import org.exbin.deltahex.operation.CodeAreaUndoHandler;
import org.exbin.deltahex.operation.CodeCommandHandler;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.editor.text.panel.TextEncodingPanel;
import org.exbin.framework.gui.editor.api.XBEditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.menu.api.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.editor.text.TextCharsetApi;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.xbup.core.type.XBData;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * Hexadecimal editor panel.
 *
 * @version 0.1.0 2016/06/23
 * @author ExBin Project (http://exbin.org)
 */
public class HexPanel extends javax.swing.JPanel implements XBEditorProvider, ClipboardActionsHandler, TextCharsetApi {

    private CodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private String fileName;
    private Color foundTextBackgroundColor;
    private Font defaultFont;
    private Map<HexColorType, Color> defaultColors;
    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;
    private HexStatusPanel statusPanel = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private FindTextPanel findTextPanel;
    private boolean findTextPanelVisible = false;

    public HexPanel() {
        undoHandler = new CodeAreaUndoHandler(codeArea);
        undoHandler.addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
            }

            @Override
            public void undoCommandAdded(Command cmnd) {
            }
        });
        initComponents();
        init();
    }

    private void init() {
        codeArea = new CodeArea();
        codeArea.setPainter(new HighlightCodeAreaPainter(codeArea));
        codeArea.setData(new MemoryPagedData(new XBData()));
        codeArea.setHandleClipboard(false);
        codeArea.addSelectionChangedListener(new CodeArea.SelectionChangedListener() {
            @Override
            public void selectionChanged(CodeArea.SelectionRange selection) {
                if (clipboardActionsUpdateListener != null) {
                    clipboardActionsUpdateListener.stateChanged();
                }
            }
        });
        CodeCommandHandler commandHandler = new CodeCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeArea.addDataChangedListener(new CodeArea.DataChangedListener() {
            @Override
            public void dataChanged() {
                findTextPanel.dataChanged();
            }
        });
        // TODO use listener in hexadecimal instead
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                if (clipboardActionsUpdateListener != null) {
                    clipboardActionsUpdateListener.stateChanged();
                }
            }
        });

        add(codeArea);
        fileName = "";
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

        findTextPanel = new FindTextPanel(this, false);
        findTextPanel.setClosePanelListener(new FindTextPanel.ClosePanelListener() {
            @Override
            public void panelClosed() {
                hideFindPanel();
            }
        });
    }

    public void hideFindPanel() {
        if (findTextPanelVisible) {
            findTextPanel.cancelSearch();
            HexPanel.this.remove(findTextPanel);
            HexPanel.this.revalidate();
            findTextPanelVisible = false;
        }
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public boolean changeLineWrap() {
        codeArea.setWrapMode(!codeArea.isWrapMode());
        return codeArea.isWrapMode();
    }

    public boolean changeShowNonprintables() {
        codeArea.setShowUnprintableCharacters(!codeArea.isShowUnprintableCharacters());
        return codeArea.isShowUnprintableCharacters();
    }

    public boolean getWordWrapMode() {
        return codeArea.isWrapMode();
    }

    public void setWordWrapMode(boolean mode) {
        if (codeArea.isWrapMode() != mode) {
            changeLineWrap();
        }
    }

    public void showFindPanel() {
        if (!findTextPanelVisible) {
            add(findTextPanel, BorderLayout.SOUTH);
            revalidate();
            findTextPanelVisible = true;
            findTextPanel.setRequestFocus();
        }
    }

    public void findText(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        findTextPanel.clearStatus();
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
                    position = codeArea.getData().getDataSize() - 1;
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
        findTextPanel.setStatus(foundMatches.size(), 0);
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
        while (position < dataSize - findText.length()) {
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
        findTextPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    public Map<HexColorType, Color> getCurrentColors() {
        Map<HexColorType, Color> colors = new HashMap<>();
        for (HexColorType colorType : HexColorType.values()) {
            Color color = colorType.getColorFromCodeArea(codeArea);
            colors.put(colorType, color);
        }
        return colors;
    }

    public Map<HexColorType, Color> getDefaultColors() {
        return defaultColors;
    }

    public void setCurrentColors(Map<HexColorType, Color> colors) {
        for (Map.Entry<HexColorType, Color> entry : colors.entrySet()) {
            entry.getKey().setColorToCodeArea(codeArea, entry.getValue());
        }
    }

    public void goToPosition(long position) {
        codeArea.getCaret().setCaretPosition(position);
        codeArea.revealCursor();
    }

    @Override
    public void performCopy() {
        codeArea.copy();
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

    @Override
    public void performSelectAll() {
        codeArea.selectAll();
    }

    @Override
    public boolean isSelection() {
        return codeArea.hasSelection();
    }

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

    public void setCurrentFont(Font font) {
        codeArea.setFont(font);
    }

    public Font getCurrentFont() {
        return codeArea.getFont();
    }

    public void showFontDialog(TextFontDialog dlg) {
        dlg.setStoredFont(codeArea.getFont());
        dlg.setVisible(true);
        if (dlg.getDialogOption() == JOptionPane.OK_OPTION) {
            codeArea.setFont(dlg.getStoredFont());
        }
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

        debugPanel = new javax.swing.JPanel();
        debugButton = new javax.swing.JButton();

        debugPanel.setName("debugPanel"); // NOI18N

        debugButton.setText("I");
        debugButton.setName("debugButton"); // NOI18N
        debugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout debugPanelLayout = new javax.swing.GroupLayout(debugPanel);
        debugPanel.setLayout(debugPanelLayout);
        debugPanelLayout.setHorizontalGroup(
            debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(debugButton)
        );
        debugPanelLayout.setVerticalGroup(
            debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(debugPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(debugButton))
        );

        setInheritsPopupMenu(true);
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void debugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugButtonActionPerformed
        DefaultDoublyLinkedList<DataSegment> segments = ((DeltaHexadecimalData) codeArea.getData()).getSegments();
        DataSegment segment = segments.first();
        System.out.println("Segments list: " + ((DeltaHexadecimalData) codeArea.getData()).getDataSize());
        while (segment != null) {
            if (segment instanceof DocumentSegment) {
                System.out.println("FILE: " + ((DocumentSegment) segment).getStartPosition() + ", " + ((DocumentSegment) segment).getLength());
            } else {
                System.out.println("DATA: " + ((BinaryDataSegment) segment).getLength());
            }
            segment = segment.getNext();
        }
        System.out.println();
    }//GEN-LAST:event_debugButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton debugButton;
    private javax.swing.JPanel debugPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    public CodeAreaUndoHandler getHexUndoHandler() {
        return undoHandler;
    }

    public void setHexUndoHandler(CodeAreaUndoHandler hexUndoHandler) {
        this.undoHandler = hexUndoHandler;
    }

    @Override
    public void loadFromFile() {
        File file = new File(getFileName());
        try {
//            DeltaDataSource dataSource = new DeltaDataSource(file);
//            DeltaHexadecimalData deltaData = new DeltaHexadecimalData(dataSource);
//            hexadecimal.setData(deltaData);
            try (FileInputStream fileStream = new FileInputStream(file)) {
                BinaryData data = codeArea.getData();
                ((EditableBinaryData) data).loadFromStream(fileStream);
                codeArea.setData(data);
            }
        } catch (IOException ex) {
            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.clear();
    }

    @Override
    public void saveToFile() {
        File file = new File(getFileName());
        try {
            codeArea.getData().saveToStream(new FileOutputStream(file));
        } catch (IOException ex) {
            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.setSyncPoint();
    }

    @Override
    public void newFile() {
        ((EditableBinaryData) codeArea.getData()).clear();
        codeArea.setData(codeArea.getData());
        codeArea.repaint();
        undoHandler.clear();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
    }

    public void loadFromStream(InputStream stream) throws IOException {
        ((EditableBinaryData) codeArea.getData()).loadFromStream(stream);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        ((EditableBinaryData) codeArea.getData()).loadFromStream(stream, 0, dataSize);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        codeArea.getData().saveToStream(stream);
    }

    public void attachCaretListener(CodeArea.CaretMovedListener listener) {
        codeArea.addCaretMovedListener(listener);
    }

    public void attachSelectionListener(CodeArea.SelectionChangedListener listener) {
        codeArea.addSelectionChangedListener(listener);
    }

    @Override
    public Charset getCharset() {
        return codeArea.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        codeArea.setCharset(charset);
    }

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
        if (!"".equals(fileName)) {
            int pos;
            int newpos = 0;
            do {
                pos = newpos;
                newpos = fileName.indexOf(File.separatorChar, pos) + 1;
            } while (newpos > 0);
            return fileName.substring(pos) + " - " + frameTitle;
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

    public void registerTextStatus(HexStatusPanel hexStatusPanel) {
        this.statusPanel = hexStatusPanel;
        attachCaretListener(new CodeArea.CaretMovedListener() {
            @Override
            public void caretMoved(CaretPosition caretPosition, CodeArea.Section section) {
                String position = String.valueOf(caretPosition.getDataPosition());
                position += ":" + caretPosition.getCodeOffset();
                statusPanel.setCursorPosition(position);
            }
        });

        attachSelectionListener(new CodeArea.SelectionChangedListener() {
            @Override
            public void selectionChanged(CodeArea.SelectionRange selection) {
                if (selection == null) {
                    statusPanel.setSelectionPosition("", "");
                } else {
                    String start = String.valueOf(selection.getFirst());
                    String end = String.valueOf(selection.getLast());
                    statusPanel.setSelectionPosition(start, end);
                }
            }
        });

        setCharsetChangeListener(new HexPanel.CharsetChangeListener() {
            @Override
            public void charsetChanged() {
                statusPanel.setEncoding(getCharset().name());
            }
        });
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        clipboardActionsUpdateListener.stateChanged();
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

    public void setMatchPosition(int matchPosition) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        painter.setCurrentMatchIndex(matchPosition);
        HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        codeArea.revealPosition(currentMatch.getPosition(), codeArea.getActiveSection());
        codeArea.repaint();
    }

    public void updatePosition() {
        findTextPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getData().getDataSize());
    }

    public void clearMatches() {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        painter.clearMatches();
    }

    public static interface CharsetChangeListener {

        public void charsetChanged();
    }
}
