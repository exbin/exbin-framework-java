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
package org.exbin.jaguif.statusbar;

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
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarManagement;

/**
 * Default status bar manager.
 */
@ParametersAreNonnullByDefault
public class StatusBarManager implements StatusBarManagement {

    protected final ContributionSequenceBuilder builder;
    protected final ContributionManagement contributionManagement;

    public StatusBarManager() {
        ContributionModuleApi contributionModule = App.getModule(ContributionModuleApi.class);
        contributionManagement = contributionModule.createContributionManager();
        builder = contributionModule.createContributionSequenceBuilder();
    }

    @Override
    public void buildStatusBar(StatusBar targetStatusBar, String statusBarId, ContextRegistration contextRegistration) {
        ContributionDefinition definition = contributionManagement.getDefinition(statusBarId);
        builder.buildSequence(new StatusBarSequenceOutput(targetStatusBar, contextRegistration), definition);
        contextRegistration.finish();
    }

    @Override
    public void registerStatusBar(String statusBarId, String moduleId) {
        contributionManagement.registerDefinition(statusBarId, moduleId);
    }

    @Override
    public void registerStatusBarContribution(String statusBarId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = contributionManagement.getDefinition(statusBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + statusBarId + " doesn't exist");
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerStatusBarGroup(String statusBarId, String moduleId, String groupId) {
        return contributionManagement.registerContributionGroup(statusBarId, moduleId, groupId);
    }

    @Override
    public void registerStatusBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        contributionManagement.registerContributionRule(contribution, rule);
    }
}
