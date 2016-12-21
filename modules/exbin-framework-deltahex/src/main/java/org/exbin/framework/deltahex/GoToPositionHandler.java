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
package org.exbin.framework.deltahex;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.dialog.GoToHexDialog;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Go to line handler.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
public class GoToPositionHandler {

    private final HexEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private GoToHexDialog goToDialog = null;

    private Action goToLineAction;

    public GoToPositionHandler(XBApplication application, HexEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        goToLineAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
                    HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
                    initGotoDialog();
                    goToDialog.setCursorPosition(activePanel.getCodeArea().getCaretPosition().getDataPosition());
                    goToDialog.setMaxPosition(activePanel.getCodeArea().getDataSize());
                    goToDialog.setLocationRelativeTo(goToDialog.getParent());
                    goToDialog.setVisible(true);
                    if (goToDialog.getDialogOption() == JOptionPane.OK_OPTION) {
                        activePanel.goToPosition(goToDialog.getGoToPosition());
                    }
                }
            }
        };
        ActionUtils.setupAction(goToLineAction, resourceBundle, "goToLineAction");
        goToLineAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, metaMask));
        goToLineAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getGoToLineAction() {
        return goToLineAction;
    }

    private void initGotoDialog() {
        if (goToDialog == null) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            goToDialog = new GoToHexDialog(frameModule.getFrame(), true);
            goToDialog.setIconImage(application.getApplicationIcon());
            goToDialog.initFocus();
        }
    }
}
