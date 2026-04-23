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
package org.exbin.jaguif.toolbar;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToolBar;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.toolbar.api.ToolBarManagement;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.ContributionDefinition;
import org.exbin.jaguif.contribution.api.ContributionManagement;
import org.exbin.jaguif.contribution.api.ContributionModuleApi;
import org.exbin.jaguif.contribution.api.ContributionSequenceBuilder;

/**
 * Default toolbar manager.
 */
@ParametersAreNonnullByDefault
public class ToolBarManager implements ToolBarManagement {

    protected final ContributionSequenceBuilder builder;
    protected final ContributionManagement contributionManagement;

    public ToolBarManager() {
        ContributionModuleApi contributionModule = App.getModule(ContributionModuleApi.class);
        contributionManagement = contributionModule.createContributionManager();
        builder = contributionModule.createContributionSequenceBuilder();
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ContextRegistration contextRegistration) {
        ContributionDefinition definition = contributionManagement.getDefinition(toolBarId);
        builder.buildSequence(new ToolBarSequenceOutput(targetToolBar, contextRegistration), definition);
        contextRegistration.finish();
    }

    @Override
    public void buildIconToolBar(JToolBar targetToolBar, String toolBarId, ContextRegistration contextRegistration) {
        ContributionDefinition definition = contributionManagement.getDefinition(toolBarId);
        builder.buildSequence(new IconToolBarSequenceOutput(targetToolBar, contextRegistration), definition);
        contextRegistration.finish();
    }

    @Override
    public void registerToolBar(String toolBarId, String moduleId) {
        contributionManagement.registerDefinition(toolBarId, moduleId);
    }

    @Override
    public void registerToolBarContribution(String toolBarId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = contributionManagement.getDefinition(toolBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + toolBarId + " doesn't exist");
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerToolBarGroup(String toolBarId, String moduleId, String groupId) {
        return contributionManagement.registerContributionGroup(toolBarId, moduleId, groupId);
    }

    @Override
    public void registerToolBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        contributionManagement.registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<SequenceContribution> getContributions() {
        List<ContributionDefinition> definitions = contributionManagement.getAllDefinitions();
        List<SequenceContribution> contributions = new ArrayList<>();
        for (ContributionDefinition definition : definitions) {
            contributions.addAll(definition.getContributions());
        }
        return contributions;
    }
}
