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
package org.exbin.framework.deltahex;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.GoToHexPanel;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;

/**
 * Go to line handler.
 *
 * @version 0.2.0 2016/12/29
 * @author ExBin Project (http://exbin.org)
 */
public class GoToPositionHandler {

    private final HexEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

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
                    final HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
                    final GoToHexPanel goToPanel = new GoToHexPanel();
                    goToPanel.initFocus();
                    goToPanel.setCursorPosition(activePanel.getCodeArea().getCaretPosition().getDataPosition());
                    goToPanel.setMaxPosition(activePanel.getCodeArea().getDataSize());
                    DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
                    JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, controlPanel);
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    final JDialog dialog = frameModule.createDialog(dialogPanel);
                    WindowUtils.addHeaderPanel(dialog, goToPanel.getResourceBundle());
                    frameModule.setDialogTitle(dialog, goToPanel.getResourceBundle());
                    controlPanel.setHandler(new DefaultControlHandler() {
                        @Override
                        public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                            if (actionType == ControlActionType.OK) {
                                activePanel.goToPosition(goToPanel.getGoToPosition());
                            }

                            WindowUtils.closeWindow(dialog);
                        }
                    });
                    WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
                    dialog.setLocationRelativeTo(dialog.getParent());
                    dialog.setVisible(true);
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
}
