/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.license;

import com.formdev.flatlaf.extras.FlatDesktop;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.license.action.AboutAction;
import org.exbin.jaguif.license.api.LicenseModuleApi;
import org.exbin.jaguif.license.contribution.AboutContribution;
import org.exbin.jaguif.license.page.BasicInfoPage;
import org.exbin.jaguif.license.page.EnvironmentVariablesPage;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.license.page.ContributorsListPage;
import org.exbin.jaguif.license.page.SingleLicensePage;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;
import org.exbin.jaguif.tabpages.api.TabPagesDefinitionManagement;
import org.exbin.jaguif.tabpages.api.TabPagesModuleApi;

/**
 * License management module.
 */
@ParametersAreNonnullByDefault
public class LicenseModule implements LicenseModuleApi {

    private AboutApplication aboutApplication;

    public LicenseModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    private AboutApplication getAboutApplication() {
        if (aboutApplication == null) {
            aboutApplication = new AboutApplication();
        }

        return aboutApplication;
    }

    @Nonnull
    @Override
    public AboutAction createAboutAction() {
        return getAboutApplication().createAboutAction();
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

        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
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
    public void registerBasicPages() {
        TabPagesModuleApi tabPagesModule = App.getModule(TabPagesModuleApi.class);
        tabPagesModule.getMainTabPagesManager().registerTabPages(ABOUT_PAGES_ID, MODULE_ID);
        TabPagesDefinitionManagement tabPagesDefinition = tabPagesModule.getMainTabPagesDefinition(ABOUT_PAGES_ID, MODULE_ID);
        tabPagesDefinition.registerTabPagesContribution(new ComponentTabPagesContribution() {
            @Nonnull
            @Override
            public TabPagesComponent createComponent() {
                return new BasicInfoPage();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return BasicInfoPage.KEY_ID;
            }
        });
        tabPagesDefinition.registerTabPagesContribution(new ComponentTabPagesContribution() {
            @Nonnull
            @Override
            public TabPagesComponent createComponent() {
                return new SingleLicensePage();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return SingleLicensePage.KEY_ID;
            }
        });
        tabPagesDefinition.registerTabPagesContribution(new ComponentTabPagesContribution() {
            @Nonnull
            @Override
            public TabPagesComponent createComponent() {
                return new ContributorsListPage();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return ContributorsListPage.KEY_ID;
            }
        });
        tabPagesDefinition.registerTabPagesContribution(new ComponentTabPagesContribution() {
            @Nonnull
            @Override
            public TabPagesComponent createComponent() {
                return new EnvironmentVariablesPage();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return EnvironmentVariablesPage.KEY_ID;
            }
        });
    }

    @Override
    public void setAboutDialogSideComponent(JComponent sideComponent) {
        getAboutApplication().setAboutDialogSideComponent(sideComponent);
    }

    @Nonnull
    @Override
    public JComponent createAboutPanel() {
        return getAboutApplication().createAboutPanel();
    }
}
