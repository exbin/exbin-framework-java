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
package org.exbin.jaguif.tabpages.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Interface for tab pages management.
 */
@ParametersAreNonnullByDefault
public interface TabPagesManagement {

    /**
     * Builds tab pages from given síde bar id.
     *
     * @param targetTabPages output tab pages
     * @param tabPagesId tab pages definition id
     * @param contextRegistration context registration
     */
    void buildTabPages(TabPages targetTabPages, String tabPagesId, ContextRegistration contextRegistration);

    /**
     * Registers tab pages.
     *
     * @param tabPagesId tab pages id
     * @param pluginId plugin id
     */
    void registerTabPages(String tabPagesId, String pluginId);

    /**
     * Registers tab pages item contribution.
     *
     * @param tabPagesId tab pages id
     * @param moduleId module id
     * @param contribution tab pages contribution
     */
    void registerTabPagesContribution(String tabPagesId, String moduleId, SequenceContribution contribution);

    /**
     * Registers tab pages group.
     *
     * @param tabPagesId tab pages id
     * @param moduleId module id
     * @param groupId group id
     * @return group contribution
     */
    @Nonnull
    GroupSequenceContribution registerTabPagesGroup(String tabPagesId, String moduleId, String groupId);

    /**
     * Register contribution rule.
     *
     * @param contribution contribution
     * @param rule rule
     */
    void registerTabPagesRule(SequenceContribution contribution, SequenceContributionRule rule);
}
