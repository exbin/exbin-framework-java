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
package org.exbin.framework.about;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.about.action.AboutAction;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.about.api.AboutModuleApi;

/**
 * Implementation of framework about module.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AboutModule implements AboutModuleApi {

    private XBApplication application;
    private AboutAction aboutAction;
    private JComponent sideComponent = null;

    public AboutModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public Action getAboutAction() {
        if (aboutAction == null) {
            aboutAction = new AboutAction();
            aboutAction.setApplication(application);
            aboutAction.setAboutDialogSideComponent(sideComponent);
        }

        return aboutAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.HELP_MENU_ID, new MenuGroup(HELP_ABOUT_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.ABOVE));
        actionModule.registerMenuItem(FrameModuleApi.HELP_MENU_ID, MODULE_ID, getAboutAction(), new MenuPosition(HELP_ABOUT_MENU_GROUP_ID));
    }

    @Override
    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
        if (aboutAction != null) {
            aboutAction.setAboutDialogSideComponent(sideComponent);
        }
    }
}
