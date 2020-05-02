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
package org.exbin.framework.gui.about;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.about.api.GuiAboutModuleApi;
import org.exbin.framework.gui.about.gui.AboutPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework about module.
 *
 * @version 0.2.0 2019/08/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiAboutModule implements GuiAboutModuleApi {

    private XBApplication application;
    private java.util.ResourceBundle resourceBundle = null;
    private Action aboutAction;
    private JComponent sideComponent = null;

    public GuiAboutModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiAboutModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    @Override
    public Action getAboutAction() {
        if (aboutAction == null) {
            getResourceBundle();
            aboutAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    AboutPanel aboutPanel = new AboutPanel();
                    aboutPanel.setApplication(application);
                    aboutPanel.setSideComponent(sideComponent);
                    CloseControlPanel controlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(aboutPanel, controlPanel);
                    final DialogWrapper aboutDialog = frameModule.createDialog(dialogPanel);
                    ((JDialog) aboutDialog.getWindow()).setTitle("About");
                    controlPanel.setHandler(aboutDialog::close);
                    aboutDialog.showCentered((Component) e.getSource());
                    aboutDialog.dispose();
                }
            };
            ActionUtils.setupAction(aboutAction, resourceBundle, "aboutAction");
            aboutAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }

        return aboutAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.HELP_MENU_ID, new MenuGroup(HELP_ABOUT_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.ABOVE));
        menuModule.registerMenuItem(GuiFrameModuleApi.HELP_MENU_ID, MODULE_ID, getAboutAction(), new MenuPosition(HELP_ABOUT_MENU_GROUP_ID));
    }

    @Override
    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }
}
