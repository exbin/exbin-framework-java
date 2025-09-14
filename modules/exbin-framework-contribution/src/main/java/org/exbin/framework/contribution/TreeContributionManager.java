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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContributionRule;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;

/**
 * Tree contribution manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TreeContributionManager {

    /**
     * Definition records: definition id -> contribution definition.
     */
    protected final Map<String, ContributionDefinition> definitions = new HashMap<>();

    public TreeContributionManager() {
    }

    /**
     * Builds sequence of items / events for given definition.
     *
     * @param targetSequence target ouput sequence
     * @param definitionId definition id
     */
    public void buildSequence(TreeContributionSequenceOutput targetSequence, String definitionId) {
        ContributionDefinition contributionDef = definitions.get(definitionId);

        if (contributionDef == null) {
            return;
        }

        BuilderRecord builderRecord = new BuilderRecord();
        BuilderContributionRecord lastContributionRecord = null;

        // Build contributions tree
        for (SequenceContribution contribution : contributionDef.getContributions()) {
            String parentGroupId = null;
            String parentSubId = null;
            PositionSequenceContributionRule.PositionMode positionHint = null;
            SeparationSequenceContributionRule.SeparationMode separationMode = null;
            List<String> afterIds = new ArrayList<>();
            List<String> beforeIds = new ArrayList<>();
            List<SequenceContributionRule> rules = contributionDef.getRules().get(contribution);
            if (rules != null) {
                for (SequenceContributionRule rule : rules) {
                    if (rule instanceof PositionSequenceContributionRule) {
                        positionHint = ((PositionSequenceContributionRule) rule).getPositionMode();
                    } else if (rule instanceof SeparationSequenceContributionRule) {
                        separationMode = ((SeparationSequenceContributionRule) rule).getSeparationMode();
                    } else if (rule instanceof RelativeSequenceContributionRule) {
                        RelativeSequenceContributionRule.NextToMode nextToMode = ((RelativeSequenceContributionRule) rule).getNextToMode();
                        String contributionId = ((RelativeSequenceContributionRule) rule).getContributionId();
                        switch (nextToMode) {
                            case AFTER:
                                afterIds.add(contributionId);
                                break;
                            case BEFORE:
                                beforeIds.add(contributionId);
                                break;
                            default:
                                throw new AssertionError();
                        }
                    } else if (rule instanceof GroupSequenceContributionRule) {
                        parentGroupId = ((GroupSequenceContributionRule) rule).getGroupId();
                    } else if (rule instanceof SubSequenceContributionRule) {
                        parentSubId = ((SubSequenceContributionRule) rule).getSubContributionId();
                    }
                }
            }

            BuilderGroupRecord groupRecord = createGroup(builderRecord, parentSubId, parentGroupId);
            BuilderSubRecord subRecord = builderRecord.subMap.get((String) (parentSubId == null ? "" : parentSubId));

            BuilderContributionRecord contributionRecord;
            String contributionId = null;
            if (contribution instanceof GroupSequenceContribution) {
                String groupId = ((GroupSequenceContribution) contribution).getGroupId();
                contributionRecord = subRecord.groupsMap.get(groupId);
                if (contributionRecord == null) {
                    contributionRecord = new BuilderGroupRecord(groupId);
                    subRecord.groupsMap.put(groupId, (BuilderGroupRecord) contributionRecord);
                }
            } else if (contribution instanceof SubSequenceContribution) {
                contributionRecord = new BuilderSubContributionRecord((SubSequenceContribution) contribution);
            } else if (contribution instanceof ItemSequenceContribution) {
                contributionRecord = new BuilderItemContributionRecord((ItemSequenceContribution) contribution);
            } else {
                throw new IllegalStateException("Unsupported contribution type: " + (contribution == null ? "null" : contribution.getClass().getName()));
            }

            if (contributionId != null && subRecord.contributionsMap.containsKey(contributionId)) {
                throw new IllegalStateException("Contribution with id " + contributionId + " already exists");
            }

            contributionRecord.separationMode = separationMode;
            contributionRecord.placeAfter.addAll(afterIds);
            if (positionHint != null) {
                contributionRecord.positionHint = positionHint;
            }
            contributionRecord.previousHint = lastContributionRecord;
            lastContributionRecord = contributionRecord;

            // Convert before rules to after rules
            List<String> defferedAfterIds = subRecord.afterMap.remove(contributionId);
            if (defferedAfterIds != null) {
                contributionRecord.placeAfter.addAll(defferedAfterIds);
            }
            for (String itemId : beforeIds) {
                BuilderContributionRecord itemRecord = subRecord.contributionsMap.get(itemId);
                if (itemRecord != null) {
                    itemRecord.placeAfter.add(contributionId);
                } else {
                    List<String> itemAfterIds = subRecord.afterMap.get(itemId);
                    if (itemAfterIds == null) {
                        itemAfterIds = new ArrayList<>();
                        itemAfterIds.add(contributionId);
                        subRecord.afterMap.put(itemId, itemAfterIds);
                    } else {
                        itemAfterIds.add(contributionId);
                    }
                }
            }

            groupRecord.contributions.add(contributionRecord);
            if (contributionId != null) {
                subRecord.contributionsMap.put(contributionId, contributionRecord);
            }
        }

        // Generate menu
        List<BuilderProcessingRecord> processing = new ArrayList<>();
        BuilderContributionMatch contributionMatch = new BuilderContributionMatch();
        BuilderProcessingRecord rootRecord = new BuilderProcessingRecord();
        rootRecord.sub = builderRecord.subMap.get("");
        rootRecord.sub.sequenceOutput = targetSequence;
        rootRecord.group = rootRecord.sub.groupsMap.get("");
        processing.add(rootRecord);
        while (!processing.isEmpty()) {
            BuilderProcessingRecord processingRecord = processing.get(processing.size() - 1);
            BuilderSubRecord subRecord = processingRecord.sub;
            BuilderGroupRecord groupRecord = processingRecord.group;

            if (groupRecord.processingState == SectionProcessingState.START) {
                if (groupRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.ABOVE || groupRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                    subRecord.separatorQueued = true;
                }
                groupRecord.processingState = SectionProcessingState.CONTRIBUTION;
            }

            if (groupRecord.processingState == SectionProcessingState.CONTRIBUTION) {
                if (!groupRecord.contributions.isEmpty()) {
                    contributionMatch.clear();
                    BuilderContributionRecord record;
                    while (contributionMatch.nextMatch == -1) {
                        int index = 0;
                        while (index < groupRecord.contributions.size()) {
                            record = groupRecord.contributions.get(index);
                            boolean noAfterItems = record.placeAfter == null || record.placeAfter.isEmpty();
                            boolean directPlaceMatch = noAfterItems ? false : subRecord.processedContributions.containsAll(record.placeAfter);
                            if (noAfterItems || directPlaceMatch) {
                                if (contributionMatch.fallbackMatch == -1) {
                                    contributionMatch.fallbackMatch = index;
                                }
                                if (record.positionHint == groupRecord.processingPosition) {
                                    if (contributionMatch.positionMatch == -1) {
                                        contributionMatch.positionMatch = index;
                                    }

                                    if (contributionMatch.nextMatch == -1 && directPlaceMatch) {
                                        contributionMatch.nextMatch = index;
                                    }

                                    if (contributionMatch.nextHintMatch == -1 && record.previousHint == subRecord.previousContribution) {
                                        contributionMatch.nextHintMatch = index;
                                    }
                                }
                            }
                            index++;
                        }

                        if (contributionMatch.positionMatch >= 0 || groupRecord.processingPosition == PositionSequenceContributionRule.PositionMode.BOTTOM_LAST) {
                            break;
                        }

                        groupRecord.processingPosition = PositionSequenceContributionRule.PositionMode.values()[groupRecord.processingPosition.ordinal() + 1];
                    }
                    if (contributionMatch.hasFoundMatch()) {
                        int index = contributionMatch.bestMatch();
                        record = groupRecord.contributions.remove(index);

                        if (record.separationMode == SeparationSequenceContributionRule.SeparationMode.ABOVE || record.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                            subRecord.separatorQueued = true;
                        }
                        if (record instanceof BuilderGroupRecord) {
                            BuilderProcessingRecord groupProcessingRecord = new BuilderProcessingRecord();
                            groupProcessingRecord.sub = subRecord;
                            groupProcessingRecord.group = (BuilderGroupRecord) record;
                            processing.add(groupProcessingRecord);
                        } else if (record instanceof BuilderItemContributionRecord) {
                            BuilderItemContributionRecord contributionRecord = (BuilderItemContributionRecord) record;
                            boolean valid = subRecord.sequenceOutput.initItem(contributionRecord.contribution, definitionId, subRecord.subId);
                            if (valid) {
                                if (subRecord.separatorQueued) {
                                    if (!subRecord.sequenceOutput.isEmpty()) {
                                        subRecord.sequenceOutput.addSeparator();
                                    }
                                    subRecord.separatorQueued = false;
                                }
                                subRecord.sequenceOutput.add(contributionRecord.contribution);
                                subRecord.previousContribution = contributionRecord;
                            }
                        } else if (record instanceof BuilderSubContributionRecord) {
                            BuilderSubContributionRecord contributionRecord = (BuilderSubContributionRecord) record;
                            boolean valid = subRecord.sequenceOutput.initItem(contributionRecord.contribution, definitionId, subRecord.subId);
                            if (valid) {
                                BuilderSubRecord subSection = builderRecord.subMap.get(contributionRecord.contributionId);
                                if (subSection != null) {
                                    subSection.subContribution = contributionRecord.contribution;
                                    subSection.sequenceOutput = contributionRecord.contribution.getSubOutput();
                                    BuilderProcessingRecord subProcessingRecord = new BuilderProcessingRecord();
                                    subProcessingRecord.sub = subSection;
                                    subProcessingRecord.group = subSection.groupsMap.get("");
                                    subProcessingRecord.isSubMode = true;
                                    processing.add(subProcessingRecord);
                                }
                                subRecord.previousContribution = contributionRecord;
                            }
                        }

                        if (record.separationMode == SeparationSequenceContributionRule.SeparationMode.BELOW || record.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                            subRecord.separatorQueued = true;
                        }
                        subRecord.processedContributions.add(record.contributionId);
                    } else {
                        Logger.getLogger(TreeContributionManager.class.getName()).log(Level.SEVERE, "Skipping items");
                        groupRecord.contributions.clear();
                    }
                    continue;
                } else {
                    groupRecord.processingState = SectionProcessingState.END;
                }
            }

            if (groupRecord.processingState == SectionProcessingState.END) {
                if (groupRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.BELOW || groupRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                    subRecord.separatorQueued = true;
                }
                processing.remove(processing.size() - 1);
                if (processingRecord.isSubMode && !subRecord.sequenceOutput.isEmpty()) {
                    BuilderSubRecord parentMenuRecord = processing.get(processing.size() - 1).sub;
                    if (parentMenuRecord.separatorQueued) {
                        if (!parentMenuRecord.sequenceOutput.isEmpty()) {
                            parentMenuRecord.sequenceOutput.addSeparator();
                        }
                        parentMenuRecord.separatorQueued = false;
                    }
                    parentMenuRecord.sequenceOutput.add(subRecord.subContribution);
                }
            }
        }
    }

    @Nonnull
    private static BuilderGroupRecord createGroup(BuilderRecord builderRecord, @Nullable String subId, @Nullable String groupId) {
        if (subId == null) {
            subId = "";
        }
        if (groupId == null) {
            groupId = "";
        }

        BuilderSubRecord subRecord = builderRecord.subMap.get(subId);
        if (subRecord == null) {
            subRecord = new BuilderSubRecord(subId, builderRecord.sequenceOutput);
            builderRecord.subMap.put(subId, subRecord);
        }

        BuilderGroupRecord groupRecord = subRecord.groupsMap.get(groupId);
        if (groupRecord == null) {
            groupRecord = new BuilderGroupRecord(groupId);
            subRecord.groupsMap.put(groupId, groupRecord);
        }

        return groupRecord;
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

        ContributionDefinition contributionDefinition = new ContributionDefinition(moduleId);
        definitions.put(definitionId, contributionDefinition);
    }

    @Nonnull
    public GroupSequenceContribution registerContributionGroup(String definitionId, String pluginId, String groupId) {
        ContributionDefinition definition = definitions.get(definitionId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + definitionId + " doesn't exist");
        }

        GroupSequenceContribution groupContribution = new GroupSequenceContribution(groupId);
        definition.getContributions().add(groupContribution);
        return groupContribution;
    }

    public void registerContributionRule(SequenceContribution contribution, SequenceContributionRule rule) {
        ContributionDefinition match = null;
        for (ContributionDefinition definition : definitions.values()) {
            if (definition.getContributions().contains(contribution)) {
                match = definition;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid definition contribution rule");
        }

        List<SequenceContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    private static class BuilderRecord {

        TreeContributionSequenceOutput sequenceOutput;
        Map<String, BuilderSubRecord> subMap = new HashMap<>();
    }

    @ParametersAreNonnullByDefault
    private static class BuilderSubRecord {

        TreeContributionSequenceOutput sequenceOutput;
        SubSequenceContribution subContribution;
        String subId;

        Map<String, BuilderGroupRecord> groupsMap = new HashMap<>();
        Map<String, BuilderContributionRecord> contributionsMap = new HashMap<>();

        boolean separatorQueued = false;
        BuilderContributionRecord previousContribution = null;
        Map<String, List<String>> afterMap = new HashMap<>();
        Set<String> processedContributions = new HashSet<>();

        public BuilderSubRecord(String subId, TreeContributionSequenceOutput sequenceOutput) {
            this.subId = subId;
            this.sequenceOutput = sequenceOutput;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderGroupRecord extends BuilderContributionRecord {

        SectionProcessingState processingState = SectionProcessingState.START;
        PositionSequenceContributionRule.PositionMode processingPosition = PositionSequenceContributionRule.PositionMode.TOP;
        List<BuilderContributionRecord> contributions = new ArrayList<>();

        public BuilderGroupRecord(String groupId) {
            contributionId = groupId;
        }
    }

    private static class BuilderProcessingRecord {

        BuilderSubRecord sub;
        BuilderGroupRecord group;
        boolean isSubMode = false;
    }

    private enum SectionProcessingState {
        START,
        CONTRIBUTION,
        END
    }

    @ParametersAreNonnullByDefault
    private static class BuilderItemContributionRecord extends BuilderContributionRecord {

        final ItemSequenceContribution contribution;

        public BuilderItemContributionRecord(ItemSequenceContribution contribution) {
            this.contribution = contribution;
            contributionId = contribution.getContributionId();
        }

        @Nonnull
        public ItemSequenceContribution getContribution() {
            return contribution;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderSubContributionRecord extends BuilderContributionRecord {

        final SubSequenceContribution contribution;

        public BuilderSubContributionRecord(SubSequenceContribution contribution) {
            this.contribution = contribution;
            this.contributionId = contribution.getContributionId();
        }

        @Nonnull
        public SubSequenceContribution getContribution() {
            return contribution;
        }
    }

    private static class BuilderContributionRecord {

        String contributionId;

        SeparationSequenceContributionRule.SeparationMode separationMode;
        PositionSequenceContributionRule.PositionMode positionHint = PositionSequenceContributionRule.PositionMode.DEFAULT;
        BuilderContributionRecord previousHint = null;
        final Set<String> placeAfter = new HashSet<>();
    }

    private static class BuilderContributionMatch {

        int fallbackMatch = -1;
        int positionMatch = -1;
        int nextMatch = -1;
        int nextHintMatch = -1;

        void clear() {
            fallbackMatch = -1;
            positionMatch = -1;
            nextMatch = -1;
            nextHintMatch = -1;
        }

        boolean hasFoundMatch() {
            return fallbackMatch >= 0 || positionMatch >= 0 || nextMatch >= 0 || nextHintMatch >= 0;
        }

        int bestMatch() {
            if (nextMatch >= 0) {
                return nextMatch;
            }
            if (nextHintMatch >= 0) {
                return nextHintMatch;
            }
            if (positionMatch >= 0) {
                return positionMatch;
            }

            return fallbackMatch;
        }
    }
}
