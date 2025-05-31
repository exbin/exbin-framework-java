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
package org.exbin.framework.toolbar;

import java.beans.PropertyChangeEvent;
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
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.toolbar.api.ToolBarContribution;
import org.exbin.framework.toolbar.api.GroupToolBarContribution;
import org.exbin.framework.toolbar.api.GroupToolBarContributionRule;
import org.exbin.framework.toolbar.api.PositionToolBarContributionRule;
import org.exbin.framework.toolbar.api.SeparationToolBarContributionRule;
import org.exbin.framework.toolbar.api.ToolBarContributionRule;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.toolbar.api.RelativeToolBarContributionRule;

/**
 * Toolbar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarManager {

    /**
     * Tool bar records: tool bar id -> tool bar definition.
     */
    private final Map<String, ToolBarDefinition> toolBars = new HashMap<>();

    public ToolBarManager() {
    }

    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
        buildToolBar(new ToolBarWrapper(targetToolBar), toolBarId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildIconToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
        buildToolBar(new IconToolBarWrapper(targetToolBar), toolBarId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    private void buildToolBar(ToolBarOutput targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
        ToolBarDefinition toolBarDef = toolBars.get(toolBarId);

        if (toolBarDef == null) {
            return;
        }

        BuilderRecord builderRecord = new BuilderRecord();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        BuilderContributionRecord lastContributionRecord = null;

        // Build contributions tree
        for (ToolBarContribution contribution : toolBarDef.getContributions()) {
            String parentGroupId = null;
            PositionToolBarContributionRule.PositionMode positionHint = null;
            SeparationToolBarContributionRule.SeparationMode separationMode = null;
            List<String> afterIds = new ArrayList<>();
            List<String> beforeIds = new ArrayList<>();
            List<ToolBarContributionRule> rules = toolBarDef.getRules().get(contribution);
            if (rules != null) {
                for (ToolBarContributionRule rule : rules) {
                    if (rule instanceof PositionToolBarContributionRule) {
                        positionHint = ((PositionToolBarContributionRule) rule).getPositionMode();
                    } else if (rule instanceof SeparationToolBarContributionRule) {
                        separationMode = ((SeparationToolBarContributionRule) rule).getSeparationMode();
                    } else if (rule instanceof RelativeToolBarContributionRule) {
                        RelativeToolBarContributionRule.NextToMode nextToMode = ((RelativeToolBarContributionRule) rule).getNextToMode();
                        String contributionId = ((RelativeToolBarContributionRule) rule).getContributionId();
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
                    } else if (rule instanceof GroupToolBarContributionRule) {
                        parentGroupId = ((GroupToolBarContributionRule) rule).getGroupId();
                    }
                }
            }

            BuilderGroupRecord groupRecord = createGroup(builderRecord, parentGroupId);

            BuilderContributionRecord contributionRecord;
            String contributionId = null;
            if (contribution instanceof GroupToolBarContribution) {
                String groupId = ((GroupToolBarContribution) contribution).getGroupId();
                contributionRecord = builderRecord.groupsMap.get(groupId);
                if (contributionRecord == null) {
                    contributionRecord = new BuilderGroupRecord(groupId);
                    builderRecord.groupsMap.put(groupId, (BuilderGroupRecord) contributionRecord);
                }
//            } else if (contribution instanceof DirectSubMenuContribution) {
//                contributionRecord = new BuilderDirectSubMenuContributionRecord(((DirectSubMenuContribution) contribution));
            } else if (contribution instanceof ActionToolBarContribution) {
                contributionRecord = new BuilderActionContributionRecord((ActionToolBarContribution) contribution);
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

        // Generate toolbar
        List<BuilderGroupRecord> processing = new ArrayList<>();
        processing.add(rootRecord);
        BuilderContributionMatch contributionMatch = new BuilderContributionMatch();
        while (!processing.isEmpty()) {
            BuilderGroupRecord processingRecord = processing.get(processing.size() - 1);

            if (processingRecord.processingState == SectionProcessingState.START) {
                if (processingRecord.separationMode == SeparationToolBarContributionRule.SeparationMode.ABOVE || processingRecord.separationMode == SeparationToolBarContributionRule.SeparationMode.AROUND) {
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

                        if (contributionMatch.positionMatch >= 0 || processingRecord.processingPosition == PositionToolBarContributionRule.PositionMode.BOTTOM_LAST) {
                            break;
                        }

                        processingRecord.processingPosition = PositionToolBarContributionRule.PositionMode.values()[processingRecord.processingPosition.ordinal() + 1];
                    }
                    if (contributionMatch.hasFoundMatch()) {
                        int index = contributionMatch.bestMatch();
                        contribution = processingRecord.contributions.remove(index);

                        if (contribution.separationMode == SeparationToolBarContributionRule.SeparationMode.ABOVE || contribution.separationMode == SeparationToolBarContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        if (contribution instanceof BuilderGroupRecord) {
                            processing.add((BuilderGroupRecord) contribution);
                        } else if (contribution instanceof BuilderActionContributionRecord) {
                            BuilderActionContributionRecord contributionRecord = (BuilderActionContributionRecord) contribution;
                            JComponent item = contributionRecord.createItem(toolBarId, buttonGroups);
                            if (builderRecord.separatorQueued) {
                                if (!targetToolBar.isEmpty()) {
                                    targetToolBar.addSeparator();
                                }
                                builderRecord.separatorQueued = false;
                            }
                            targetToolBar.add(item);
                            contributionRecord.finishItem(activationUpdateService);
                            builderRecord.previousContribution = contributionRecord;
                        }

                        if (contribution.separationMode == SeparationToolBarContributionRule.SeparationMode.BELOW || contribution.separationMode == SeparationToolBarContributionRule.SeparationMode.AROUND) {
                            builderRecord.separatorQueued = true;
                        }
                        builderRecord.processedContributions.add(contribution.contributionId);
                    } else {
                        Logger.getLogger(ToolBarManager.class.getName()).log(Level.SEVERE, "Skipping items");
                        processingRecord.contributions.clear();
                    }
                    continue;
                } else {
                    processingRecord.processingState = SectionProcessingState.END;
                }
            }

            if (processingRecord.processingState == SectionProcessingState.END) {
                if (processingRecord.separationMode == SeparationToolBarContributionRule.SeparationMode.BELOW || processingRecord.separationMode == SeparationToolBarContributionRule.SeparationMode.AROUND) {
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
    private static JComponent createToolBarComponent(Action action) {
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        JComponent toolBarItem;
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    if (action.getValue(Action.SMALL_ICON) != null) {
                        JToggleButton newItem = new JToggleButton(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        toolBarItem = newItem;
                    } else {
                        JCheckBox newItem = new JCheckBox(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        toolBarItem = newItem;
                    }

                    break;
                }
                case RADIO: {
                    JRadioButton newItem = new JRadioButton(action);
                    newItem.setFocusable(false);
                    newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                    newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                    toolBarItem = newItem;
                    break;
                }
                case CYCLE: {
                    JPopupMenu popupMenu = (JPopupMenu) action.getValue(ActionConsts.CYCLE_POPUP_MENU);
                    DropDownButton dropDown = new DropDownButton(action, popupMenu);
                    dropDown.setActionTooltip((String) action.getValue(Action.SHORT_DESCRIPTION));
                    action.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                        dropDown.setActionText((String) action.getValue(Action.NAME));
                    });
                    // createDefaultToolBarItem(action);
                    toolBarItem = dropDown;
                    break;
                }
                default: {
                    toolBarItem = createDefaultToolBarItem(action);
                }
            }
        } else {
            toolBarItem = createDefaultToolBarItem(action);
        }
        return toolBarItem;
    }

    @Nonnull
    private static JComponent createDefaultToolBarItem(Action action) {
        JButton button = new JButton(action);
        button.setFocusable(false);
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return button;
    }

    private static void finishToolBarAction(Action action, ActionContextService activationUpdateService) {
        if (action == null) {
            return;
        }

        activationUpdateService.requestUpdate(action);
    }

    public void registerToolBar(String toolBarId, String pluginId) {
        ObjectUtils.requireNonNull(toolBarId);
        ObjectUtils.requireNonNull(pluginId);

        ToolBarDefinition toolBar = toolBars.get(toolBarId);
        if (toolBar != null) {
            throw new IllegalStateException("Tool bar with Id " + toolBarId + " already exists.");
        }

        ToolBarDefinition toolBarDefinition = new ToolBarDefinition(pluginId);
        toolBars.put(toolBarId, toolBarDefinition);
    }

    @Nonnull
    public ToolBarContribution registerToolBarItem(String toolBarId, String pluginId, Action action) {
        ToolBarDefinition toolBarDef = toolBars.get(toolBarId);
        if (toolBarDef == null) {
            throw new IllegalStateException("Tool bar with Id " + toolBarId + " doesn't exist");
        }

        ActionToolBarContribution toolBarContribution = new ActionToolBarContribution(action);
        toolBarDef.getContributions().add(toolBarContribution);
        return toolBarContribution;
    }

    @Nonnull
    public ToolBarContribution registerToolBarGroup(String toolBarId, String pluginId, String groupId) {
        ToolBarDefinition toolBarDef = toolBars.get(toolBarId);
        if (toolBarDef == null) {
            throw new IllegalStateException("Tool bar with Id " + toolBarId + " doesn't exist");
        }

        GroupToolBarContribution groupContribution = new GroupToolBarContribution(groupId);
        toolBarDef.getContributions().add(groupContribution);
        return groupContribution;
    }

    public void registerToolBarRule(ToolBarContribution contribution, ToolBarContributionRule rule) {
        ToolBarDefinition match = null;
        for (ToolBarDefinition toolBarDef : toolBars.values()) {
            if (toolBarDef.getContributions().contains(contribution)) {
                match = toolBarDef;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid tool bar contribution rule");
        }

        List<ToolBarContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ToolBarDefinition toolBarDef : toolBars.values()) {
            for (ToolBarContribution contribution : toolBarDef.getContributions()) {
                if (contribution instanceof ActionToolBarContribution) {
                    actions.add(((ActionToolBarContribution) contribution).getAction());
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
        PositionToolBarContributionRule.PositionMode processingPosition = PositionToolBarContributionRule.PositionMode.TOP;
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

        final ActionToolBarContribution contribution;

        public BuilderActionContributionRecord(ActionToolBarContribution contribution) {
            this.contribution = contribution;
            contributionId = (String) contribution.getAction().getValue(ActionConsts.ACTION_ID);
        }

        @Nonnull
        public JComponent createItem(String toolBarId, Map<String, ButtonGroup> buttonGroups) {
            Action action = contribution.getAction();
            JComponent component = ToolBarManager.createToolBarComponent(action);

            return component;
        }

        public void finishItem(ActionContextService activationUpdateService) {
            Action action = contribution.getAction();
            ToolBarManager.finishToolBarAction(action, activationUpdateService);
        }
    }

    private static class BuilderContributionRecord {

        String contributionId;

        SeparationToolBarContributionRule.SeparationMode separationMode;
        PositionToolBarContributionRule.PositionMode positionHint = PositionToolBarContributionRule.PositionMode.DEFAULT;
        BuilderContributionRecord previousHint = null;
        final Set<String> placeAfter = new HashSet<>();
    }

    @ParametersAreNonnullByDefault
    private static interface ToolBarOutput {

        void add(Action action);

        void add(JComponent component);

        void addSeparator();

        boolean isPopup();

        boolean isEmpty();
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

    @ParametersAreNonnullByDefault
    private static class ToolBarWrapper implements ToolBarOutput {

        private final JToolBar toolBar;

        public ToolBarWrapper(JToolBar menuBar) {
            this.toolBar = menuBar;
        }

        @Override
        public void add(Action action) {
            toolBar.add(action);
        }

        @Override
        public void add(JComponent component) {
            toolBar.add(component);
        }

        @Override
        public void addSeparator() {
        }

        @Override
        public boolean isPopup() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return toolBar.getComponentCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class IconToolBarWrapper implements ToolBarOutput {

        private final JToolBar toolBar;

        public IconToolBarWrapper(JToolBar menuBar) {
            this.toolBar = menuBar;
        }

        @Override
        public void add(Action action) {
            toolBar.add(action);
        }

        @Override
        public void add(JComponent component) {
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).setText("");
            }
            toolBar.add(component);
        }

        @Override
        public void addSeparator() {
        }

        @Override
        public boolean isPopup() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return toolBar.getComponentCount() == 0;
        }
    }
}
