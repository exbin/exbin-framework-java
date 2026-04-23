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
package org.exbin.jaguif.contribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.ContributionDefinition;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.contribution.api.TreeContributionManagement;
import org.exbin.jaguif.utils.ObjectUtils;

/**
 * Tree contribution manager.
 */
@ParametersAreNonnullByDefault
public class TreeContributionManager implements TreeContributionManagement {

    /**
     * Definition records: definition id -> contribution definition.
     */
    protected final Map<String, DefaultContributionDefinition> definitions = new HashMap<>();

    public TreeContributionManager() {
    }

    @Nonnull
    @Override
    public ContributionDefinition registerDefinition(String definitionId, String moduleId) {
        ObjectUtils.requireNonNull(definitionId);
        ObjectUtils.requireNonNull(moduleId);

        DefaultContributionDefinition definition = definitions.get(definitionId);
        if (definition != null) {
            throw new IllegalStateException("Contribution definition with Id " + definitionId + " already exists.");
        }

        DefaultContributionDefinition contributionDefinition = new DefaultContributionDefinition();
        definitions.put(definitionId, contributionDefinition);
        return contributionDefinition;
    }

    @Override
    public void unregisterDefinition(String definitionId) {
        DefaultContributionDefinition definition = definitions.get(definitionId);
        if (definition != null) {
            // TODO clear pointers to improve garbage collection performance?
//            List<MenuContribution> contributions = definition.getContributions();
//            for (MenuContribution contribution : contributions) {
//                contribution.
//            }

            /*            for (Map.Entry<String, String> usage : pluginsUsage.entrySet()) {
                if (menuId.equals(usage.getValue())) {
                    pluginsUsage.remove(usage.getKey());
                    break;
                }
            } */
            definitions.remove(definitionId);
        }
    }

    @Nullable
    @Override
    public ContributionDefinition getDefinition(String definitionId) {
        return definitions.get(definitionId);
    }

    @Nonnull
    @Override
    public List<ContributionDefinition> getAllDefinitions() {
        List<ContributionDefinition> result = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            result.add(definition);
        }
        return result;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerContributionGroup(String definitionId, String moduleId, String groupId) {
        DefaultContributionDefinition definition = definitions.get(definitionId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + definitionId + " doesn't exist");
        }

        GroupSequenceContribution groupContribution = new GroupSequenceContribution(groupId);
        definition.addContribution(groupContribution);
        return groupContribution;
    }

    @Override
    public boolean subGroupExists(String subId, String groupId) {
        DefaultContributionDefinition definition = definitions.get(subId);
        if (definition == null) {
            return false;
        }

        for (SequenceContribution contribution : definition.getContributions()) {
            if (contribution instanceof GroupSequenceContribution && ((GroupSequenceContribution) contribution).getGroupId().equals(groupId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void registerContributionRule(SequenceContribution contribution, SequenceContributionRule rule) {
        DefaultContributionDefinition match = null;
        for (DefaultContributionDefinition definition : definitions.values()) {
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
