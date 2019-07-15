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
package org.exbin.framework.gui.menu;

import java.awt.Event;
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
import org.exbin.framework.gui.menu.api.ClipboardActions;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Basic clipboard action set.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicClipboardActions implements ClipboardActions {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiMenuModule.class);

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
        deleteAction.setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
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
