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
package org.exbin.framework.bined.handler;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * View nonprintables handler.
 *
 * @version 0.2.1 2019/08/18
 * @author ExBin Project (http://exbin.org)
 */
public class ShowNonprintablesHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action viewNonprintablesAction;
    private Action viewNonprintablesToolbarAction;

    public ShowNonprintablesHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        viewNonprintablesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setShowNonprintables(!editorProvider.isShowNonprintables());
            }
        };
        ActionUtils.setupAction(viewNonprintablesAction, resourceBundle, "viewNonprintablesAction");
        viewNonprintablesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        viewNonprintablesAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));

        viewNonprintablesToolbarAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setShowNonprintables(!editorProvider.isShowNonprintables());
            }
        };
        ActionUtils.setupAction(viewNonprintablesToolbarAction, resourceBundle, "viewNonprintablesToolbarAction");
        viewNonprintablesToolbarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
    }

    public void setShowNonprintables(boolean showNonprintables) {
        editorProvider.setShowNonprintables(showNonprintables);
        viewNonprintablesAction.putValue(Action.SELECTED_KEY, showNonprintables);
        viewNonprintablesToolbarAction.putValue(Action.SELECTED_KEY, showNonprintables);
    }

    @Nonnull
    public Action getViewNonprintablesAction() {
        return viewNonprintablesAction;
    }

    @Nonnull
    public Action getViewNonprintablesToolbarAction() {
        return viewNonprintablesToolbarAction;
    }
}
