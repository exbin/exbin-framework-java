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
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Find/replace actions.
 *
 * @version 0.2.0 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    public static final String EDIT_FIND_ACTION_ID = "editFindAction";
    public static final String EDIT_FIND_AGAIN_ACTION_ID = "editFindAgainAction";
    public static final String EDIT_REPLACE_ACTION_ID = "editReplaceAction";

    private BinaryEditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action editFindAction;
    private Action editFindAgainAction;
    private Action editReplaceAction;

    public FindReplaceActions() {
    }

    public void setup(XBApplication application, BinaryEditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getEditFindAction() {
        if (editFindAction == null) {
            editFindAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinaryEditorProvider) {
                        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                        activePanel.showSearchPanel(false);
                    }
                }
            };
            ActionUtils.setupAction(editFindAction, resourceBundle, EDIT_FIND_ACTION_ID);
            editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
            editFindAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return editFindAction;
    }

    @Nonnull
    public Action getEditFindAgainAction() {
        if (editFindAgainAction == null) {
            editFindAgainAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                    activePanel.findAgain();
                }
            };
            ActionUtils.setupAction(editFindAgainAction, resourceBundle, EDIT_FIND_AGAIN_ACTION_ID);
            editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        }
        return editFindAgainAction;
    }

    @Nonnull
    public Action getEditReplaceAction() {
        if (editReplaceAction == null) {
            editReplaceAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinaryEditorProvider) {
                        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                        activePanel.showSearchPanel(true);
                    }
                }
            };
            ActionUtils.setupAction(editReplaceAction, resourceBundle, EDIT_REPLACE_ACTION_ID);
            editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            editReplaceAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return editReplaceAction;
    }
}
