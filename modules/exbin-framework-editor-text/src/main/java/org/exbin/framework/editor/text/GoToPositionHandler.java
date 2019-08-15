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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.panel.TextGoToPanel;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;

/**
 * Go to line handler.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToPositionHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action goToLineAction;

    public GoToPositionHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorTextModule.class);
    }

    public void init() {
        goToLineAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof TextPanel) {
                    final TextPanel activePanel = (TextPanel) editorProvider;
                    final TextGoToPanel goToPanel = new TextGoToPanel();
                    goToPanel.initFocus();
                    goToPanel.setMaxLine(activePanel.getLineCount());
                    goToPanel.setCharPos(1);
                    DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
                    JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, controlPanel);
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    WindowUtils.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle(), controlPanel);
                    frameModule.setDialogTitle(dialog, goToPanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType == DefaultControlHandler.ControlActionType.OK) {
                            activePanel.gotoLine(goToPanel.getLine());
                            activePanel.gotoRelative(goToPanel.getCharPos());
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(frameModule.getFrame());
                }
            }
        };
        ActionUtils.setupAction(goToLineAction, resourceBundle, "goToLineAction");
        goToLineAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, ActionUtils.getMetaMask()));
        goToLineAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getGoToLineAction() {
        return goToLineAction;
    }
}
