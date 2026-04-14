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
package org.exbin.jaguif.about;

import com.formdev.flatlaf.extras.FlatDesktop;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.about.action.AboutAction;
import org.exbin.jaguif.about.api.AboutModuleApi;
import org.exbin.jaguif.about.contribution.AboutContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;

/**
 * Implementation of framework about module.
 */
@ParametersAreNonnullByDefault
public class AboutModule implements AboutModuleApi {

    protected JComponent sideComponent = null;

    public AboutModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public AboutAction createAboutAction() {
        AboutAction aboutAction = new AboutAction();
        if (sideComponent != null) {
            aboutAction.setAboutDialogSideComponent(sideComponent);
        }
        return aboutAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        boolean aboutActionRegistered = false;

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            // From: https://www.formdev.com/flatlaf/macos/
            FlatDesktop.setAboutHandler(() -> {
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                createAboutAction().openAboutDialog(frameModule.getFrame());
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler((e) -> {
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                getAboutAction().openAboutDialog(frameModule.getFrame());
            }); */
            aboutActionRegistered = true;
        }

        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(HELP_ABOUT_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(aboutActionRegistered ? SeparationSequenceContributionRule.SeparationMode.NONE : SeparationSequenceContributionRule.SeparationMode.ABOVE));
        if (!aboutActionRegistered) {
            contribution = new AboutContribution();
            mgmt.registerMenuContribution(contribution);
            mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(HELP_ABOUT_MENU_GROUP_ID));
        }
    }

    @Override
    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }
}
