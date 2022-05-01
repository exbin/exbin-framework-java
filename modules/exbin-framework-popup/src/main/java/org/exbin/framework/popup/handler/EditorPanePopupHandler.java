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
package org.exbin.framework.popup.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;
import org.exbin.framework.popup.LinkActionsHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;

/**
 * Popup handler for JEditorPane.
 *
 * @version 0.2.1 2022/05/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorPanePopupHandler implements ClipboardActionsHandler, LinkActionsHandler {

    private final JEditorPane editorPane;

    public EditorPanePopupHandler(JEditorPane editorPane) {
        this.editorPane = editorPane;
    }

    @Override
    public void performCut() {
        editorPane.cut();
    }

    @Override
    public void performCopy() {
        editorPane.copy();
    }

    @Override
    public void performPaste() {
        editorPane.paste();
    }

    @Override
    public void performDelete() {
        ActionUtils.invokeTextAction(editorPane, DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    public void performSelectAll() {
        editorPane.selectAll();
    }

    @Override
    public boolean isSelection() {
        return editorPane.isEnabled() && editorPane.getSelectionStart() != editorPane.getSelectionEnd();
    }

    @Override
    public boolean isEditable() {
        return editorPane.isEnabled() && editorPane.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return editorPane.isEnabled() && !editorPane.getText().isEmpty();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        // Ignore
    }

    @Override
    public boolean canPaste() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public void performCopyLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performOpenLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLinkSelected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
