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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardUtils;

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
    private Component actionFocusOwner = null;

    private JComponent lastFocusOwner = null;
    private boolean isValidClipboardFlavor = false;
    private CaretListener textComponentCaretListener;
    private PropertyChangeListener textComponentPCL;

    private Action cutTextAction;
    private Action copyTextAction;
    private Action pasteTextAction;
    private Action deleteTextAction;
    private Action selectAllTextAction;

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
        if (cutTextAction == null) {
            cutTextAction = new PassingTextAction(new DefaultEditorKit.CutAction());
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(cutTextAction, resourceBundle, EDIT_CUT_ACTION_ID);
            cutTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
            cutTextAction.setEnabled(false);
            actionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), cutTextAction);
        }
        return cutTextAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        if (copyTextAction == null) {
            copyTextAction = new PassingTextAction(new DefaultEditorKit.CopyAction());
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(copyTextAction, resourceBundle, EDIT_COPY_ACTION_ID);
            copyTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
            copyTextAction.setEnabled(false);
            actionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), copyTextAction);
        }
        return copyTextAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        if (pasteTextAction == null) {
            pasteTextAction = new PassingTextAction(new DefaultEditorKit.PasteAction());
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(pasteTextAction, resourceBundle, EDIT_PASTE_ACTION_ID);
            pasteTextAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
            pasteTextAction.setEnabled(false);
            actionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), pasteTextAction);
        }
        return pasteTextAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        if (deleteTextAction == null) {
            deleteTextAction = new PassingTextAction(new TextAction(DELETE_ACTION) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object src = actionFocusOwner;

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
        }
        return deleteTextAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        if (selectAllTextAction == null) {
            selectAllTextAction = new PassingTextAction(new TextAction(SELECT_ALL_ACTION) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object src = actionFocusOwner;

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
        }
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

    /**
     * Called by the KeyboardFocus PropertyChangeListener, before any other
     * focus-change related work is done.
     */
    private void updateFocusOwner(JComponent oldOwner, JComponent newOwner) {
        if (oldOwner instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) oldOwner;
            text.removeCaretListener(textComponentCaretListener);
            text.removePropertyChangeListener(textComponentPCL);
        }
        if (newOwner instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) newOwner;
            // maybeInstallTextActions(text);

            text.addCaretListener(textComponentCaretListener);
            text.addPropertyChangeListener(textComponentPCL);
        } else if (newOwner == null) {
            copyTextAction.setEnabled(false);
            cutTextAction.setEnabled(false);
            pasteTextAction.setEnabled(false);
            deleteTextAction.setEnabled(false);
        }

        lastFocusOwner = newOwner;
    }

    private final class KeyboardFocusPCL implements PropertyChangeListener {

        KeyboardFocusPCL() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            Component oldOwner = getFocusOwner();
            Object newValue = e.getNewValue();
            JComponent newOwner = (newValue instanceof JComponent) ? (JComponent) newValue : null;
            if (oldOwner instanceof JComponent) {
                updateFocusOwner((JComponent) oldOwner, newOwner);
            }

            if (newOwner != null) {
                actionFocusOwner = newOwner;
                /* ActionMap textActionMap = newOwner.getActionMap();
                 if (textActionMap != null) {
                 if (actionMap.get(markerActionKey) == null) {
                 actionFocusOwner = newOwner;
                 }
                 } */

 /*if (newOwner instanceof JTextComponent) {
                 if (((JTextComponent) newOwner).getComponentPopupMenu() == null) {
                 ((JTextComponent) newOwner).setComponentPopupMenu(defaultPopupMenu);
                 }
                 } */
            }
        }

    }

    @Nullable
    private Component getFocusOwner() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    }

    private void updateTextActions(JTextComponent text) {
        Caret caret = text.getCaret();
        boolean selection = (caret.getDot() != caret.getMark());
        // text.getSelectionEnd() > text.getSelectionStart();
        boolean editable = text.isEditable();
        boolean data = isValidClipboardFlavor;
        copyTextAction.setEnabled(selection);
        cutTextAction.setEnabled(editable && selection);
        deleteTextAction.setEnabled(editable && selection);
        pasteTextAction.setEnabled(editable && data);
    }

    private final class ClipboardListener implements FlavorListener {

        @Override
        public void flavorsChanged(FlavorEvent e) {
            JComponent c = (JComponent) getFocusOwner();
            if (c instanceof JTextComponent) {
                isValidClipboardFlavor = ClipboardUtils.getClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
                updateTextActions((JTextComponent) c);
            }
        }
    }

    private final class TextComponentCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            updateTextActions((JTextComponent) (e.getSource()));
        }
    }

    private final class TextComponentPCL implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ((propertyName == null) || "editable".equals(propertyName)) {
                updateTextActions((JTextComponent) (e.getSource()));
            }
        }
    }
}
