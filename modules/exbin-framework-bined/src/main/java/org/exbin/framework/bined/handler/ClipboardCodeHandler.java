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
package org.exbin.framework.bined.handler;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Clipboard code handling.
 *
 * @version 0.2.1 2018/10/29
 * @author ExBin Project (http://exbin.org)
 */
public class ClipboardCodeHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action copyAsCodeAction;
    private Action pasteFromCodeAction;

    public ClipboardCodeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        copyAsCodeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                    activePanel.performCopyAsCode();
                }
            }
        };
        ActionUtils.setupAction(copyAsCodeAction, resourceBundle, "copyAsCodeAction");

        pasteFromCodeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
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

    public Action createCopyAsCodeAction(final CodeAreaCore codeArea) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copyAsCode();
            }
        };
        ActionUtils.setupAction(action, resourceBundle, "copyAsCodeAction");
        return action;
    }

    public Action createPasteFromCodeAction(final CodeAreaCore codeArea) {
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
        return action;
    }
}
