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
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Show nonprintables actions.
 *
 * @version 0.2.1 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowNonprintablesActions {

    public static final String VIEW_NONPRINTABLES_ACTION_ID = "viewNonprintablesAction";
    public static final String VIEW_NONPRINTABLES_TOOLBAR_ACTION_ID = "viewNonprintablesToolbarAction";

    private BinaryEditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action viewNonprintablesAction;
    private Action viewNonprintablesToolbarAction;

    public ShowNonprintablesActions() {
    }

    public void setup(XBApplication application, BinaryEditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setShowNonprintables(boolean showNonprintables) {
        editorProvider.setShowNonprintables(showNonprintables);
        viewNonprintablesAction.putValue(Action.SELECTED_KEY, showNonprintables);
        viewNonprintablesToolbarAction.putValue(Action.SELECTED_KEY, showNonprintables);
    }

    @Nonnull
    public Action getViewNonprintablesAction() {
        if (viewNonprintablesAction == null) {
            viewNonprintablesAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setShowNonprintables(!editorProvider.isShowNonprintables());
                }
            };
            ActionUtils.setupAction(viewNonprintablesAction, resourceBundle, VIEW_NONPRINTABLES_ACTION_ID);
            viewNonprintablesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
            viewNonprintablesAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));

        }
        return viewNonprintablesAction;
    }

    @Nonnull
    public Action getViewNonprintablesToolbarAction() {
        if (viewNonprintablesToolbarAction == null) {
            viewNonprintablesToolbarAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setShowNonprintables(!editorProvider.isShowNonprintables());
                }
            };
            ActionUtils.setupAction(viewNonprintablesToolbarAction, resourceBundle, VIEW_NONPRINTABLES_TOOLBAR_ACTION_ID);
            viewNonprintablesToolbarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        }
        return viewNonprintablesToolbarAction;
    }
}