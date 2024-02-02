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
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
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
        actionModule.initAction(cutAction, resourceBundle, EDIT_CUT_ACTION_ID);
        cutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        cutAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, cutAction);
        return cutAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        CopyAction copyAction = new CopyAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(copyAction, resourceBundle, EDIT_COPY_ACTION_ID);
        copyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        copyAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, copyAction);
        return copyAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        PasteAction pasteAction = new PasteAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(pasteAction, resourceBundle, EDIT_PASTE_ACTION_ID);
        pasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        pasteAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, pasteAction);
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        DeleteAction deleteAction = new DeleteAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(deleteAction, resourceBundle, EDIT_DELETE_ACTION_ID);
        deleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, deleteAction);
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        SelectAllAction selectAllAction = new SelectAllAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(selectAllAction, resourceBundle, EDIT_SELECT_ALL_ACTION_ID);
        selectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        selectAllAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, selectAllAction);
        return selectAllAction;
    }

    @Override
    public void setClipboardActionsHandler(ClipboardActionsHandler clipboardActionsHandler) {
        this.clipboardActionsHandler = clipboardActionsHandler;
        // TODO updateClipboardActions();
    }

    public void registerClipboardListener() {
        Clipboard clipboard = ClipboardUtils.getClipboard();
        clipboardFlavorListener = (FlavorEvent e) -> {
            // TODO updateClipboardActions();
        };
        clipboard.addFlavorListener(clipboardFlavorListener);
    }

    public void unregisterClipboardListener() {
        Clipboard clipboard = ClipboardUtils.getClipboard();
        clipboard.removeFlavorListener(clipboardFlavorListener);
        clipboardFlavorListener = null;
    }
    
    @ParametersAreNonnullByDefault
    private class CutAction extends AbstractAction implements ActionActiveComponent {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performCut();
            }
        }

        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(ClipboardActionsHandler.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private class CopyAction extends AbstractAction implements ActionActiveComponent {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performCopy();
            }
        }

        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(ClipboardActionsHandler.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private class PasteAction extends AbstractAction implements ActionActiveComponent {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performPaste();
            }
        }

        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(ClipboardActionsHandler.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isEditable() && clipboardActionsHandler.canPaste());
        }
    }

    @ParametersAreNonnullByDefault
    private class DeleteAction extends AbstractAction implements ActionActiveComponent {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performDelete();
            }
        }

        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(ClipboardActionsHandler.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canDelete() && clipboardActionsHandler.isSelection());
        }
    }

    @ParametersAreNonnullByDefault
    private class SelectAllAction extends AbstractAction implements ActionActiveComponent {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clipboardActionsHandler != null) {
                clipboardActionsHandler.performSelectAll();
            }
        }

        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(ClipboardActionsHandler.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
        }
    }
}
