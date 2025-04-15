/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.text.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.JTextComponent;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.service.impl.TextServiceImpl;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.editor.text.service.TextSearchService;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.TestApplication;

/**
 * Text editor panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextPanel extends javax.swing.JPanel implements ClipboardActionsHandler {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TextPanel.class);

    private final TextPanelCompoundUndoManager undoManagement = new TextPanelCompoundUndoManager();
    private boolean modified = false;
    private Object highlight;
    private Color foundTextBackgroundColor;
    private Charset charset;
    private Font defaultFont;
    private Color[] defaultColors;
    private CharsetChangeListener charsetChangeListener = null;
    private TextStatusPanel textStatus = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;

    public TextPanel() {
        initComponents();
        init();
    }

    private void init() {
        highlight = null;
        foundTextBackgroundColor = Color.YELLOW;
        charset = Charset.forName(EncodingsHandler.ENCODING_UTF8);
        defaultFont = textArea.getFont();
        defaultColors = new Color[5];
        defaultColors[0] = new Color(textArea.getForeground().getRGB());
        defaultColors[1] = new Color(SystemColor.text.getRGB()); // Patch on wrong value in textArea.getBackground()
        defaultColors[2] = new Color(textArea.getSelectedTextColor().getRGB());
        defaultColors[3] = new Color(textArea.getSelectionColor().getRGB());
        defaultColors[4] = foundTextBackgroundColor;

        // if the document is ever edited, assume that it needs to be saved
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                setModified(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                setModified(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setModified(true);
            }
        });

        textArea.getDocument().addUndoableEditListener(undoManagement::undoableEditHappened);
        textArea.addCaretListener((e) -> {
            // TODO detect selection changes only
            if (clipboardActionsUpdateListener != null) {
                clipboardActionsUpdateListener.stateChanged();
            }
        });
    }

    public boolean changeLineWrap() {
        textArea.setLineWrap(!textArea.getLineWrap());
        return textArea.getLineWrap();
    }

    public boolean getWordWrapMode() {
        return textArea.getLineWrap();
    }

    public void setWordWrapMode(boolean mode) {
        if (textArea.getLineWrap() != mode) {
            changeLineWrap();
        }
    }

    public void findText(TextSearchService.FindTextParameters findTextParameters) {
        int pos = textArea.getCaretPosition();
        if (highlight != null) {
            if (((Highlight) highlight).getStartOffset() == pos) {
                pos++;
            }
            textArea.getHighlighter().removeHighlight(highlight);
        } else if (findTextParameters.isSearchFromStart()) {
            pos = 0;
        }

        findTextParameters.setStartFrom(pos);
        TextSearchService textService = new TextServiceImpl();
        Optional<TextSearchService.FoundMatch> optFoundMatch = textService.findText(textArea, findTextParameters);

        if (optFoundMatch.isPresent()) {
            TextSearchService.FoundMatch foundMatch = optFoundMatch.get();
            try {
                textArea.setCaretPosition(foundMatch.getTo());
                highlight = textArea.getHighlighter().addHighlight(foundMatch.getFrom(), foundMatch.getTo(), new DefaultHighlighter.DefaultHighlightPainter(foundTextBackgroundColor));
            } catch (BadLocationException ex) {
                Logger.getLogger(TextPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(UiUtils.getFrame(this), resourceBundle.getString("notFound.message"), resourceBundle.getString("notFound.title"), JOptionPane.INFORMATION_MESSAGE);
            highlight = null;
        }
    }

    @Nonnull
    public Color[] getCurrentColors() {
        Color[] colors = new Color[5];
        colors[0] = textArea.getForeground();
        colors[1] = textArea.getBackground();
        colors[2] = textArea.getSelectedTextColor();
        colors[3] = textArea.getSelectionColor();
        colors[4] = getFoundTextBackgroundColor();
        return colors;
    }

    public Color[] getDefaultColors() {
        return defaultColors;
    }

    public void setCurrentColors(Color[] colors) {
        if (colors[0] != null) {
            textArea.setForeground(colors[0]);
        }
        if (colors[1] != null) {
            textArea.setBackground(colors[1]);
        }
        if (colors[2] != null) {
            textArea.setSelectedTextColor(colors[2]);
        }
        if (colors[3] != null) {
            textArea.setSelectionColor(colors[3]);
        }
        if (colors[4] != null) {
            setFoundTextBackgroundColor(colors[4]);
        }
    }

    public Document getDocument() {
        return textArea.getDocument();
    }

    public int getLineCount() {
        return textArea.getLineCount();
    }

    public String getText() {
        return textArea.getText();
    }

    public void setNoBorder() {
        textAreaScrollPane.setBorder(null);
    }

    public void gotoLine(int line) {
        try {
            textArea.setCaretPosition(textArea.getLineStartOffset(line - 1));
        } catch (BadLocationException ex) {
            Logger.getLogger(TextPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gotoRelative(int charPos) {
        textArea.setCaretPosition(textArea.getCaretPosition() + charPos - 1);
    }

    @Override
    public void performCopy() {
        textArea.copy();
    }

    @Override
    public void performCut() {
        textArea.cut();
    }

    @Override
    public void performDelete() {
        textArea.getInputContext().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DELETE, KeyEvent.CHAR_UNDEFINED));
    }

    @Override
    public void performPaste() {
        textArea.paste();
    }

    @Override
    public void performSelectAll() {
        textArea.selectAll();
    }

    @Override
    public boolean isSelection() {
        return textArea.getSelectionEnd() > textArea.getSelectionStart();
    }
    
    @Nonnull
    public JTextComponent getTextComponent() {
        return textArea;
    }

    public void printFile() {
        try {
            textArea.print();
        } catch (PrinterException ex) {
            Logger.getLogger(TextPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCurrentFont(Font font) {
        textArea.setFont(font);
    }

    public Font getCurrentFont() {
        return textArea.getFont();
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

        textAreaScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setInheritsPopupMenu(true);
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        textAreaScrollPane.setName("textAreaScrollPane"); // NOI18N

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setName("textArea"); // NOI18N
        textAreaScrollPane.setViewportView(textArea);

        add(textAreaScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication.run(() -> WindowUtils.invokeWindow(new TextPanel()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea textArea;
    private javax.swing.JScrollPane textAreaScrollPane;
    // End of variables declaration//GEN-END:variables

    public void setModified(boolean modified) {
        if (highlight != null) {
            textArea.getHighlighter().removeHighlight(highlight);
            highlight = null;
        }
        boolean oldValue = this.modified;
        this.modified = modified;
        firePropertyChange("modified", oldValue, this.modified);
    }

    @Nonnull
    public TextPanelCompoundUndoManager getUndo() {
        return undoManagement;
    }

    public void setPopupMenu(JPopupMenu menu) {
        textArea.setComponentPopupMenu(menu);
    }

    @Nonnull
    public Point getCaretPosition() {
        int line;
        int caretPosition = textArea.getCaretPosition();
        javax.swing.text.Element root = textArea.getDocument().getDefaultRootElement();
        line = root.getElementIndex(caretPosition);
        try {
            return new Point(caretPosition - textArea.getLineStartOffset(line) + 1, line + 1);
        } catch (BadLocationException ex) {
            Logger.getLogger(TextPanel.class.getName()).log(Level.SEVERE, null, ex);
            return new Point(0, 0);
        }
    }

    public void attachCaretListener(ChangeListener listener) {
        textArea.getCaret().addChangeListener(listener);
    }

    @Nonnull
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Nonnull
    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
        this.charsetChangeListener = charsetChangeListener;
    }

    public void changeCharset(Charset charset) {
        this.charset = charset;
        if (charsetChangeListener != null) {
            charsetChangeListener.charsetChanged();
        }
    }

    public void registerTextStatus(TextStatusPanel textStatusPanel) {
        this.textStatus = textStatusPanel;
        attachCaretListener((ChangeEvent e) -> {
            Point pos = getCaretPosition();
            String textPosition = Long.toString((long) pos.getX()) + ":" + Long.toString((long) pos.getY());
            textStatus.setTextPosition(textPosition);
        });
        setCharsetChangeListener(() -> {
            textStatus.setEncoding(getCharset().name());
        });
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
    }

    @Override
    public boolean isEditable() {
        return textArea.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        Clipboard clipboard = ClipboardUtils.getClipboard();
        return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
    }

    @Override
    public boolean canDelete() {
        return textArea.isEditable();
    }

    public void addTextAreaFocusListener(FocusListener focusListener) {
        textArea.addFocusListener(focusListener);
    }

    public void removeTextAreaFocusListener(FocusListener focusListener) {
        textArea.removeFocusListener(focusListener);
    }

    public boolean isModified() {
        return modified;
    }

    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    public interface CharsetChangeListener {

        public void charsetChanged();
    }
}
