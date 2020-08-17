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
package org.exbin.framework.bined.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JOptionPane;
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
import org.exbin.bined.RowWrappingMode;
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
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.bined.service.impl.BinarySearchServiceImpl;
import org.exbin.framework.gui.editor.api.EditorProvider.EditorModificationListener;
import org.exbin.xbup.core.util.StringUtils;

/**
 * Binary editor panel.
 *
 * @version 0.2.1 2020/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel implements ClipboardActionsHandler, TextCharsetApi, TextFontApi {

    private static final FileHandlingMode DEFAULT_FILE_HANDLING_MODE = FileHandlingMode.DELTA;

    private BinEdComponentFileApi fileApi = null;
    private BinaryEditorPreferences preferences;
    private ExtCodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private Color foundTextBackgroundColor;
    private Font defaultFont;
    private ExtendedCodeAreaColorProfile defaultColors;
    private BinaryStatusApi binaryStatus = null;
    private TextEncodingStatusApi encodingStatus = null;

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

    private FileHandlingMode fileHandlingMode = DEFAULT_FILE_HANDLING_MODE;

    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private ReleaseFileMethod releaseFileMethod = null;
    private ModifiedStateListener modifiedChangeListener = null;

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.addSelectionChangedListener((SelectionRange selection) -> {
            updateClipboardActionsStatus();
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
        codeArea.setCharset(Charset.forName(StringUtils.ENCODING_UTF8));
        defaultFont = codeArea.getCodeFont();

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
        setShowValuesPanel(true);
    }

    public void setApplication(XBApplication application) {
        preferences = new BinaryEditorPreferences(application.getAppPreferences());
        binarySearchPanel.setApplication(application);
    }

    public void setFileApi(BinEdComponentFileApi fileApi) {
        this.fileApi = fileApi;
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
            BinEdComponentPanel.this.remove(binarySearchPanel);
            BinEdComponentPanel.this.revalidate();
            binarySearchPanelVisible = false;
        }
    }

    public void setShowValuesPanel(boolean show) {
        if (valuesPanelVisible != show) {
            if (show) {
                add(valuesPanelScrollPane, BorderLayout.EAST);
                revalidate();
                valuesPanelVisible = true;
                valuesPanel.enableUpdate();
            } else {
                valuesPanel.disableUpdate();
                BinEdComponentPanel.this.remove(valuesPanelScrollPane);
                BinEdComponentPanel.this.revalidate();
                valuesPanelVisible = false;
            }
        }
    }

    public boolean isShowValuesPanel() {
        return valuesPanelVisible;
    }

    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    public boolean changeLineWrap() {
        ((RowWrappingCapable) codeArea).setRowWrapping(((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING ? RowWrappingMode.NO_WRAPPING : RowWrappingMode.WRAPPING);
        return ((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING;
    }

    public boolean isShowNonprintables() {
        return codeArea.isShowUnprintables();
    }

    public void setShowNonprintables(boolean show) {
        codeArea.setShowUnprintables(show);
    }

    public boolean isWordWrapMode() {
        return false;
        // TODO codeArea.isWrapMode();
    }

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

    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return (ExtendedCodeAreaColorProfile) codeArea.getColorsProfile();
    }

    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return defaultColors;
    }

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

    @Override
    public void setCurrentFont(Font font) {
        codeArea.setCodeFont(font);
    }

    @Override
    public Font getCurrentFont() {
        return codeArea.getCodeFont();
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
        WindowUtils.invokeDialog(new BinEdComponentPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    public boolean releaseFile() {
        if (releaseFileMethod != null) {
            return releaseFileMethod.execute();
        }

        return true;
    }

    public CodeAreaUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setUndoHandler(CodeAreaUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        if (valuesPanel != null) {
            valuesPanel.setCodeArea(codeArea, undoHandler);
        }
        // TODO set ENTER KEY mode in apply options

        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
                updateCurrentDocumentSize();
                notifyModified();
            }

            @Override
            public void undoCommandAdded(@Nonnull final BinaryDataCommand command) {
                updateCurrentDocumentSize();
                notifyModified();
            }
        });
    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
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

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
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

    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        attachCaretListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });
        codeArea.addSelectionChangedListener(selectionRange -> {
            binaryStatus.setSelectionRange(selectionRange);
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
                FileHandlingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
                if (newHandlingMode != fileHandlingMode) {
                    fileApi.switchFileHandlingMode(newHandlingMode);
                    preferences.getEditorPreferences().setFileHandlingMode(newHandlingMode);
                }
            }
        });

        updateCurrentMemoryMode();
    }

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

    @Override
    public boolean canDelete() {
        return true;
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

    private void updateCurrentSelectionRange() {
        if (binaryStatus != null) {
            SelectionRange selectionRange = codeArea.getSelection();
            binaryStatus.setSelectionRange(selectionRange);
        }
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        return fileHandlingMode;
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

    @Nullable
    public BinaryData getContentData() {
        return codeArea.getContentData();
    }

    public void setContentData(BinaryData data) {
        codeArea.setContentData(data);

        documentOriginalSize = codeArea.getDataSize();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();

        // Autodetect encoding using IDE mechanism
//        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//        codeArea.setCharset(charset);
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
            updateCurrentSelectionRange();
        }

        encodingStatus.setEncoding(codeArea.getCharset().name());
    }

    public void setModificationListener(final EditorModificationListener editorModificationListener) {
        codeArea.addDataChangedListener(editorModificationListener::modified);
    }

    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        this.fileHandlingMode = fileHandlingMode;
        updateCurrentMemoryMode();
    }

    public void setModifiedChangeListener(ModifiedStateListener modifiedChangeListener) {
        this.modifiedChangeListener = modifiedChangeListener;
    }

    private void notifyModified() {
        if (modifiedChangeListener != null) {
            modifiedChangeListener.modifiedChanged();
        }
    }

    public interface CharsetChangeListener {

        void charsetChanged();
    }

    public interface ReleaseFileMethod {

        boolean execute();
    }

    public interface ModifiedStateListener {

        void modifiedChanged();
    }
}
