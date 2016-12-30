/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.text;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.panel.FindTextPanel;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;

/**
 * Find/replace handler.
 *
 * @version 0.2.0 2016/12/30
 * @author ExBin Project (http://exbin.org)
 */
public class FindReplaceHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private Action editFindAction;
    private Action editFindAgainAction;
    private Action editReplaceAction;

    public FindReplaceHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorTextModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        editFindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(false);
            }
        };
        ActionUtils.setupAction(editFindAction, resourceBundle, "editFindAction");
        editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, metaMask));
        editFindAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        editFindAgainAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(false);
            }
        };
        ActionUtils.setupAction(editFindAgainAction, resourceBundle, "editFindAgainAction");
        editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));

        editReplaceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog(true);
            }
        };
        ActionUtils.setupAction(editReplaceAction, resourceBundle, "editReplaceAction");
        editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, metaMask));
        editReplaceAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void showFindDialog(boolean shallReplace) {
        final GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        final FindTextPanel findPanel = new FindTextPanel();
        findPanel.setShallReplace(shallReplace);
        findPanel.setSelected();
        DefaultControlPanel controlPanel = new DefaultControlPanel(findPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(findPanel, controlPanel);
        final JDialog dialog = frameModule.createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, dialogPanel);
        controlPanel.setHandler(new DefaultControlHandler() {
            @Override
            public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                if (actionType == ControlActionType.OK) {
                    if (editorProvider instanceof TextPanel) {
                        ((TextPanel) editorProvider).findText(findPanel);
                    }
                }

                WindowUtils.closeWindow(dialog);
            }
        });
        WindowUtils.addHeaderPanel(dialog, findPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, findPanel.getResourceBundle());
        WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
        dialog.setLocationRelativeTo(frameModule.getFrame());
        dialog.setVisible(true);
    }

    public Action getEditFindAction() {
        return editFindAction;
    }

    public Action getEditFindAgainAction() {
        return editFindAgainAction;
    }

    public Action getEditReplaceAction() {
        return editReplaceAction;
    }
}
