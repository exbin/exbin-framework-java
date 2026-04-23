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
package org.exbin.jaguif.contribution.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sequence action item contribution.
 */
@ParametersAreNonnullByDefault
public interface ContributionManagement {

    /**
     * Registers definition.
     *
     * @param definitionId definition identifier
     * @param moduleId module identifier
     * @return contribution definition
     */
    @Nonnull
    ContributionDefinition registerDefinition(String definitionId, String moduleId);

    /**
     * Unregisters definition.
     *
     * @param definitionId definition identifier
     */
    void unregisterDefinition(String definitionId);

    /**
     * Returns contribution definition.
     *
     * @param definitionId definition identifier
     * @return contribution definition
     */
    @Nullable
    ContributionDefinition getDefinition(String definitionId);

    /**
     * Returns registered definitions.
     *
     * @return contribution definitions
     */
    List<ContributionDefinition> getAllDefinitions();

    /**
     * Registers contribution group.
     *
     * @param definitionId definition identifier
     * @param moduleId module identifier
     * @param groupId group identifier
     * @return group sequence contribution
     */
    @Nonnull
    GroupSequenceContribution registerContributionGroup(String definitionId, String moduleId, String groupId);

    /**
     * Checks whether group exists.
     *
     * @param subId sub group identifier
     * @param groupId group identifier
     * @return true if exists
     */
    boolean subGroupExists(String subId, String groupId);

    /**
     * Registers contribution rule.
     *
     * @param contribution contribution
     * @param rule contribution rule
     */
    void registerContributionRule(SequenceContribution contribution, SequenceContributionRule rule);
}
