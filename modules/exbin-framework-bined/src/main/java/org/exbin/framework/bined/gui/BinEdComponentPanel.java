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
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.exbin.bined.CaretMovedListener;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.handler.EncodingStatusHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.bined.service.impl.BinarySearchServiceImpl;
import org.exbin.bined.EditModeChangedListener;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.framework.bined.BinaryStatusApi;

/**
 * Binary editor panel.
 *
 * @version 0.2.1 2021/10/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel {

    private ExtCodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private TextEncodingStatusApi encodingStatus = null;

    private BinarySearchPanel binarySearchPanel;
    private boolean binarySearchPanelVisible = false;
    private ValuesPanel valuesPanel;
    private JScrollPane valuesPanelScrollPane;
    private boolean valuesPanelVisible = false;
    private EncodingStatusHandler encodingsHandler;

//    private PropertyChangeListener propertyChangeListener;
    private CharsetChangeListener charsetChangeListener = null;

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//        codeArea.addSelectionChangedListener(() -> {
//            updateClipboardActionsStatus();
//        });

        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
//        codeArea.addDataChangedListener(() -> {
//            if (binarySearchPanelVisible) {
//                binarySearchPanel.dataChanged();
//            }
//            updateCurrentDocumentSize();
//        });
        // TODO use listener in code area component instead
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.addFlavorListener((FlavorEvent e) -> {
//            updateClipboardActionsStatus();
//        });

        add(codeArea);
//        codeArea.setCharset(Charset.forName(StringUtils.ENCODING_UTF8));

//        addPropertyChangeListener((PropertyChangeEvent evt) -> {
//            if (propertyChangeListener != null) {
//                propertyChangeListener.propertyChange(evt);
//            }
//        });
        binarySearchPanel = new BinarySearchPanel();
        binarySearchPanel.setBinarySearchService(new BinarySearchServiceImpl(codeArea));
        binarySearchPanel.setClosePanelListener(this::hideSearchPanel);

        valuesPanel = new ValuesPanel();
        valuesPanel.setCodeArea(codeArea, undoHandler);
        valuesPanelScrollPane = new JScrollPane(valuesPanel);
        valuesPanelScrollPane.setBorder(null);
        setShowValuesPanel(true);
    }

    public void setApplication(XBApplication application) {
        binarySearchPanel.setApplication(application);
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

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    public boolean changeLineWrap() {
        ((RowWrappingCapable) codeArea).setRowWrapping(((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING ? RowWrappingMode.NO_WRAPPING : RowWrappingMode.WRAPPING);
        return ((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING;
    }

    public void findAgain() {
        // TODO hexSearchPanel.f
    }

    public void notifyDataChanged() {
        if (binarySearchPanelVisible) {
            binarySearchPanel.dataChanged();
        }
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
    @Nullable
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

    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
    }

    public void attachCaretListener(CaretMovedListener listener) {
        codeArea.addCaretMovedListener(listener);
    }

    public void attachEditModeChangedListener(EditModeChangedListener listener) {
        codeArea.addEditModeChangedListener(listener);
    }

//    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
//        this.propertyChangeListener = propertyChangeListener;
//    }
    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
        this.charsetChangeListener = charsetChangeListener;
    }

//    private void changeCharset(Charset charset) {
//        codeArea.setCharset(charset);
//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//    }
//
//    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
//        this.encodingStatus = encodingStatusApi;
//        setCharsetChangeListener(() -> {
//            encodingStatus.setEncoding(getCharset().name());
//        });
//    }
//
//    public void setEncodingsHandler(EncodingStatusHandler encodingsHandler) {
//        this.encodingsHandler = encodingsHandler;
//    }
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        attachCaretListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });
        codeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(codeArea.getSelection());
        });

        attachEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            binaryStatus.setEditMode(mode, operation);
        });
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());

        binaryStatus.setControlHandler(new BinaryStatusApi.StatusControlHandler() {
            @Override
            public void changeEditOperation(EditOperation editOperation) {
                codeArea.setEditOperation(editOperation);
            }

            @Override
            public void changeCursorPosition() {
//                if (goToPositionAction != null) {
//                    goToPositionAction.actionPerformed(null);
//                }
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
                throw new IllegalStateException();
//                FileHandlingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
//                if (newHandlingMode != fileHandlingMode) {
//                    fileApi.switchFileHandlingMode(newHandlingMode);
//                    preferences.getEditorPreferences().setFileHandlingMode(newHandlingMode);
//                }
            }
        });

        BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
            newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        binaryStatus.setMemoryMode(newMemoryMode);
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        binarySearchPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
    }

    @Nullable
    public BinaryData getContentData() {
        return codeArea.getContentData();
    }

    public void setContentData(BinaryData data) {
        codeArea.setContentData(data);

        // Autodetect encoding using IDE mechanism
//        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//        codeArea.setCharset(charset);
    }

    public void addBinaryAreaFocusListener(FocusListener focusListener) {
        codeArea.addFocusListener(focusListener);
    }

    public void removeBinaryAreaFocusListener(FocusListener focusListener) {
        codeArea.removeFocusListener(focusListener);
    }

    public interface CharsetChangeListener {

        void charsetChanged();
    }

    public interface Control {

    }
}
