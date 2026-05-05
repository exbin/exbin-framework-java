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

/**
 * Interface for registered tab pages definition management.
 */
@ParametersAreNonnullByDefault
public interface TabPagesDefinitionManagement {

    /**
     * Registers item as a child item for given tab pages.
     *
     * @param contribution tab pages contribution
     */
    void registerTabPagesContribution(SequenceContribution contribution);

    /**
     * Registers group as a child item for given tab pages.
     *
     * @param groupId group id
     * @return tab pages contribution
     */
    @Nonnull
    GroupSequenceContribution registerTabPagesGroup(String groupId);

    /**
     * Registers tab pages contribution rule.
     *
     * @param tabPagesContribution tab pages contribution
     * @param rule tab pages contribution rule
     */
    void registerTabPagesRule(SequenceContribution tabPagesContribution, SequenceContributionRule rule);
}
