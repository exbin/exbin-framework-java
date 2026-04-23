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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for contribution definition.
 */
@ParametersAreNonnullByDefault
public interface ContributionDefinition {

    /**
     * Registers contribution.
     *
     * @param contribution contribution
     */
    void addContribution(SequenceContribution contribution);

    /**
     * Returns contributions
     *
     * @return contributions;
     */
    @Nonnull
    List<SequenceContribution> getContributions();

    /**
     * Registers contribution rule.
     *
     * @param contribution contribution
     * @param rule contribution rule
     */
    void addRule(SequenceContribution contribution, SequenceContributionRule rule);

    /**
     * Returns contribution rules.
     *
     * @param contribution contribution
     * @return contribution rules
     */
    @Nonnull
    Optional<List<SequenceContributionRule>> getContributionRules(SequenceContribution contribution);
}
