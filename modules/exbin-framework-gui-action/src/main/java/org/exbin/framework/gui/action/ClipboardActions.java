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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.gui.utils.ClipboardActionsApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Clipboard operations.
 *
 * @version 0.2.2 2021/10/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardActions implements ClipboardActionsApi {

    public static final String EDIT_SELECT_ALL_ACTION_ID = "editSelectAllAction";
    public static final String EDIT_DELETE_ACTION_ID = "editDeleteAction";
    public static final String EDIT_PASTE_ACTION_ID = "editPasteAction";
    public static final String EDIT_COPY_ACTION_ID = "editCopyAction";
    public static final String EDIT_CUT_ACTION_ID = "editCutAction";

    private ResourceBundle resourceBundle;

    private ClipboardActionsHandler clipboardActionsHandler = null;

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

    public void setClipboardActionsHandler(ClipboardActionsHandler clipboardActionsHandler) {
        this.clipboardActionsHandler = clipboardActionsHandler;
        updateClipboardActions();
    }

    public void updateClipboardActions() {
        cutAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
        copyAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
        pasteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.canPaste());
        deleteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canDelete() && clipboardActionsHandler.isSelection());
        selectAllAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
    }
}
