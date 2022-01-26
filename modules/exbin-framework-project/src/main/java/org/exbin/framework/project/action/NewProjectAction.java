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
package org.exbin.framework.project.action;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.project.api.ProjectModuleApi;
import org.exbin.framework.project.gui.NewProjectPanel;
import org.exbin.framework.project.model.ProjectTreeModel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * New project action.
 *
 * @version 0.2.2 2022/01/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class NewProjectAction extends AbstractAction {

    public static final String ACTION_ID = "newProjectAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;

    public NewProjectAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, ActionUtils.getMetaMask() | InputEvent.SHIFT_DOWN_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectModuleApi projectModule = application.getModuleRepository().getModuleByInterface(ProjectModuleApi.class);

        NewProjectPanel newProjectPanel = new NewProjectPanel();
        DefaultControlPanel controlPanel = new DefaultControlPanel(newProjectPanel.getResourceBundle());
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        Frame parentFrame = frameModule.getFrame();
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(parentFrame, Dialog.ModalityType.APPLICATION_MODAL, newProjectPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), newProjectPanel.getClass(), newProjectPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, newProjectPanel.getResourceBundle());

        ProjectTreeModel projectTreeModel = new ProjectTreeModel(projectModule.getProjectCategories());
        newProjectPanel.setCategoryModel(projectTreeModel);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                // projectModule.createNewProject(projectTreeModel.getSelectedType());
                throw new UnsupportedOperationException("Not supported yet.");
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(parentFrame);
    }
}
