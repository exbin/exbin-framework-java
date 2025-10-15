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
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Contribution manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ContributionManager {

    public ContributionManager() {
    }

    /**
     * Builds sequence of items / events for given definition.
     *
     * @param targetSequence target ouput sequence
     * @param contributionDef contribution definition
     */
    public void buildSequence(ContributionSequenceOutput targetSequence, @Nullable ContributionDefinition contributionDef) {
        if (contributionDef == null) {
            return;
        }

        BuilderRecord builderRecord = new BuilderRecord();
        BuilderContributionRecord lastContributionRecord = null;

        // Build contributions tree
        for (SequenceContribution contribution : contributionDef.getContributions()) {
            String parentGroupId = null;
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
                    }
                }
            }

            BuilderGroupRecord groupRecord = createGroup(builderRecord, parentGroupId);

            BuilderContributionRecord contributionRecord;
            String contributionId = null;
            if (contribution instanceof GroupSequenceContribution) {
                String groupId = ((GroupSequenceContribution) contribution).getGroupId();
                contributionRecord = builderRecord.groupsMap.get(groupId);
                if (contributionRecord == null) {
                    contributionRecord = new BuilderGroupRecord(groupId);
                    builderRecord.groupsMap.put(groupId, (BuilderGroupRecord) contributionRecord);
                }
            } else if (contribution instanceof ItemSequenceContribution) {
                contributionRecord = new BuilderItemContributionRecord((ItemSequenceContribution) contribution);
            } else {
                throw new IllegalStateException("Unsupported contribution type: " + (contribution == null ? "null" : contribution.getClass().getName()));
            }

            if (contributionId != null && builderRecord.contributionsMap.containsKey(contributionId)) {
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
            List<String> defferedAfterIds = builderRecord.afterMap.remove(contributionId);
            if (defferedAfterIds != null) {
                contributionRecord.placeAfter.addAll(defferedAfterIds);
            }
            for (String itemId : beforeIds) {
                BuilderContributionRecord itemRecord = builderRecord.contributionsMap.get(itemId);
                if (itemRecord != null) {
                    itemRecord.placeAfter.add(contributionId);
                } else {
                    List<String> itemAfterIds = builderRecord.afterMap.get(itemId);
                    if (itemAfterIds == null) {
                        itemAfterIds = new ArrayList<>();
                        itemAfterIds.add(contributionId);
                        builderRecord.afterMap.put(itemId, itemAfterIds);
                    } else {
                        itemAfterIds.add(contributionId);
                    }
                }
            }

            groupRecord.contributions.add(contributionRecord);
            if (contributionId != null) {
                builderRecord.contributionsMap.put(contributionId, contributionRecord);
            }
        }

        BuilderGroupRecord rootRecord = builderRecord.groupsMap.get("");
        if (rootRecord == null) {
            return;
        }

        // Generate sequence
        List<BuilderGroupRecord> processing = new ArrayList<>();
        processing.add(rootRecord);
        BuilderContributionMatch contributionMatch = new BuilderContributionMatch();
        while (!processing.isEmpty()) {
            BuilderGroupRecord processingRecord = processing.get(processing.size() - 1);

            if (processingRecord.processingState == SectionProcessingState.START) {
                if (processingRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.ABOVE || processingRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                    builderRecord.separatorQueued = true;
                }
                processingRecord.processingState = SectionProcessingState.CONTRIBUTION;
            }

            if (processingRecord.processingState == SectionProcessingState.CONTRIBUTION) {
                if (!processingRecord.contributions.isEmpty()) {
                    contributionMatch.clear();
                    BuilderContributionRecord record;
                    while (contributionMatch.nextMatch == -1) {
                        int index = 0;
                        while (index < processingRecord.contributions.size()) {
                            record = processingRecord.contributions.get(index);
                            boolean noAfterItems = record.placeAfter == null || record.placeAfter.isEmpty();
                            boolean directPlaceMatch = noAfterItems ? false : builderRecord.processedContributions.containsAll(record.placeAfter);
                            if (noAfterItems || directPlaceMatch) {
                                if (contributionMatch.fallbackMatch == -1) {
                                    contributionMatch.fallbackMatch = index;
                                }
                                if (record.positionHint == processingRecord.processingPosition) {
                                    if (contributionMatch.positionMatch == -1) {
                                        contributionMatch.positionMatch = index;
                                    }

                                    if (contributionMatch.nextMatch == -1 && directPlaceMatch) {
                                        contributionMatch.nextMatch = index;
                                    }

                                    if (contributionMatch.nextHintMatch == -1 && record.previousHint == builderRecord.previousContribution) {
                                        contributionMatch.nextHintMatch = index;
                                    }
                                }
                            }
                            index++;
                        }

                        if (contributionMatch.positionMatch >= 0 || processingRecord.processingPosition == PositionSequenceContributionRule.PositionMode.BOTTOM_LAST) {
                            break;
                        }

                        processingRecord.processingPosition = PositionSequenceContributionRule.PositionMode.values()[processingRecord.processingPosition.ordinal() + 1];
                    }
                    if (contributionMatch.hasFoundMatch()) {
                        int index = contributionMatch.bestMatch();
                        record = processingRecord.contributions.remove(index);

                        if (record.separationMode == SeparationSequenceContributionRule.SeparationMode.ABOVE || record.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        if (record instanceof BuilderGroupRecord) {
                            processing.add((BuilderGroupRecord) record);
                        } else if (record instanceof BuilderItemContributionRecord) {
                            BuilderItemContributionRecord contributionRecord = (BuilderItemContributionRecord) record;
                            boolean valid = targetSequence.initItem(((BuilderItemContributionRecord) record).contribution);
                            if (valid) {
                                if (builderRecord.separatorQueued) {
                                    if (!targetSequence.isEmpty()) {
                                        targetSequence.addSeparator();
                                    }
                                    builderRecord.separatorQueued = false;
                                }
                                targetSequence.add(((BuilderItemContributionRecord) record).contribution);
                                builderRecord.previousContribution = contributionRecord;
                            }
                        }

                        if (record.separationMode == SeparationSequenceContributionRule.SeparationMode.BELOW || record.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        builderRecord.processedContributions.add(record.contributionId);
                    } else {
                        Logger.getLogger(ContributionManager.class.getName()).log(Level.SEVERE, "Skipping items");
                        processingRecord.contributions.clear();
                    }
                    continue;
                } else {
                    processingRecord.processingState = SectionProcessingState.END;
                }
            }

            if (processingRecord.processingState == SectionProcessingState.END) {
                if (processingRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.BELOW || processingRecord.separationMode == SeparationSequenceContributionRule.SeparationMode.AROUND) {
                    builderRecord.separatorQueued = true;
                }
                processing.remove(processing.size() - 1);
            }
        }
    }

    @Nonnull
    private static BuilderGroupRecord createGroup(BuilderRecord builderRecord, @Nullable String groupId) {
        if (groupId == null) {
            groupId = "";
        }

        BuilderGroupRecord groupRecord = builderRecord.groupsMap.get(groupId);
        if (groupRecord == null) {
            groupRecord = new BuilderGroupRecord(groupId);
            builderRecord.groupsMap.put(groupId, groupRecord);
        }

        return groupRecord;
    }

    private static class BuilderRecord {

        Map<String, BuilderGroupRecord> groupsMap = new HashMap<>();
        Map<String, BuilderContributionRecord> contributionsMap = new HashMap<>();

        boolean separatorQueued = false;
        BuilderContributionRecord previousContribution = null;
        Map<String, List<String>> afterMap = new HashMap<>();
        Set<String> processedContributions = new HashSet<>();
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
