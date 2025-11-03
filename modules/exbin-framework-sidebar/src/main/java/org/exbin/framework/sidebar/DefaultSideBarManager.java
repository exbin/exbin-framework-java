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
package org.exbin.framework.sidebar;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.sidebar.api.SideBarManager;
import org.exbin.framework.action.api.ActionContextManager;
import org.exbin.framework.contribution.ContributionSequenceBuilder;

/**
 * Sidebar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSideBarManager extends ContributionManager implements SideBarManager {

    protected final ContributionSequenceBuilder builder = new ContributionSequenceBuilder();

    public DefaultSideBarManager() {
    }

    @Override
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextManager actionContextManager) {
        ContributionDefinition definition = definitions.get(sideBarId);
        builder.buildSequence(new SideToolBarSequenceOutput(targetSideBar, actionContextManager), definition);
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        registerDefinition(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public ActionSideBarContribution registerSideBarItem(String sideBarId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        definition.addContribution(sideBarContribution);
        return sideBarContribution;
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
}
