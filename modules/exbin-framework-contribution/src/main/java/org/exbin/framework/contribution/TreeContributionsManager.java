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
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.utils.ObjectUtils;

/**
 * Tree contributions manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TreeContributionsManager extends TreeContributionManager {

    /**
     * Definition records: definition id -> contribution definition.
     */
    protected final Map<String, ContributionDefinition> definitions = new HashMap<>();

    public TreeContributionsManager() {
    }

    public boolean subGroupExists(String subId, String groupId) {
        ContributionDefinition definition = definitions.get(subId);
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

    public void unregisterDefinition(String menuId) {
        ContributionDefinition definition = definitions.get(menuId);
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
            definitions.remove(menuId);
        }
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
    public GroupSequenceContribution registerContributionGroup(String definitionId, String pluginId, String groupId) {
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
