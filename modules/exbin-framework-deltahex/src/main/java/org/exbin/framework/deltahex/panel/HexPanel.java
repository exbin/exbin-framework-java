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
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.Hexadecimal;
import org.exbin.deltahex.HexadecimalCaret;
import org.exbin.deltahex.delta.BinaryDataSegment;
import org.exbin.deltahex.delta.DataSegment;
import org.exbin.deltahex.delta.DeltaHexadecimalData;
import org.exbin.deltahex.delta.DocumentSegment;
import org.exbin.deltahex.delta.MemoryHexadecimalData;
import org.exbin.deltahex.delta.list.DefaultDoublyLinkedList;
import org.exbin.deltahex.highlight.HighlightHexadecimalPainter;
import org.exbin.deltahex.operation.HexCommandHandler;
import org.exbin.deltahex.operation.HexUndoHandler;
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
 * @version 0.1.0 2016/06/08
 * @author ExBin Project (http://exbin.org)
 */
public class HexPanel extends javax.swing.JPanel implements XBEditorProvider, ClipboardActionsHandler, TextCharsetApi {

    private Hexadecimal hexadecimal;
    private HexUndoHandler undoHandler;
    private String fileName;
    private Color foundTextBackgroundColor;
    private Font defaultFont;
    private Color[] defaultColors;
    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;
    private HexStatusPanel statusPanel = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private FindTextPanel findTextPanel;
    private boolean findTextPanelVisible = false;

    public HexPanel() {
        undoHandler = new HexUndoHandler(hexadecimal);
        undoHandler.addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                hexadecimal.repaint();
            }

            @Override
            public void undoCommandAdded(Command cmnd) {
            }
        });
        initComponents();
        init();
    }

    private void init() {
        hexadecimal = new Hexadecimal();
        hexadecimal.setPainter(new HighlightHexadecimalPainter(hexadecimal));
        hexadecimal.setData(new MemoryHexadecimalData(new XBData()));
        hexadecimal.setHandleClipboard(false);
        hexadecimal.addSelectionChangedListener(new Hexadecimal.SelectionChangedListener() {
            @Override
            public void selectionChanged(Hexadecimal.SelectionRange selection) {
                if (clipboardActionsUpdateListener != null) {
                    clipboardActionsUpdateListener.stateChanged();
                }
            }
        });
        HexCommandHandler commandHandler = new HexCommandHandler(hexadecimal, undoHandler);
        hexadecimal.setCommandHandler(commandHandler);
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

        add(hexadecimal);
        fileName = "";
        foundTextBackgroundColor = Color.YELLOW;
        hexadecimal.setCharset(Charset.forName(TextEncodingPanel.ENCODING_UTF8));
        defaultFont = hexadecimal.getFont();
        defaultColors = new Color[5];
        defaultColors[0] = new Color(hexadecimal.getForeground().getRGB());
        defaultColors[1] = new Color(SystemColor.text.getRGB()); // Patch on wrong value in textArea.getBackground()
        defaultColors[2] = new Color(hexadecimal.getSelectionColor().getRGB());
        defaultColors[3] = new Color(hexadecimal.getSelectionBackgroundColor().getRGB());
        defaultColors[4] = foundTextBackgroundColor;

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (propertyChangeListener != null) {
                    propertyChangeListener.propertyChange(evt);
                }
            }
        });

        findTextPanel = new FindTextPanel(this, false);
        findTextPanel.addCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HexPanel.this.remove(findTextPanel);
                HexPanel.this.validate();
                HexPanel.this.repaint();
                findTextPanelVisible = false;
            }
        });
    }

    public Hexadecimal getHexadecimal() {
        return hexadecimal;
    }

    public boolean changeLineWrap() {
        hexadecimal.setWrapMode(!hexadecimal.isWrapMode());
        return hexadecimal.isWrapMode();
    }

    public boolean changeShowNonprintables() {
        hexadecimal.setShowNonprintingCharacters(!hexadecimal.isShowNonprintingCharacters());
        return hexadecimal.isShowNonprintingCharacters();
    }

    public boolean getWordWrapMode() {
        return hexadecimal.isWrapMode();
    }

    public void setWordWrapMode(boolean mode) {
        if (hexadecimal.isWrapMode() != mode) {
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

    public void findText(SearchParameters findParameters) {
        System.out.println("SEARCH: " + findParameters.getSearchText());
        long position = hexadecimal.getCaretPosition().getDataPosition();
        HighlightHexadecimalPainter painter = (HighlightHexadecimalPainter) hexadecimal.getPainter();
        HighlightHexadecimalPainter.SearchMatch currentMatch = painter.getCurrentMatch();

        if (currentMatch != null) {
            if (currentMatch.getPosition() == position) {
                position++;
            }
            // textArea.getHighlighter().removeHighlight(highlight);
        } else if (!findParameters.isSearchFromCursor()) {
            position = 0;
        }
        String findText = findParameters.getSearchText();
        byte[] findBytes = findText.getBytes(hexadecimal.getCharset());
        BinaryData data = hexadecimal.getData();

        List<HighlightHexadecimalPainter.SearchMatch> foundMatches = new ArrayList<>();

        long dataSize = data.getDataSize();
        while (position < dataSize - findBytes.length) {
            int matchLength = 0;
            while (matchLength < findBytes.length) {
                if (data.getByte(position + matchLength) != findBytes[matchLength]) {
                    break;
                }
                matchLength++;
            }

            if (matchLength == findBytes.length) {
                HighlightHexadecimalPainter.SearchMatch match = new HighlightHexadecimalPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(findBytes.length);
                foundMatches.add(match);

                if (foundMatches.size() == 100) {
                    break;
                }
            }

            position++;
        }

        painter.setMatches(foundMatches);
        if (foundMatches.isEmpty()) {
            JOptionPane.showMessageDialog(null, "String was not found", "Find text", JOptionPane.INFORMATION_MESSAGE); // getFrame
        }
        hexadecimal.repaint();
    }

    public Color[] getCurrentColors() {
        Color[] colors = new Color[5];
        colors[0] = hexadecimal.getForeground();
        colors[1] = hexadecimal.getBackground();
        colors[2] = hexadecimal.getSelectionColor();
        colors[3] = hexadecimal.getSelectionBackgroundColor();
        colors[4] = getFoundTextBackgroundColor();
        return colors;
    }

    public Color[] getDefaultColors() {
        return defaultColors;
    }

    public void setCurrentColors(Color[] colors) {
        if (colors[0] != null) {
            hexadecimal.setForeground(colors[0]);
        }
        if (colors[1] != null) {
            hexadecimal.setBackground(colors[1]);
        }
        if (colors[2] != null) {
            hexadecimal.setSelectionColor(colors[2]);
        }
        if (colors[3] != null) {
            hexadecimal.setSelectionBackgroundColor(colors[3]);
        }
        if (colors[4] != null) {
            setFoundTextBackgroundColor(colors[4]);
        }
    }

    public void goToPosition(long position) {
        hexadecimal.getCaret().setCaretPosition(position);
    }

    @Override
    public void performCopy() {
        hexadecimal.copy();
    }

    @Override
    public void performCut() {
        hexadecimal.cut();
    }

    @Override
    public void performDelete() {
        hexadecimal.delete();
    }

    @Override
    public void performPaste() {
        hexadecimal.paste();
    }

    @Override
    public void performSelectAll() {
        hexadecimal.selectAll();
    }

    @Override
    public boolean isSelection() {
        return hexadecimal.hasSelection();
    }

    public void printFile() {
//        try {
//            textArea.print();
//        } catch (PrinterException ex) {
//            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void setCurrentFont(Font font) {
        hexadecimal.setFont(font);
    }

    public Font getCurrentFont() {
        return hexadecimal.getFont();
    }

    public void showFontDialog(TextFontDialog dlg) {
        dlg.setStoredFont(hexadecimal.getFont());
        dlg.setVisible(true);
        if (dlg.getDialogOption() == JOptionPane.OK_OPTION) {
            hexadecimal.setFont(dlg.getStoredFont());
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/exbin/framework/deltahex/panel/Bundle"); // NOI18N
        debugButton.setText(bundle.getString("HexPanel.debugButton.text")); // NOI18N
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
        DefaultDoublyLinkedList<DataSegment> segments = ((DeltaHexadecimalData) hexadecimal.getData()).getSegments();
        DataSegment segment = segments.first();
        System.out.println("Segments list: " + ((DeltaHexadecimalData) hexadecimal.getData()).getDataSize());
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

    public HexUndoHandler getHexUndoHandler() {
        return undoHandler;
    }

    public void setHexUndoHandler(HexUndoHandler hexUndoHandler) {
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
                BinaryData data = hexadecimal.getData();
                ((EditableBinaryData) data).loadFromStream(fileStream);
                hexadecimal.setData(data);
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
            hexadecimal.getData().saveToStream(new FileOutputStream(file));
        } catch (IOException ex) {
            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandler.setSyncPoint();
    }

    @Override
    public void newFile() {
        ((EditableBinaryData) hexadecimal.getData()).clear();
        hexadecimal.setData(hexadecimal.getData());
        hexadecimal.repaint();
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
        hexadecimal.setComponentPopupMenu(menu);
    }

    public void loadFromStream(InputStream stream) throws IOException {
        ((EditableBinaryData) hexadecimal.getData()).loadFromStream(stream);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        ((EditableBinaryData) hexadecimal.getData()).loadFromStream(stream, 0, dataSize);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        hexadecimal.getData().saveToStream(stream);
    }

    public Point getCaretPosition() {
//        int line;
//        int caretPosition = textArea.getCaretPosition();
//        javax.swing.text.Element root = textArea.getDocument().getDefaultRootElement();
//        line = root.getElementIndex(caretPosition);
//        try {
//            return new Point(caretPosition - textArea.getLineStartOffset(line) + 1, line + 1);
//        } catch (BadLocationException ex) {
//            Logger.getLogger(HexPanel.class.getName()).log(Level.SEVERE, null, ex);
//            return new Point(0, 0);
//        }
        return new Point();
    }

    public void attachCaretListener(Hexadecimal.CaretMovedListener listener) {
        hexadecimal.addCaretMovedListener(listener);
    }

    public void attachSelectionListener(Hexadecimal.SelectionChangedListener listener) {
        hexadecimal.addSelectionChangedListener(listener);
    }

    @Override
    public Charset getCharset() {
        return hexadecimal.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        hexadecimal.setCharset(charset);
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
        hexadecimal.setCharset(charset);
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
        attachCaretListener(new Hexadecimal.CaretMovedListener() {
            @Override
            public void caretMoved(CaretPosition caretPosition, HexadecimalCaret.Section section) {
                String position = String.valueOf(caretPosition.getDataPosition());
                if (caretPosition.isLowerHalf()) {
                    position += ".5";
                }
                statusPanel.setCursorPosition(position);
            }
        });

        attachSelectionListener(new Hexadecimal.SelectionChangedListener() {
            @Override
            public void selectionChanged(Hexadecimal.SelectionRange selection) {
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
        return hexadecimal.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return hexadecimal.canPaste();
    }

    public interface CharsetChangeListener {

        public void charsetChanged();
    }
}
