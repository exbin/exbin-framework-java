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
package org.exbin.framework.editor.text;

import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;
import org.exbin.framework.action.api.clipboard.ClipboardStateListener;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.text.font.TextFontController;
import org.exbin.framework.text.encoding.TextEncodingController;
import org.exbin.framework.action.api.clipboard.TextClipboardController;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.action.api.ContextComponent;

/**
 * Text panel component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorTextPanelComponent implements ContextComponent, TextPanelComponent, TextClipboardController, TextEncodingController, TextFontController {

    private final TextPanel textPanel;

    public EditorTextPanelComponent(TextPanel textPanel) {
        this.textPanel = textPanel;
    }

    @Nonnull
    @Override
    public TextPanel getTextPanel() {
        return textPanel;
    }

    @Override
    public void performCopy() {
        textPanel.getTextArea().copy();
    }

    @Override
    public void performCut() {
        textPanel.getTextArea().cut();
    }

    @Override
    public void performDelete() {
        ActionUtils.invokeTextAction(textPanel.getTextArea(), DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    public void performPaste() {
        textPanel.getTextArea().paste();
    }

    @Override
    public void performSelectAll() {
        textPanel.getTextArea().selectAll();
    }

    @Override
    public boolean hasSelection() {
        JTextArea textArea = textPanel.getTextArea();
        return textArea.getSelectionEnd() > textArea.getSelectionStart();
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public void setUpdateListener(ClipboardStateListener updateListener) {
        textPanel.setUpdateListener(updateListener);
    }

    @Override
    public boolean isEditable() {
        return textPanel.getTextArea().isEditable();
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
        return textPanel.getTextArea().isEditable();
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return textPanel.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        textPanel.setCharset(charset);
    }

    @Nonnull
    @Override
    public Font getCurrentFont() {
        return textPanel.getCurrentFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return textPanel.getDefaultFont();
    }

    @Override
    public void setCurrentFont(Font font) {
        textPanel.setCurrentFont(font);
    }
}
