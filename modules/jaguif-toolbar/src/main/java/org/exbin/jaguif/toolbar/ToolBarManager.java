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
package org.exbin.jaguif.toolbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToolBar;
import org.exbin.jaguif.contribution.ContributionDefinition;
import org.exbin.jaguif.contribution.ContributionManager;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.contribution.ContributionSequenceBuilder;
import org.exbin.jaguif.toolbar.api.ToolBarManagement;
import org.exbin.jaguif.action.api.ActionContextRegistration;

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

    @Override
    public void registerToolBarContribution(String toolBarId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = definitions.get(toolBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + toolBarId + " doesn't exist");
        }

        definition.addContribution(contribution);
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
}
