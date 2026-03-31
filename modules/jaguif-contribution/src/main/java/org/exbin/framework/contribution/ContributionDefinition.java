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
package org.exbin.framework.contribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Contribution definition.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ContributionDefinition {

    protected final List<SequenceContribution> contributions = new ArrayList<>();
    protected final Map<SequenceContribution, List<SequenceContributionRule>> rules = new HashMap<>();

    public ContributionDefinition() {
    }

    @Nonnull
    public List<SequenceContribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<SequenceContribution> contributions) {
        this.contributions.clear();
        this.contributions.addAll(contributions);
    }

    public void addContribution(SequenceContribution contribution) {
        contributions.add(contribution);
    }

    public void removeContribution(SequenceContribution contribution) {
        contributions.remove(contribution);
    }

    public boolean containsContribution(SequenceContribution contribution) {
        return contributions.contains(contribution);
    }

    @Nonnull
    public Map<SequenceContribution, List<SequenceContributionRule>> getRules() {
        return rules;
    }

    @Nonnull
    public Optional<List<SequenceContributionRule>> getContributionRules(SequenceContribution contribution) {
        return Optional.ofNullable(rules.get(contribution));
    }

    public void setRules(Map<SequenceContribution, List<SequenceContributionRule>> rules) {
        this.rules.clear();
        this.rules.putAll(rules);
    }

    public void addRule(SequenceContribution contribution, SequenceContributionRule rule) {
        List<SequenceContributionRule> contributionRules = rules.get(contribution);
        if (contributionRules == null) {
            contributionRules = new ArrayList<>();
            rules.put(contribution, contributionRules);
        }
        contributionRules.add(rule);
    }

    public void removeRule(SequenceContribution contribution, SequenceContributionRule rule) {
        List<SequenceContributionRule> contributionRules = rules.get(contribution);
        if (contributionRules != null) {
            contributionRules.remove(rule);
            if (contributionRules.isEmpty()) {
                rules.put(contribution, null);
            }
        }
    }
}
