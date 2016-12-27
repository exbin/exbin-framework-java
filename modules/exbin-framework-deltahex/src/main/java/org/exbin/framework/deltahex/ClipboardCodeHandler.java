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
package org.exbin.framework.deltahex;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Clipboard code handling.
 *
 * @version 0.1.0 2016/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class ClipboardCodeHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private Action copyAsCodeAction;
    private Action pasteFromCodeAction;

    public ClipboardCodeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        copyAsCodeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
                    HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
                    activePanel.performCopyAsCode();
                }
            }
        };
        ActionUtils.setupAction(copyAsCodeAction, resourceBundle, "copyAsCodeAction");

        pasteFromCodeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
                    HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
                    activePanel.performPasteFromCode();
                }
            }
        };
        ActionUtils.setupAction(pasteFromCodeAction, resourceBundle, "pasteFromCodeAction");
    }

    public Action getCopyAsCodeAction() {
        return copyAsCodeAction;
    }

    public Action getPasteFromCodeAction() {
        return pasteFromCodeAction;
    }

    public Action createCopyAsCodeAction(final CodeArea codeArea) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copyAsCode();
            }
        };
        ActionUtils.setupAction(action, resourceBundle, "copyAsCodeAction");
        action.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        return action;
    }

    public Action createPasteFromCodeAction(final CodeArea codeArea) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    codeArea.pasteFromCode();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        ActionUtils.setupAction(action, resourceBundle, "pasteFromCodeAction");
        action.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        return action;
    }
}
