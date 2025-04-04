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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * Clipboard actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardActions implements ClipboardActionsApi {

    public static final String SELECT_ALL_ACTION_ID = "popupSelectAllAction";
    public static final String DELETE_ACTION_ID = "popupDeleteAction";
    public static final String PASTE_ACTION_ID = "popupPasteAction";
    public static final String COPY_ACTION_ID = "popupCopyAction";
    public static final String CUT_ACTION_ID = "popupCutAction";

    private ResourceBundle resourceBundle;

    public ClipboardActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public Action createCutAction() {
        CutAction cutAction = new CutAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(cutAction, resourceBundle, CUT_ACTION_ID);
        cutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        cutAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, cutAction);
        return cutAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        CopyAction copyAction = new CopyAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(copyAction, resourceBundle, COPY_ACTION_ID);
        copyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        copyAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, copyAction);
        return copyAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        PasteAction pasteAction = new PasteAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(pasteAction, resourceBundle, PASTE_ACTION_ID);
        pasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        pasteAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, pasteAction);
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        DeleteAction deleteAction = new DeleteAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(deleteAction, resourceBundle, DELETE_ACTION_ID);
        deleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, deleteAction);
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        SelectAllAction selectAllAction = new SelectAllAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(selectAllAction, resourceBundle, SELECT_ALL_ACTION_ID);
        selectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        selectAllAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, selectAllAction);
        return selectAllAction;
    }

    @ParametersAreNonnullByDefault
    private static class CutAction extends AbstractAction implements ActionContextChange {

        private ClipboardActionsHandler clipboardActionsHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performCut();
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ClipboardActionsHandler.class, (instance) -> {
                clipboardActionsHandler = instance;
                update();
            });
        }

        public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
            this.clipboardActionsHandler = clipboardActionsHandler;
            update();
        }

        public void update() {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private static class CopyAction extends AbstractAction implements ActionContextChange {

        private ClipboardActionsHandler clipboardActionsHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performCopy();
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ClipboardActionsHandler.class, (instance) -> {
                clipboardActionsHandler = instance;
                update();
            });
        }

        public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
            this.clipboardActionsHandler = clipboardActionsHandler;
            update();
        }

        public void update() {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private static class PasteAction extends AbstractAction implements ActionContextChange {

        private ClipboardActionsHandler clipboardActionsHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performPaste();
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ClipboardActionsHandler.class, (instance) -> {
                clipboardActionsHandler = instance;
                update();
            });
            manager.registerUpdateListener(ClipboardFlavorState.class, (instance) -> {
                update();
            });
        }

        public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
            this.clipboardActionsHandler = clipboardActionsHandler;
            update();
        }

        public void update() {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.canPaste());
        }
    }

    @ParametersAreNonnullByDefault
    private static class DeleteAction extends AbstractAction implements ActionContextChange {

        private ClipboardActionsHandler clipboardActionsHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performDelete();
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ClipboardActionsHandler.class, (instance) -> {
                clipboardActionsHandler = instance;
                update();
            });
        }

        public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
            this.clipboardActionsHandler = clipboardActionsHandler;
            update();
        }

        public void update() {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canDelete() && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private static class SelectAllAction extends AbstractAction implements ActionContextChange {

        private ClipboardActionsHandler clipboardActionsHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performSelectAll();
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ClipboardActionsHandler.class, (instance) -> {
                clipboardActionsHandler = instance;
                update();
            });
        }

        public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
            this.clipboardActionsHandler = clipboardActionsHandler;
            update();
        }

        public void update() {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
        }
    }
}
