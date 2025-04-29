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
package org.exbin.framework.sidebar;

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
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.sidebar.api.SideBarContribution;
import org.exbin.framework.sidebar.api.GroupSideBarContribution;
import org.exbin.framework.sidebar.api.GroupSideBarContributionRule;
import org.exbin.framework.sidebar.api.PositionSideBarContributionRule;
import org.exbin.framework.sidebar.api.SeparationSideBarContributionRule;
import org.exbin.framework.sidebar.api.SideBarContributionRule;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.sidebar.api.RelativeSideBarContributionRule;

/**
 * Sidebar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarManager {

    /**
     * Side bar records: side bar id -> side bar definition.
     */
    private final Map<String, SideBarDefinition> sideBars = new HashMap<>();

    public SideBarManager() {
    }

    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextService activationUpdateService) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);

        if (sideBarDef == null) {
            return;
        }

        BuilderRecord builderRecord = new BuilderRecord();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        BuilderContributionRecord lastContributionRecord = null;

        // Build contributions tree
        for (SideBarContribution contribution : sideBarDef.getContributions()) {
            String parentGroupId = null;
            PositionSideBarContributionRule.PositionMode positionHint = null;
            SeparationSideBarContributionRule.SeparationMode separationMode = null;
            List<String> afterIds = new ArrayList<>();
            List<String> beforeIds = new ArrayList<>();
            List<SideBarContributionRule> rules = sideBarDef.getRules().get(contribution);
            if (rules != null) {
                for (SideBarContributionRule rule : rules) {
                    if (rule instanceof PositionSideBarContributionRule) {
                        positionHint = ((PositionSideBarContributionRule) rule).getPositionMode();
                    } else if (rule instanceof SeparationSideBarContributionRule) {
                        separationMode = ((SeparationSideBarContributionRule) rule).getSeparationMode();
                    } else if (rule instanceof RelativeSideBarContributionRule) {
                        RelativeSideBarContributionRule.NextToMode nextToMode = ((RelativeSideBarContributionRule) rule).getNextToMode();
                        String contributionId = ((RelativeSideBarContributionRule) rule).getContributionId();
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
                    } else if (rule instanceof GroupSideBarContributionRule) {
                        parentGroupId = ((GroupSideBarContributionRule) rule).getGroupId();
                    }
                }
            }

            BuilderGroupRecord groupRecord = createGroup(builderRecord, parentGroupId);

            BuilderContributionRecord contributionRecord;
            String contributionId = null;
            if (contribution instanceof GroupSideBarContribution) {
                String groupId = ((GroupSideBarContribution) contribution).getGroupId();
                contributionRecord = builderRecord.groupsMap.get(groupId);
                if (contributionRecord == null) {
                    contributionRecord = new BuilderGroupRecord(groupId);
                    builderRecord.groupsMap.put(groupId, (BuilderGroupRecord) contributionRecord);
                }
//            } else if (contribution instanceof DirectSubMenuContribution) {
//                contributionRecord = new BuilderDirectSubMenuContributionRecord(((DirectSubMenuContribution) contribution));
            } else if (contribution instanceof ActionSideBarContribution) {
                contributionRecord = new BuilderActionContributionRecord((ActionSideBarContribution) contribution);
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

        // Generate sidebar
        List<BuilderGroupRecord> processing = new ArrayList<>();
        BuilderContributionMatch contributionMatch = new BuilderContributionMatch();
        BuilderGroupRecord rootRecord = builderRecord.groupsMap.get("");
        processing.add(rootRecord);
        while (!processing.isEmpty()) {
            BuilderGroupRecord processingRecord = processing.get(processing.size() - 1);

            if (processingRecord.processingState == SectionProcessingState.START) {
                if (processingRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.ABOVE || processingRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND) {
                    builderRecord.separatorQueued = true;
                }
                processingRecord.processingState = SectionProcessingState.CONTRIBUTION;
            }

            if (processingRecord.processingState == SectionProcessingState.CONTRIBUTION) {
                if (!processingRecord.contributions.isEmpty()) {
                    contributionMatch.clear();
                    BuilderContributionRecord contribution;
                    while (contributionMatch.nextMatch == -1) {
                        int index = 0;
                        while (index < processingRecord.contributions.size()) {
                            contribution = processingRecord.contributions.get(index);
                            boolean noAfterItems = contribution.placeAfter == null || contribution.placeAfter.isEmpty();
                            boolean directPlaceMatch = noAfterItems ? false : builderRecord.processedContributions.containsAll(contribution.placeAfter);
                            if (noAfterItems || directPlaceMatch) {
                                if (contributionMatch.fallbackMatch == -1) {
                                    contributionMatch.fallbackMatch = index;
                                }
                                if (contribution.positionHint == processingRecord.processingPosition) {
                                    if (contributionMatch.positionMatch == -1) {
                                        contributionMatch.positionMatch = index;
                                    }

                                    if (contributionMatch.nextMatch == -1 && directPlaceMatch) {
                                        contributionMatch.nextMatch = index;
                                    }

                                    if (contributionMatch.nextHintMatch == -1 && contribution.previousHint == builderRecord.previousContribution) {
                                        contributionMatch.nextHintMatch = index;
                                    }
                                }
                            }
                            index++;
                        }

                        if (contributionMatch.positionMatch >= 0 || processingRecord.processingPosition == PositionSideBarContributionRule.PositionMode.BOTTOM_LAST) {
                            break;
                        }

                        processingRecord.processingPosition = PositionSideBarContributionRule.PositionMode.values()[processingRecord.processingPosition.ordinal() + 1];
                    }
                    if (contributionMatch.hasFoundMatch()) {
                        int index = contributionMatch.bestMatch();
                        contribution = processingRecord.contributions.remove(index);

                        if (contribution.separationMode == SeparationSideBarContributionRule.SeparationMode.ABOVE || contribution.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        if (contribution instanceof BuilderGroupRecord) {
                            processing.add((BuilderGroupRecord) contribution);
                        } else if (contribution instanceof BuilderActionContributionRecord) {
                            BuilderActionContributionRecord contributionRecord = (BuilderActionContributionRecord) contribution;
                            JComponent item = contributionRecord.createItem(sideBarId, buttonGroups);
                            if (builderRecord.separatorQueued) {
                                if (targetSideBar.getComponentCount() > 0) {
                                    targetSideBar.addSeparator();
                                }
                                builderRecord.separatorQueued = false;
                            }
                            targetSideBar.add(item);
                            contributionRecord.finishItem(activationUpdateService);
                            builderRecord.previousContribution = contributionRecord;
                        }

                        if (contribution.separationMode == SeparationSideBarContributionRule.SeparationMode.BELOW || contribution.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        builderRecord.processedContributions.add(contribution.contributionId);
                    } else {
                        Logger.getLogger(SideBarManager.class.getName()).log(Level.SEVERE, "Skipping items");
                        processingRecord.contributions.clear();
                    }
                    continue;
                } else {
                    processingRecord.processingState = SectionProcessingState.END;
                }
            }

            if (processingRecord.processingState == SectionProcessingState.END) {
                if (processingRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.BELOW || processingRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND) {
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

    @Nonnull
    private static JComponent createSideBarComponent(Action action) {
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        JComponent sideBarItem;
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    if (action.getValue(Action.SMALL_ICON) != null) {
                        JToggleButton newItem = new JToggleButton(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        sideBarItem = newItem;
                    } else {
                        JCheckBox newItem = new JCheckBox(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        sideBarItem = newItem;
                    }

                    break;
                }
                case RADIO: {
                    JRadioButton newItem = new JRadioButton(action);
                    newItem.setFocusable(false);
                    newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                    newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                    sideBarItem = newItem;
                    break;
                }
                case CYCLE: {
                    JPopupMenu popupMenu = (JPopupMenu) action.getValue(ActionConsts.CYCLE_POPUP_MENU);
//                    DropDownButton dropDown = new DropDownButton(action, popupMenu);
//                    dropDown.setActionTooltip((String) action.getValue(Action.SHORT_DESCRIPTION));
//                    action.addPropertyChangeListener((PropertyChangeEvent evt) -> {
//                        dropDown.setActionText((String) action.getValue(Action.NAME));
//                    });
//                    // createDefaultSideBarItem(action);
//                    sideBarItem = dropDown;
                    sideBarItem = null;
                    break;
                }
                default: {
                    sideBarItem = createDefaultSideBarItem(action);
                }
            }
        } else {
            sideBarItem = createDefaultSideBarItem(action);
        }
        return sideBarItem;
    }

    private static void addSideBarSeparator(JToolBar targetSideBar) {
        targetSideBar.addSeparator();
    }

    @Nonnull
    private static JComponent createDefaultSideBarItem(Action action) {
        JButton newItem = new JButton(action);
        newItem.setFocusable(false);
        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return newItem;
    }

    public void finishSideBarAction(Action action, ActionContextService activationUpdateService) {
        if (action == null) {
            return;
        }

        activationUpdateService.requestUpdate(action);
    }

    public void registerSideBar(String sideBarId, String pluginId) {
        ObjectUtils.requireNonNull(sideBarId);
        ObjectUtils.requireNonNull(pluginId);

        SideBarDefinition sideBar = sideBars.get(sideBarId);
        if (sideBar != null) {
            throw new IllegalStateException("Side bar with Id " + sideBarId + " already exists.");
        }

        SideBarDefinition sideBarDefinition = new SideBarDefinition(pluginId);
        sideBars.put(sideBarId, sideBarDefinition);
    }

    @Nonnull
    public SideBarContribution registerSideBarItem(String sideBarId, String pluginId, Action action) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);
        if (sideBarDef == null) {
            throw new IllegalStateException("Side bar with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        sideBarDef.getContributions().add(sideBarContribution);
        return sideBarContribution;
    }

    @Nonnull
    public SideBarContribution registerSideBarGroup(String sideBarId, String pluginId, String groupId) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);
        if (sideBarDef == null) {
            throw new IllegalStateException("Side bar with Id " + sideBarId + " doesn't exist");
        }

        GroupSideBarContribution groupContribution = new GroupSideBarContribution(groupId);
        sideBarDef.getContributions().add(groupContribution);
        return groupContribution;
    }

    public void registerSideBarRule(SideBarContribution contribution, SideBarContributionRule rule) {
        SideBarDefinition match = null;
        for (SideBarDefinition sideBarDef : sideBars.values()) {
            if (sideBarDef.getContributions().contains(contribution)) {
                match = sideBarDef;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid side bar contribution rule");
        }

        List<SideBarContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (SideBarDefinition sideBarDef : sideBars.values()) {
            for (SideBarContribution contribution : sideBarDef.getContributions()) {
                if (contribution instanceof ActionSideBarContribution) {
                    actions.add(((ActionSideBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
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
        PositionSideBarContributionRule.PositionMode processingPosition = PositionSideBarContributionRule.PositionMode.TOP;
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
    private static class BuilderActionContributionRecord extends BuilderContributionRecord {

        final ActionSideBarContribution contribution;

        public BuilderActionContributionRecord(ActionSideBarContribution contribution) {
            this.contribution = contribution;
            contributionId = (String) contribution.getAction().getValue(ActionConsts.ACTION_ID);
        }

        @Nonnull
        public JComponent createItem(String toolBarId, Map<String, ButtonGroup> buttonGroups) {
            Action action = contribution.getAction();
            JComponent component = SideBarManager.createSideBarComponent(action);

            /* if (isPopup) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            } */
            return component;
        }

        public void finishItem(ActionContextService activationUpdateService) {
            Action action = contribution.getAction();
            // SideBarManager.finishSideBarAction(action, activationUpdateService);
        }
    }

    private static class BuilderContributionRecord {

        String contributionId;

        SeparationSideBarContributionRule.SeparationMode separationMode;
        PositionSideBarContributionRule.PositionMode positionHint = PositionSideBarContributionRule.PositionMode.DEFAULT;
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
