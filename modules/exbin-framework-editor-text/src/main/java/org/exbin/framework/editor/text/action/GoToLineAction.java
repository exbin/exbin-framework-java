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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.text.gui.TextGoToPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.WindowHandler;

/**
 * Go to line action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToLineAction extends AbstractAction {

    public static final String ACTION_ID = "goToLineAction";

    private Component component;

    public GoToLineAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(Component.class, (instance) -> {
                    component = instance;
                    setEnabled(component instanceof TextPanel);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(component instanceof TextPanel)) {
            return;
        }

        final TextPanel textPanel = (TextPanel) component;
        final TextGoToPanel goToPanel = new TextGoToPanel();
        goToPanel.initFocus();
        goToPanel.setMaxLine(textPanel.getLineCount());
        goToPanel.setCharPos(1);
        DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(goToPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, goToPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                textPanel.gotoLine(goToPanel.getLine());
                textPanel.gotoRelative(goToPanel.getCharPos());
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(textPanel);
    }
}
