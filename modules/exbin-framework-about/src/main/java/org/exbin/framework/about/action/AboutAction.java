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
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.CloseControlPanel;

/**
 * About application action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AboutAction extends AbstractAction {

    public static final String ACTION_ID = "aboutAction";

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(AboutAction.class);
    private JComponent sideComponent = null;

    public AboutAction() {
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        AboutPanel aboutPanel = new AboutPanel();
        aboutPanel.setSideComponent(sideComponent);
        CloseControlPanel controlPanel = new CloseControlPanel();
        final DialogWrapper aboutDialog = frameModule.createDialog(aboutPanel, controlPanel);
        ((JDialog) aboutDialog.getWindow()).setTitle(resourceBundle.getString("aboutAction.dialogTitle"));
        controlPanel.setHandler(aboutDialog::close);
        aboutDialog.showCentered((Component) e.getSource());
        aboutDialog.dispose();
    }

    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }
}
