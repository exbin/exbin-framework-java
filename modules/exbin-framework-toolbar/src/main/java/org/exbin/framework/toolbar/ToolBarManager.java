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
package org.exbin.framework.toolbar;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.ContributionSequenceBuilder;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Default toolbar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarManager extends ContributionManager implements ToolBarManagement {

    protected final ContributionSequenceBuilder builder = new ContributionSequenceBuilder();

    public ToolBarManager() {
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition contributionDef = definitions.get(toolBarId);
        builder.buildSequence(new ToolBarSequenceOutput(targetToolBar, actionContextRegistration), contributionDef);
        actionContextRegistration.finish();
    }

    @Override
    public void buildIconToolBar(JToolBar targetToolBar, String toolBarId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition contributionDef = definitions.get(toolBarId);
        builder.buildSequence(new IconToolBarSequenceOutput(targetToolBar, actionContextRegistration), contributionDef);
        actionContextRegistration.finish();
    }

    @Override
    public void registerToolBar(String toolBarId, String moduleId) {
        registerDefinition(toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ActionToolBarContribution registerToolBarItem(String toolBarId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(toolBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + toolBarId + " doesn't exist");
        }

        ActionToolBarContribution toolBarContribution = new ActionToolBarContribution(action);
        definition.addContribution(toolBarContribution);
        return toolBarContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerToolBarGroup(String toolBarId, String moduleId, String groupId) {
        return registerContributionGroup(toolBarId, moduleId, groupId);
    }

    @Override
    public void registerToolBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionToolBarContribution) {
                    actions.add(((ActionToolBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }
}
