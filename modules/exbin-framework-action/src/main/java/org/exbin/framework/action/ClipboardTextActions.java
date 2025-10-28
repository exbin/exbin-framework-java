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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.utils.ActionUtils;

/**
 * Clipboard operations.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardTextActions implements ClipboardActionsApi {

    public static final String EDIT_SELECT_ALL_ACTION_ID = "editSelectAllAction";
    public static final String EDIT_DELETE_ACTION_ID = "editDeleteAction";
    public static final String EDIT_PASTE_ACTION_ID = "editPasteAction";
    public static final String EDIT_COPY_ACTION_ID = "editCopyAction";
    public static final String EDIT_CUT_ACTION_ID = "editCutAction";

    public static final String DELETE_ACTION = "delete";
    public static final String SELECT_ALL_ACTION = "selectAll";

    private ResourceBundle resourceBundle;
    private ActionMap actionMap;

    public ClipboardTextActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        initializeTextActions();
    }

    private void initializeTextActions() {
        actionMap = new ActionMap();
    }

    public void performCut(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            ActionUtils.invokeTextAction((JTextComponent) src, DefaultEditorKit.cutAction);
        }
    }

    public void performCopy(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            ActionUtils.invokeTextAction((JTextComponent) src, DefaultEditorKit.copyAction);
        }
    }

    public void performPaste(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            ActionUtils.invokeTextAction((JTextComponent) src, DefaultEditorKit.pasteAction);
        }
    }

    @Nonnull
    @Override
    public Action createCutAction() {
        PassingTextAction cutTextAction = new PassingTextAction(new DefaultEditorKit.CutAction());
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(cutTextAction, resourceBundle, EDIT_CUT_ACTION_ID);
        cutTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        cutTextAction.setEnabled(false);
        actionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), cutTextAction);
        return cutTextAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        PassingTextAction copyTextAction = new PassingTextAction(new DefaultEditorKit.CopyAction());
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(copyTextAction, resourceBundle, EDIT_COPY_ACTION_ID);
        copyTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        copyTextAction.setEnabled(false);
        actionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), copyTextAction);
        return copyTextAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        PassingTextAction pasteTextAction = new PassingTextAction(new DefaultEditorKit.PasteAction());
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(pasteTextAction, resourceBundle, EDIT_PASTE_ACTION_ID);
        pasteTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        pasteTextAction.setEnabled(false);
        actionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), pasteTextAction);
        return pasteTextAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        PassingTextAction deleteTextAction = new PassingTextAction(new TextAction(DELETE_ACTION) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();

                if (src instanceof JTextComponent) {
                    ActionUtils.invokeTextAction((JTextComponent) src, DefaultEditorKit.deleteNextCharAction);
                }
            }
        });
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(deleteTextAction, resourceBundle, EDIT_DELETE_ACTION_ID);
        deleteTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteTextAction.setEnabled(false);
        actionMap.put(DELETE_ACTION, deleteTextAction);
        return deleteTextAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        PassingTextAction selectAllTextAction = new PassingTextAction(new TextAction(SELECT_ALL_ACTION) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();

                if (src instanceof JTextComponent) {
                    SwingUtilities.invokeLater(() -> {
                        JTextComponent txtComp = (JTextComponent) src;
                        txtComp.requestFocus();
                        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.selectAllAction);
                        int docLength = txtComp.getDocument().getLength();
                        if (txtComp.getSelectionStart() > 0 || txtComp.getSelectionEnd() != docLength) {
                            txtComp.selectAll();
                        }
                    });
                }
            }
        });
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(selectAllTextAction, resourceBundle, EDIT_SELECT_ALL_ACTION_ID);
        selectAllTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        return selectAllTextAction;
    }

    @ParametersAreNonnullByDefault
    public class PassingTextAction extends TextAction {

        private final TextAction parentAction;

        public PassingTextAction(TextAction parentAction) {
            super((String) parentAction.getValue(Action.NAME));
            this.parentAction = parentAction;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            parentAction.actionPerformed(actionEvent);
        }
    }
}
