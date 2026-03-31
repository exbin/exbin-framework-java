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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Contribution manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ContributionManager {

    /**
     * Definition records: definition id -> contribution definition.
     */
    protected final Map<String, ContributionDefinition> definitions = new HashMap<>();

    public ContributionManager() {
    }

    public void registerDefinition(String definitionId, String moduleId) {
        ObjectUtils.requireNonNull(definitionId);
        ObjectUtils.requireNonNull(moduleId);

        ContributionDefinition definition = definitions.get(definitionId);
        if (definition != null) {
            throw new IllegalStateException("Contribution definition with Id " + definitionId + " already exists.");
        }

        ContributionDefinition contributionDefinition = new ContributionDefinition();
        definitions.put(definitionId, contributionDefinition);
    }

    @Nonnull
    public GroupSequenceContribution registerContributionGroup(String definitionId, String moduleId, String groupId) {
        ContributionDefinition definition = definitions.get(definitionId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + definitionId + " doesn't exist");
        }

        GroupSequenceContribution groupContribution = new GroupSequenceContribution(groupId);
        definition.addContribution(groupContribution);
        return groupContribution;
    }

    public void registerContributionRule(SequenceContribution contribution, SequenceContributionRule rule) {
        ContributionDefinition match = null;
        for (ContributionDefinition definition : definitions.values()) {
            if (definition.containsContribution(contribution)) {
                match = definition;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid definition contribution rule");
        }

        match.addRule(contribution, rule);
    }
}
