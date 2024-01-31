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
package org.exbin.framework.about.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import org.exbin.framework.App;
import org.exbin.framework.about.gui.AboutPanel;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.CloseControlPanel;

/**
 * About application action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AboutAction extends AbstractAction {

    public static final String ACTION_ID = "aboutAction";

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AboutAction.class);
    private JComponent sideComponent = null;

    public AboutAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        AboutPanel aboutPanel = new AboutPanel();
        aboutPanel.setSideComponent(sideComponent);
        aboutPanel.loadFromApplication();
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowHandler aboutDialog = windowModule.createDialog(aboutPanel, controlPanel);
        ((JDialog) aboutDialog.getWindow()).setTitle(resourceBundle.getString("aboutAction.dialogTitle"));
        controlPanel.setHandler(aboutDialog::close);
        aboutDialog.showCentered((Component) e.getSource());
        aboutDialog.dispose();
    }

    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }
}
