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
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.bined.BinaryEditorControl;

/**
 * Clipboard code actions.
 *
 * @version 0.2.1 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardCodeActions {

    public static final String COPY_AS_CODE_ACTION_ID = "copyAsCodeAction";
    public static final String PASTE_FROM_CODE_ACTION_ID = "pasteFromCodeAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action copyAsCodeAction;
    private Action pasteFromCodeAction;

    public ClipboardCodeActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getCopyAsCodeAction() {
        if (copyAsCodeAction == null) {
            copyAsCodeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinaryEditorControl) {
                        BinEdComponentPanel activePanel = ((BinaryEditorControl) editorProvider).getComponentPanel();
                        activePanel.performCopyAsCode();
                    }
                }
            };
            ActionUtils.setupAction(copyAsCodeAction, resourceBundle, COPY_AS_CODE_ACTION_ID);
        }
        return copyAsCodeAction;
    }

    @Nonnull
    public Action getPasteFromCodeAction() {
        if (pasteFromCodeAction == null) {
            pasteFromCodeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinaryEditorControl) {
                        BinEdComponentPanel activePanel = ((BinaryEditorControl) editorProvider).getComponentPanel();
                        activePanel.performPasteFromCode();
                    }
                }
            };
            ActionUtils.setupAction(pasteFromCodeAction, resourceBundle, PASTE_FROM_CODE_ACTION_ID);
        }
        return pasteFromCodeAction;
    }

    /*
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
    } */
}
