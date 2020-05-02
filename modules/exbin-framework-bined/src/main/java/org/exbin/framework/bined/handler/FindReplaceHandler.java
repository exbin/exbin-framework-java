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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Find/replace handler.
 *
 * @version 0.2.0 2016/12/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action editFindAction;
    private Action editFindAgainAction;
    private Action editReplaceAction;

    public FindReplaceHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        editFindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                    activePanel.showSearchPanel(false);
                }
            }
        };
        ActionUtils.setupAction(editFindAction, resourceBundle, "editFindAction");
        editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
        editFindAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        editFindAgainAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                activePanel.findAgain();
            }
        };
        ActionUtils.setupAction(editFindAgainAction, resourceBundle, "editFindAgainAction");
        editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));

        editReplaceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                    activePanel.showSearchPanel(true);
                }
            }
        };
        ActionUtils.setupAction(editReplaceAction, resourceBundle, "editReplaceAction");
        editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
        editReplaceAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getEditFindAction() {
        return editFindAction;
    }

    @Nonnull
    public Action getEditFindAgainAction() {
        return editFindAgainAction;
    }

    @Nonnull
    public Action getEditReplaceAction() {
        return editReplaceAction;
    }
}
