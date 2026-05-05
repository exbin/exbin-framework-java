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
package org.exbin.jaguif.tabpages;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.ContributionDefinition;
import org.exbin.jaguif.contribution.api.ContributionManagement;
import org.exbin.jaguif.contribution.api.ContributionModuleApi;
import org.exbin.jaguif.contribution.api.ContributionSequenceBuilder;
import org.exbin.jaguif.tabpages.api.TabPages;
import org.exbin.jaguif.tabpages.api.TabPagesManagement;

/**
 * Default tab pages manager.
 */
@ParametersAreNonnullByDefault
public class TabPagesManager implements TabPagesManagement {

    protected final ContributionSequenceBuilder builder;
    protected final ContributionManagement contributionManagement;

    public TabPagesManager() {
        ContributionModuleApi contributionModule = App.getModule(ContributionModuleApi.class);
        contributionManagement = contributionModule.createContributionManager();
        builder = contributionModule.createContributionSequenceBuilder();
    }

    @Override
    public void buildTabPages(TabPages targetTabPages, String tabPagesId, ContextRegistration contextRegistration) {
        ContributionDefinition definition = contributionManagement.getDefinition(tabPagesId);
        builder.buildSequence(new TabPagesSequenceOutput(targetTabPages, contextRegistration), definition);
        contextRegistration.finish();
    }

    @Override
    public void registerTabPages(String tabPagesId, String moduleId) {
        contributionManagement.registerDefinition(tabPagesId, moduleId);
    }

    @Override
    public void registerTabPagesContribution(String tabPagesId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = contributionManagement.getDefinition(tabPagesId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + tabPagesId + " doesn't exist");
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerTabPagesGroup(String tabPagesId, String moduleId, String groupId) {
        return contributionManagement.registerContributionGroup(tabPagesId, moduleId, groupId);
    }

    @Override
    public void registerTabPagesRule(SequenceContribution contribution, SequenceContributionRule rule) {
        contributionManagement.registerContributionRule(contribution, rule);
    }
}
