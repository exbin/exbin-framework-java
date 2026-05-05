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
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.tabpages.api.TabPagesDefinitionManagement;
import org.exbin.jaguif.tabpages.api.TabPagesManagement;

/**
 * Default tab pages definition manager.
 */
@ParametersAreNonnullByDefault
public class TabPagesDefinitionManager implements TabPagesDefinitionManagement {

    protected final TabPagesManagement tabPagesManager;
    protected final String tabPagesId;
    protected final String moduleId;

    public TabPagesDefinitionManager(TabPagesManagement tabPagesManager, String tabPagesId, String moduleId) {
        this.tabPagesManager = tabPagesManager;
        this.tabPagesId = tabPagesId;
        this.moduleId = moduleId;
    }

    @Override
    public void registerTabPagesContribution(SequenceContribution contribution) {
        tabPagesManager.registerTabPagesContribution(tabPagesId, moduleId, contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerTabPagesGroup(String groupId) {
        return tabPagesManager.registerTabPagesGroup(tabPagesId, moduleId, groupId);
    }

    @Override
    public void registerTabPagesRule(SequenceContribution tabPagesContribution, SequenceContributionRule rule) {
        tabPagesManager.registerTabPagesRule(tabPagesContribution, rule);
    }
}
