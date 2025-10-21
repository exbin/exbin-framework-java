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
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
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

    public TreeContributionManager() {
    }

    /**
     * Builds sequence of items / events for given definition.
     *
     * @param targetSequence target ouput sequence
     * @param definitionId definition id
     * @param contributionDef contribution definition
     */
    public void buildSequence(TreeContributionSequenceOutput targetSequence, String definitionId, @Nullable ContributionDefinition contributionDef) {
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
            Optional<List<SequenceContributionRule>> rules = contributionDef.getContributionRules(contribution);
            if (rules.isPresent()) {
                for (SequenceContributionRule rule : rules.get()) {
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

        // Generate sequence
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
                                    Optional<TreeContributionSequenceOutput> subOutput = contributionRecord.contribution.getSubOutput();
                                    subSection.sequenceOutput = subOutput.orElse(targetSequence);
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
                    BuilderSubRecord parentSubRecord = processing.get(processing.size() - 1).sub;
                    if (parentSubRecord.separatorQueued) {
                        if (!parentSubRecord.sequenceOutput.isEmpty()) {
                            parentSubRecord.sequenceOutput.addSeparator();
                        }
                        parentSubRecord.separatorQueued = false;
                    }
                    parentSubRecord.sequenceOutput.add(subRecord.subContribution);
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
