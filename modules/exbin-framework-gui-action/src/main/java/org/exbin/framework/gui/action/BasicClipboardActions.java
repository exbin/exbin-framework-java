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
package org.exbin.framework.gui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.ClipboardActionsUpdater;

/**
 * Basic clipboard action set.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicClipboardActions implements ClipboardActionsUpdater {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiActionModule.class);

    private ClipboardActionsHandler clipboardActionsHandler = null;

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action deleteAction;
    private Action selectAllAction;

    public BasicClipboardActions() {
        this(null);
    }

    public BasicClipboardActions(@Nullable ClipboardActionsHandler handler) {
        this.clipboardActionsHandler = handler;

        cutAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clipboardActionsHandler != null) {
                    clipboardActionsHandler.performCut();
                }
            }
        };
        ActionUtils.setupAction(cutAction, resourceBundle, "editCutAction");
        cutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        cutAction.setEnabled(false);

        copyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clipboardActionsHandler != null) {
                    clipboardActionsHandler.performCopy();
                }
            }
        };
        ActionUtils.setupAction(copyAction, resourceBundle, "editCopyAction");
        copyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        copyAction.setEnabled(false);

        pasteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clipboardActionsHandler != null) {
                    clipboardActionsHandler.performPaste();
                }
            }
        };
        ActionUtils.setupAction(pasteAction, resourceBundle, "editPasteAction");
        pasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        pasteAction.setEnabled(false);

        deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clipboardActionsHandler != null) {
                    clipboardActionsHandler.performDelete();
                }
            }
        };
        ActionUtils.setupAction(deleteAction, resourceBundle, "editDeleteAction");
        deleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteAction.setEnabled(false);

        selectAllAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clipboardActionsHandler != null) {
                    clipboardActionsHandler.performSelectAll();
                }
            }
        };
        ActionUtils.setupAction(selectAllAction, resourceBundle, "editSelectAllAction");
        selectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));

        changeClipboardActionsHandler();

        Clipboard clipboard;
        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (SecurityException e) {
            clipboard = new Clipboard("sandbox");
        }
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActions();
        });
    }

    @Override
    public void updateClipboardActions() {
        cutAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
        copyAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
        pasteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.canPaste());
        deleteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canDelete() && clipboardActionsHandler.isSelection());
        selectAllAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
    }

    @Override
    public void setClipboardActionsHandler(ClipboardActionsHandler clipboardHandler) {
        this.clipboardActionsHandler = clipboardHandler;
        changeClipboardActionsHandler();
    }

    private void changeClipboardActionsHandler() {
        if (clipboardActionsHandler != null) {
            clipboardActionsHandler.setUpdateListener(this::updateClipboardActions);
        }
    }

    @Nonnull
    @Override
    public Action getCutAction() {
        return cutAction;
    }

    @Nonnull
    @Override
    public Action getCopyAction() {
        return copyAction;
    }

    @Nonnull
    @Override
    public Action getPasteAction() {
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action getDeleteAction() {
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action getSelectAllAction() {
        return selectAllAction;
    }
}
