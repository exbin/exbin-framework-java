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
package org.exbin.framework.action;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsUpdater;
import org.exbin.framework.utils.ClipboardUtils;

/**
 * Clipboard operations.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardActions implements ClipboardActionsUpdater {

    public static final String EDIT_SELECT_ALL_ACTION_ID = "popupSelectAllAction";
    public static final String EDIT_DELETE_ACTION_ID = "popupDeleteAction";
    public static final String EDIT_PASTE_ACTION_ID = "popupPasteAction";
    public static final String EDIT_COPY_ACTION_ID = "popupCopyAction";
    public static final String EDIT_CUT_ACTION_ID = "popupCutAction";

    private ResourceBundle resourceBundle;

    private ClipboardActionsHandler clipboardActionsHandler = null;
    private FlavorListener clipboardFlavorListener;

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action deleteAction;
    private Action selectAllAction;

    public ClipboardActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public Action getCutAction() {
        if (cutAction == null) {
            cutAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clipboardActionsHandler != null) {
                        clipboardActionsHandler.performCut();
                    }
                }
            };
            ActionUtils.setupAction(cutAction, resourceBundle, EDIT_CUT_ACTION_ID);
            cutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
            cutAction.setEnabled(false);
        }
        return cutAction;
    }

    @Nonnull
    @Override
    public Action getCopyAction() {
        if (copyAction == null) {
            copyAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clipboardActionsHandler != null) {
                        clipboardActionsHandler.performCopy();
                    }
                }
            };
            ActionUtils.setupAction(copyAction, resourceBundle, EDIT_COPY_ACTION_ID);
            copyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
            copyAction.setEnabled(false);
        }
        return copyAction;
    }

    @Nonnull
    @Override
    public Action getPasteAction() {
        if (pasteAction == null) {
            pasteAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clipboardActionsHandler != null) {
                        clipboardActionsHandler.performPaste();
                    }
                }
            };
            ActionUtils.setupAction(pasteAction, resourceBundle, EDIT_PASTE_ACTION_ID);
            pasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
            pasteAction.setEnabled(false);
        }
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clipboardActionsHandler != null) {
                        clipboardActionsHandler.performDelete();
                    }
                }
            };
            ActionUtils.setupAction(deleteAction, resourceBundle, EDIT_DELETE_ACTION_ID);
            deleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
            deleteAction.setEnabled(false);
        }
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action getSelectAllAction() {
        if (selectAllAction == null) {
            selectAllAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clipboardActionsHandler != null) {
                        clipboardActionsHandler.performSelectAll();
                    }
                }
            };
            ActionUtils.setupAction(selectAllAction, resourceBundle, EDIT_SELECT_ALL_ACTION_ID);
            selectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        }
        return selectAllAction;
    }

    @Override
    public void setClipboardActionsHandler(ClipboardActionsHandler clipboardActionsHandler) {
        this.clipboardActionsHandler = clipboardActionsHandler;
        updateClipboardActions();
    }

    @Override
    public void updateClipboardActions() {
        if (cutAction != null) {
            cutAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
        }
        if (copyAction != null) {
            copyAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
        }
        if (pasteAction != null) {
            pasteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.canPaste());
        }
        if (deleteAction != null) {
            deleteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canDelete() && clipboardActionsHandler.isSelection());
        }
        if (selectAllAction != null) {
            selectAllAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
        }
    }

    public void registerClipboardListener() {
        Clipboard clipboard = ClipboardUtils.getClipboard();
        clipboardFlavorListener = (FlavorEvent e) -> {
            updateClipboardActions();
        };
        clipboard.addFlavorListener(clipboardFlavorListener);
    }

    public void unregisterClipboardListener() {
        Clipboard clipboard = ClipboardUtils.getClipboard();
        clipboard.removeFlavorListener(clipboardFlavorListener);
        clipboardFlavorListener = null;
    }
}
