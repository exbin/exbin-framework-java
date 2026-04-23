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
package org.exbin.jaguif.sidebar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.sidebar.api.SideBarManagement;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.contribution.api.ContributionDefinition;
import org.exbin.jaguif.contribution.api.ContributionManagement;
import org.exbin.jaguif.contribution.api.ContributionModuleApi;
import org.exbin.jaguif.contribution.api.ContributionSequenceBuilder;
import org.exbin.jaguif.docking.api.SidePanelDocking;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.sidebar.api.SideBarModuleApi;
import org.exbin.jaguif.sidebar.api.SideBar;
import org.exbin.jaguif.frame.api.FrameController;

/**
 * Default sidebar manager.
 */
@ParametersAreNonnullByDefault
public class SideBarManager implements SideBarManagement {

    protected final ContributionSequenceBuilder builder;
    protected final ContributionManagement contributionManagement;

    public SideBarManager() {
        ContributionModuleApi contributionModule = App.getModule(ContributionModuleApi.class);
        contributionManagement = contributionModule.createContributionManager();
        builder = contributionModule.createContributionSequenceBuilder();
    }

    @Override
    public void buildSideBar(SideBar targetSideBar, String sideBarId, ContextRegistration contextRegistration) {
        ContributionDefinition definition = contributionManagement.getDefinition(sideBarId);
        builder.buildSequence(new SideToolBarSequenceOutput(targetSideBar, contextRegistration), definition);
        contextRegistration.finish();
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        contributionManagement.registerDefinition(sideBarId, moduleId);
    }

    @Override
    public void registerSideBarContribution(String sideBarId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = contributionManagement.getDefinition(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerSideBarGroup(String sideBarId, String moduleId, String groupId) {
        return contributionManagement.registerContributionGroup(sideBarId, moduleId, groupId);
    }

    @Override
    public void registerSideBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        contributionManagement.registerContributionRule(contribution, rule);
    }

    @Nonnull
    public SideBar createSideToolBar(SidePanelDocking docking) {
        SideBar sideBar = new DefaultSideBar(docking);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        FrameController frameHandler = frameModule.getFrameController();
        ContextUpdateManagement updateManager = frameHandler.getUpdateManager();
        ActiveContextManagement contextManager = frameHandler.getContextManager();
        updateManager.addGroup("mainSideBar");
        buildSideBar(sideBar, SideBarModuleApi.MAIN_SIDE_BAR_ID, contextModule.createContextRegistrator("mainSideBar", updateManager, contextManager));
        return sideBar;
    }
}
