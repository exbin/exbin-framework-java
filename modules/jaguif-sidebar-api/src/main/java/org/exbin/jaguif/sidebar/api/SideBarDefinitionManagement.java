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
package org.exbin.jaguif.sidebar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;

/**
 * Interface for registered side bars definition management.
 */
@ParametersAreNonnullByDefault
public interface SideBarDefinitionManagement {

    /**
     * Registers item as a child item for given side bar.
     *
     * @param contribution side bar contribution
     */
    void registerSideBarContribution(SequenceContribution contribution);

    /**
     * Registers group as a child item for given side bar.
     *
     * @param groupId group id
     * @return sidebar contribution
     */
    @Nonnull
    GroupSequenceContribution registerSideBarGroup(String groupId);

    /**
     * Registers side bar contribution rule.
     *
     * @param sideBarContribution side bar contribution
     * @param rule side bar contribution rule
     */
    void registerSideBarRule(SequenceContribution sideBarContribution, SequenceContributionRule rule);
}
