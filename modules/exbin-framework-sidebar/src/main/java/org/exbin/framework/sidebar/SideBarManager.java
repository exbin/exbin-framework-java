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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
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
    private Map<String, SideBarDefinition> sideBars = new HashMap<>();

    /**
     * Side bar modified flags.
     */
    private Set<String> sideBarModified = new HashSet<>();

    /**
     * Map of plugins usage per tool bar id.
     */
    private Map<String, String> pluginsUsage = new HashMap<>();

    public SideBarManager() {
    }

    // TODO support for multiple frames / sidebars
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextService activationUpdateService) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);

        if (sideBarDef == null) {
            return;
        }

        List<SideBarGroupRecord> groupRecords = new LinkedList<>();

        // Create list of build-in groups
        Map<String, SideBarGroupRecord> groupsMap = new HashMap<>();
        for (PositionSideBarContributionRule.PositionMode mode : PositionSideBarContributionRule.PositionMode.values()) {
            SideBarGroupRecord sideBarGroupRecord = new SideBarGroupRecord(mode.name());
            groupsMap.put(mode.name(), sideBarGroupRecord);
            groupRecords.add(sideBarGroupRecord);
        }

        // Build full tree of groups
        for (SideBarContribution contribution : sideBarDef.getContributions()) {
            if (!(contribution instanceof GroupSideBarContribution)) {
                continue;
            }
            String groupId = ((GroupSideBarContribution) contribution).getGroupId();
            SeparationSideBarContributionRule.SeparationMode separationMode = getSeparationMode(sideBarId, contribution);
            String parentGroupId = getParentGroup(sideBarId, contribution);
            if (parentGroupId != null) {
                SideBarGroupRecord groupRecord = groupsMap.get(parentGroupId);
                SideBarGroupRecord menuGroupRecord = new SideBarGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            } else {
                PositionSideBarContributionRule.PositionMode positionMode = getPositionMode(sideBarId, contribution);
                if (positionMode == null) {
                    positionMode = PositionSideBarContributionRule.PositionMode.DEFAULT;
                }
                SideBarGroupRecord groupRecord = groupsMap.get(positionMode.name());
                SideBarGroupRecord menuGroupRecord = new SideBarGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            }
        }

        // Go thru all contributions and link them to its target group
        for (SideBarContribution contribution : sideBarDef.getContributions()) {
            if (contribution instanceof GroupSideBarContribution) {
                continue;
            }
            PositionSideBarContributionRule.PositionMode positionMode = getPositionMode(sideBarId, contribution);
            String parentGroupId = getParentGroup(sideBarId, contribution);
            if (positionMode != null) {
                SideBarGroupRecord sideBarGroupRecord = groupsMap.get(positionMode.name());
                sideBarGroupRecord.contributions.add(contribution);
            } else {
                if (parentGroupId != null) {
                    SideBarGroupRecord sideBarGroupRecord = groupsMap.get(parentGroupId);
                    sideBarGroupRecord.contributions.add(contribution);
                } else {
                    SideBarGroupRecord sideBarGroupRecord = groupsMap.get(PositionSideBarContributionRule.PositionMode.DEFAULT.name());
                    sideBarGroupRecord.contributions.add(contribution);
                }
            }
        }

        processSideBarGroup(groupRecords, targetSideBar, activationUpdateService);
    }

    private void processSideBarGroup(List<SideBarGroupRecord> groups, JToolBar targetSideBar, ActionContextService activationUpdateService) {
        List<SideBarGroupRecordPathNode> processingPath = new LinkedList<>();
        processingPath.add(new SideBarGroupRecordPathNode(groups));

        boolean separatorQueued = false;
        boolean sideBarContinues = false;

        while (!processingPath.isEmpty()) {
            SideBarGroupRecordPathNode pathNode = processingPath.get(processingPath.size() - 1);
            if (pathNode.childIndex == pathNode.records.size()) {
                processingPath.remove(processingPath.size() - 1);
                continue;
            }

            SideBarGroupRecord groupRecord = pathNode.records.get(pathNode.childIndex);
            pathNode.childIndex++;

            if ((groupRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.ABOVE || groupRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND) && sideBarContinues) {
                addSideBarSeparator(targetSideBar);
                separatorQueued = false;
            }

            for (SideBarContribution contribution : groupRecord.contributions) {
                if (separatorQueued) {
                    addSideBarSeparator(targetSideBar);
                    separatorQueued = false;
                }

                if (contribution instanceof ActionSideBarContribution) {
                    Action action = ((ActionSideBarContribution) contribution).getAction();
                    JComponent sideBarItem = createSideBarComponent(action);
                    targetSideBar.add(sideBarItem);
                    finishSideBarAction(action, activationUpdateService);
                }

                sideBarContinues = true;
            }

            if (groupRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.AROUND || groupRecord.separationMode == SeparationSideBarContributionRule.SeparationMode.BELOW) {
                separatorQueued = true;
            }

            if (!groupRecord.subGroups.isEmpty()) {
                processingPath.add(new SideBarGroupRecordPathNode(groupRecord.subGroups));
            }
        }
    }

    @Nonnull
    private static JComponent createSideBarComponent(Action action) {
        if (SwingUtilities.isEventDispatchThread()) {
            return createSideBarComponentInt(action);
        }

        final JComponent[] result = new JComponent[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = createSideBarComponentInt(action);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(SideBarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result[0];
    }

    @Nonnull
    private static JComponent createSideBarComponentInt(Action action) {
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
        if (SwingUtilities.isEventDispatchThread()) {
            targetSideBar.addSeparator();
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    targetSideBar.addSeparator();
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(SideBarManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        if (action != null) {
            activationUpdateService.requestUpdate(action);
        }
    }

    public void registerSideBar(String sideBarId, String pluginId) {
        ObjectUtils.requireNonNull(sideBarId);
        ObjectUtils.requireNonNull(pluginId);

        SideBarDefinition sideBar = sideBars.get(sideBarId);
        if (sideBar != null) {
            throw new IllegalStateException("Tool bar with Id " + sideBarId + " already exists.");
        }

        SideBarDefinition sideBarDefinition = new SideBarDefinition(pluginId);
        sideBars.put(sideBarId, sideBarDefinition);
    }

    @Nonnull
    public SideBarContribution registerSideBarItem(String sideBarId, String pluginId, Action action) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);
        if (sideBarDef == null) {
            throw new IllegalStateException("Tool bar with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        sideBarDef.getContributions().add(sideBarContribution);
        return sideBarContribution;
    }

    @Nonnull
    public SideBarContribution registerSideBarGroup(String sideBarId, String pluginId, String groupId) {
        SideBarDefinition sideBarDef = sideBars.get(sideBarId);
        if (sideBarDef == null) {
            throw new IllegalStateException("Tool bar with Id " + sideBarId + " doesn't exist");
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
            throw new IllegalStateException("Invalid tool bar contribution rule");
        }

        List<SideBarContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    @Nullable
    private GroupSideBarContribution getGroup(String sideBarId, String groupId) {
        SideBarDefinition sideBarDefinition = sideBars.get(sideBarId);
        for (SideBarContribution contribution : sideBarDefinition.getContributions()) {
            if (contribution instanceof GroupSideBarContribution) {
                if (((GroupSideBarContribution) contribution).getGroupId().equals(groupId)) {
                    return (GroupSideBarContribution) contribution;
                }
            }
        }
        return null;
    }

    @Nullable
    private SeparationSideBarContributionRule.SeparationMode getSeparationMode(String sideBarId, SideBarContribution contribution) {
        SideBarDefinition sideBarDefinition = sideBars.get(sideBarId);
        List<SideBarContributionRule> rules = sideBarDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (SideBarContributionRule rule : rules) {
            if (rule instanceof SeparationSideBarContributionRule) {
                return ((SeparationSideBarContributionRule) rule).getSeparationMode();
            }
        }
        return null;
    }

    @Nullable
    private PositionSideBarContributionRule.PositionMode getPositionMode(String sideBarId, SideBarContribution contribution) {
        SideBarDefinition sideBarDefinition = sideBars.get(sideBarId);
        List<SideBarContributionRule> rules = sideBarDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (SideBarContributionRule rule : rules) {
            if (rule instanceof PositionSideBarContributionRule) {
                return ((PositionSideBarContributionRule) rule).getPositionMode();
            }
        }
        return null;
    }

    @Nullable
    private String getParentGroup(String sideBarId, SideBarContribution contribution) {
        SideBarDefinition menuDefinition = sideBars.get(sideBarId);
        List<SideBarContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (SideBarContributionRule rule : rules) {
            if (rule instanceof GroupSideBarContributionRule) {
                return ((GroupSideBarContributionRule) rule).getGroupId();
            }
        }
        return null;
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

    @ParametersAreNonnullByDefault
    private class SideBarGroupRecord {

        String groupId;
        SeparationSideBarContributionRule.SeparationMode separationMode;
        List<SideBarGroupRecord> subGroups = new LinkedList<>();
        List<SideBarContribution> contributions = new LinkedList<>();

        public SideBarGroupRecord(String groupId) {
            this.groupId = groupId;
        }

        public SideBarGroupRecord(String groupId, SeparationSideBarContributionRule.SeparationMode separationMode) {
            this(groupId);
            this.separationMode = separationMode;
        }
    }

    @ParametersAreNonnullByDefault
    private class SideBarGroupRecordPathNode {

        List<SideBarGroupRecord> records;
        int childIndex;

        public SideBarGroupRecordPathNode(List<SideBarGroupRecord> records) {
            this.records = records;
        }
    }
}
