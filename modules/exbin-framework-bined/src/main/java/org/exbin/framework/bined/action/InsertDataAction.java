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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.InsertDataPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;

/**
 * Insert data action.
 *
 * @version 0.2.1 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertDataAction extends AbstractAction {
    
    public static final String ACTION_ID = "insertDataAction";

    private BinaryEditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public InsertDataAction() {

    }
    
    public void setup(XBApplication application, BinaryEditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (editorProvider instanceof BinaryEditorProvider) {
            final BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
            final InsertDataPanel insertDataPanel = new InsertDataPanel();
            DefaultControlPanel controlPanel = new DefaultControlPanel(insertDataPanel.getResourceBundle());
            JPanel dialogPanel = WindowUtils.createDialogPanel(insertDataPanel, controlPanel);
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, editorProvider.getPanel(), "", Dialog.ModalityType.APPLICATION_MODAL);
            frameModule.setDialogTitle(dialog, insertDataPanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == ControlActionType.OK) {
                    insertDataPanel.acceptInput();
                    InsertDataPanel.FillWithType fillWithType = insertDataPanel.getFillWithType();
                    ExtCodeArea codeArea = activePanel.getCodeArea();
                    // TODO
                }

                dialog.close();
                dialog.dispose();
            });
            SwingUtilities.invokeLater(insertDataPanel::initFocus);
            dialog.showCentered(editorProvider.getPanel());
        }
    }
}