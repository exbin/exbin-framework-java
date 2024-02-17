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
package org.exbin.framework.editor.text.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.text.TextEditor;
import org.exbin.framework.editor.text.gui.FindTextPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.editor.text.service.TextSearchService;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowHandler;

/**
 * Find/replace actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    public static final String EDIT_FIND_ACTION_ID = "editFindAction";
    public static final String EDIT_FIND_AGAIN_ACTION_ID = "editFindAgainAction";
    public static final String EDIT_REPLACE_ACTION_ID = "editReplaceAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void showFindDialog(boolean shallReplace) {
        final WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        final FindTextPanel findPanel = new FindTextPanel();
        findPanel.setShallReplace(shallReplace);
        findPanel.setSelected();
        DefaultControlPanel controlPanel = new DefaultControlPanel(findPanel.getResourceBundle());
        final WindowHandler dialog = windowModule.createDialog(findPanel, controlPanel);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                if (editorProvider instanceof TextEditor) {
                    TextSearchService.FindTextParameters findTextParameters = new TextSearchService.FindTextParameters();
                    findTextParameters.setFindText(findPanel.getFindText());
                    findTextParameters.setSearchFromStart(findPanel.isSearchFromStart());
                    findTextParameters.setShallReplace(findPanel.isShallReplace());
                    findTextParameters.setReplaceText(findPanel.getReplaceText());

                    ((TextEditor) editorProvider).getEditorComponent().findText(findTextParameters);
                }
            }

            dialog.close();
            dialog.dispose();
        });
        windowModule.addHeaderPanel(dialog.getWindow(), findPanel.getClass(), findPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, findPanel.getResourceBundle());
        dialog.showCentered(frameModule.getFrame());
    }

    @Nonnull
    public Action createEditFindAction() {
        AbstractAction editFindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(false);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAction, resourceBundle, EDIT_FIND_ACTION_ID);
        editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
        editFindAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        return editFindAction;
    }

    @Nonnull
    public Action createEditFindAgainAction() {
        AbstractAction editFindAgainAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(false);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAgainAction, resourceBundle, EDIT_FIND_AGAIN_ACTION_ID);
        editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        return editFindAgainAction;
    }

    @Nonnull
    public Action createEditReplaceAction() {
        AbstractAction editReplaceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(true);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editReplaceAction, resourceBundle, EDIT_REPLACE_ACTION_ID);
        editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
        editReplaceAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        return editReplaceAction;
    }
}
