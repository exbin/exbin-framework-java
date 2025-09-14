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
package org.exbin.framework.about;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.App;
import org.exbin.framework.about.action.AboutAction;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Implementation of framework about module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AboutModule implements AboutModuleApi {

    private JComponent sideComponent = null;

    public AboutModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public Action createAboutAction() {
        AboutAction aboutAction = new AboutAction();
        if (sideComponent != null) {
            aboutAction.setAboutDialogSideComponent(sideComponent);
        }
        return aboutAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        boolean aboutActionRegistered = false;

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            // From: https://www.formdev.com/flatlaf/macos/
            FlatDesktop.setAboutHandler(() -> {
                createAboutAction().actionPerformed(new ActionEvent(frameModule.getFrame(), 0, ""));
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler((e) -> {
                getAboutAction().actionPerformed(new ActionEvent(frameModule.getFrame(), 0, ""));
            }); */
            aboutActionRegistered = true;
        }

        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(HELP_ABOUT_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(aboutActionRegistered ? SeparationSequenceContributionRule.SeparationMode.NONE : SeparationSequenceContributionRule.SeparationMode.ABOVE));
        if (!aboutActionRegistered) {
            contribution = mgmt.registerMenuItem(createAboutAction());
            mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(HELP_ABOUT_MENU_GROUP_ID));
        }
    }

    @Override
    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }
}
