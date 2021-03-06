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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.GoToBinaryPanel;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;

/**
 * Go to line handler.
 *
 * @version 0.2.0 2017/01/07
 * @author ExBin Project (http://exbin.org)
 */
public class GoToPositionHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action goToLineAction;

    public GoToPositionHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        goToLineAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    final BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
                    final GoToBinaryPanel goToPanel = new GoToBinaryPanel();
                    goToPanel.setCursorPosition(((CaretCapable) activePanel.getCodeArea()).getCaret().getCaretPosition().getDataPosition());
                    goToPanel.setMaxPosition(activePanel.getCodeArea().getDataSize());
                    DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
                    JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, controlPanel);
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, editorProvider.getPanel(), "", Dialog.ModalityType.APPLICATION_MODAL);
                    WindowUtils.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle());
                    frameModule.setDialogTitle(dialog, goToPanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType == ControlActionType.OK) {
                            goToPanel.acceptInput();
                            activePanel.goToPosition(goToPanel.getTargetPosition());
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    SwingUtilities.invokeLater(goToPanel::initFocus);
                    dialog.showCentered(editorProvider.getPanel());
                }
            }
        };
        ActionUtils.setupAction(goToLineAction, resourceBundle, "goToLineAction");
        goToLineAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, ActionUtils.getMetaMask()));
        goToLineAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getGoToLineAction() {
        return goToLineAction;
    }
}
