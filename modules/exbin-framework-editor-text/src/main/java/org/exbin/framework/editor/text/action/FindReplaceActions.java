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
package org.exbin.framework.editor.text.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.gui.FindTextPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.editor.text.service.TextSearchService;

/**
 * Find/replace actions.
 *
 * @version 0.2.1 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    public static final String EDIT_FIND_ACTION_ID = "editFindAction";
    public static final String EDIT_FIND_AGAIN_ACTION_ID = "editFindAgainAction";
    public static final String EDIT_REPLACE_ACTION_ID = "editReplaceAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action editFindAction;
    private Action editFindAgainAction;
    private Action editReplaceAction;

    public FindReplaceActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void showFindDialog(boolean shallReplace) {
        final GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        final FindTextPanel findPanel = new FindTextPanel();
        findPanel.setShallReplace(shallReplace);
        findPanel.setSelected();
        DefaultControlPanel controlPanel = new DefaultControlPanel(findPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(findPanel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, dialogPanel);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                if (editorProvider instanceof TextPanel) {
                    TextSearchService.FindTextParameters findTextParameters = new TextSearchService.FindTextParameters();
                    findTextParameters.setFindText(findPanel.getFindText());
                    findTextParameters.setSearchFromStart(findPanel.isSearchFromStart());
                    findTextParameters.setShallReplace(findPanel.isShallReplace());
                    findTextParameters.setReplaceText(findPanel.getReplaceText());

                    ((TextPanel) editorProvider).findText(findTextParameters);
                }
            }

            dialog.close();
            dialog.dispose();
        });
        WindowUtils.addHeaderPanel(dialog.getWindow(), findPanel.getClass(), findPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, findPanel.getResourceBundle());
        dialog.showCentered(frameModule.getFrame());
    }

    @Nonnull
    public Action getEditFindAction() {
        if (editFindAction == null) {
            editFindAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showFindDialog(false);
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
                    showFindDialog(false);
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
                    showFindDialog(true);
                }
            };
            ActionUtils.setupAction(editReplaceAction, resourceBundle, EDIT_REPLACE_ACTION_ID);
            editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            editReplaceAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return editReplaceAction;
    }
}