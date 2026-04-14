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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.ContributionDefinition;
import org.exbin.jaguif.sidebar.api.ActionSideBarContribution;
import org.exbin.jaguif.contribution.ContributionManager;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.contribution.ContributionSequenceBuilder;
import org.exbin.jaguif.sidebar.api.SideBarManagement;
import org.exbin.jaguif.action.api.ActionContextRegistration;
import org.exbin.jaguif.action.api.ActionManagement;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.docking.api.SidePanelDocking;
import org.exbin.jaguif.frame.api.ComponentFrame;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.sidebar.api.ComponentSideBarContribution;
import org.exbin.jaguif.sidebar.api.SideBarComponent;
import org.exbin.jaguif.sidebar.api.SideBarModuleApi;
import org.exbin.jaguif.sidebar.api.SideBar;

/**
 * Default sidebar manager.
 */
@ParametersAreNonnullByDefault
public class SideBarManager extends ContributionManager implements SideBarManagement {

    protected final ContributionSequenceBuilder builder = new ContributionSequenceBuilder();

    public SideBarManager() {
    }

    @Override
    public void buildSideBar(SideBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition definition = definitions.get(sideBarId);
        builder.buildSequence(new SideToolBarSequenceOutput(targetSideBar, actionContextRegistration), definition);
        actionContextRegistration.finish();
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        registerDefinition(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public ActionSideBarContribution registerSideBarAction(String sideBarId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        definition.addContribution(sideBarContribution);
        return sideBarContribution;
    }

    @Override
    public void registerSideBarContribution(String sideBarId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = definitions.get(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerSideBarGroup(String sideBarId, String moduleId, String groupId) {
        return registerContributionGroup(sideBarId, moduleId, groupId);
    }

    @Override
    public void registerSideBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionSideBarContribution) {
                    actions.add(((ActionSideBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @Nonnull
    public SideBar createSideToolBar(SidePanelDocking docking) {
        SideBar sideBar = new DefaultSideBar(docking);
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ComponentFrame frameHandler = frameModule.getFrameHandler();
        ActionManagement actionManager = frameHandler.getActionManager();
        buildSideBar(sideBar, SideBarModuleApi.MAIN_SIDE_BAR_ID, actionModule.createActionContextRegistrar(actionManager));
        return sideBar;
    }
}
