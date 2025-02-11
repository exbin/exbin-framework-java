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
package org.exbin.framework.action;

import java.beans.PropertyChangeEvent;
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
import org.exbin.framework.action.api.ActionToolBarContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.toolbar.ToolBarContribution;
import org.exbin.framework.action.gui.DropDownButton;
import org.exbin.framework.action.api.toolbar.GroupToolBarContribution;
import org.exbin.framework.action.api.toolbar.GroupToolBarContributionRule;
import org.exbin.framework.action.api.toolbar.PositionToolBarContributionRule;
import org.exbin.framework.action.api.toolbar.SeparationToolBarContributionRule;
import org.exbin.framework.action.api.toolbar.ToolBarContributionRule;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.action.api.ActionContextService;

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
    private Map<String, ToolBarDefinition> toolBars = new HashMap<>();

    /**
     * Tool bar modified flags.
     */
    private Set<String> toolBarModified = new HashSet<>();

    /**
     * Map of plugins usage per tool bar id.
     */
    private Map<String, String> pluginsUsage = new HashMap<>();

    public ToolBarManager() {
    }

    // TODO support for multiple frames / toolbars
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
        ToolBarDefinition toolBarDef = toolBars.get(toolBarId);

        if (toolBarDef == null) {
            return;
        }

        List<ToolBarGroupRecord> groupRecords = new LinkedList<>();

        // Create list of build-in groups
        Map<String, ToolBarGroupRecord> groupsMap = new HashMap<>();
        for (PositionMode mode : PositionMode.values()) {
            ToolBarGroupRecord toolBarGroupRecord = new ToolBarGroupRecord(mode.name());
            groupsMap.put(mode.name(), toolBarGroupRecord);
            groupRecords.add(toolBarGroupRecord);
        }

        // Build full tree of groups
        for (ToolBarContribution contribution : toolBarDef.getContributions()) {
            if (!(contribution instanceof GroupToolBarContribution)) {
                continue;
            }
            String groupId = ((GroupToolBarContribution) contribution).getGroupId();
            SeparationMode separationMode = getSeparationMode(toolBarId, contribution);
            String parentGroupId = getParentGroup(toolBarId, contribution);
            if (parentGroupId != null) {
                ToolBarGroupRecord groupRecord = groupsMap.get(parentGroupId);
                ToolBarGroupRecord menuGroupRecord = new ToolBarGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            } else {
                PositionMode positionMode = getPositionMode(toolBarId, contribution);
                if (positionMode == null) {
                    positionMode = PositionMode.DEFAULT;
                }
                ToolBarGroupRecord groupRecord = groupsMap.get(positionMode.name());
                ToolBarGroupRecord menuGroupRecord = new ToolBarGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            }
        }

        // Go thru all contributions and link them to its target group
        for (ToolBarContribution contribution : toolBarDef.getContributions()) {
            if (contribution instanceof GroupToolBarContribution) {
                continue;
            }
            PositionMode positionMode = getPositionMode(toolBarId, contribution);
            String parentGroupId = getParentGroup(toolBarId, contribution);
            if (positionMode != null) {
                ToolBarGroupRecord toolBarGroupRecord = groupsMap.get(positionMode.name());
                toolBarGroupRecord.contributions.add(contribution);
            } else {
                if (parentGroupId != null) {
                    ToolBarGroupRecord toolBarGroupRecord = groupsMap.get(parentGroupId);
                    toolBarGroupRecord.contributions.add(contribution);
                } else {
                    ToolBarGroupRecord toolBarGroupRecord = groupsMap.get(PositionMode.DEFAULT.name());
                    toolBarGroupRecord.contributions.add(contribution);
                }
            }
        }

        processToolBarGroup(groupRecords, targetToolBar, activationUpdateService);
    }

    private void processToolBarGroup(List<ToolBarGroupRecord> groups, JToolBar targetToolBar, ActionContextService activationUpdateService) {
        List<ToolBarGroupRecordPathNode> processingPath = new LinkedList<>();
        processingPath.add(new ToolBarGroupRecordPathNode(groups));

        boolean separatorQueued = false;
        boolean toolBarContinues = false;

        while (!processingPath.isEmpty()) {
            ToolBarGroupRecordPathNode pathNode = processingPath.get(processingPath.size() - 1);
            if (pathNode.childIndex == pathNode.records.size()) {
                processingPath.remove(processingPath.size() - 1);
                continue;
            }

            ToolBarGroupRecord groupRecord = pathNode.records.get(pathNode.childIndex);
            pathNode.childIndex++;

            if ((groupRecord.separationMode == SeparationMode.ABOVE || groupRecord.separationMode == SeparationMode.AROUND) && toolBarContinues) {
                addToolbarSeparator(targetToolBar);
                separatorQueued = false;
            }

            for (ToolBarContribution contribution : groupRecord.contributions) {
                if (separatorQueued) {
                    addToolbarSeparator(targetToolBar);
                    separatorQueued = false;
                }

                if (contribution instanceof ActionToolBarContribution) {
                    Action action = ((ActionToolBarContribution) contribution).getAction();
                    JComponent toolBarItem = createToolBarComponent(action);
                    targetToolBar.add(toolBarItem);
                    finishToolbarAction(action, activationUpdateService);
                }

                toolBarContinues = true;
            }

            if (groupRecord.separationMode == SeparationMode.AROUND || groupRecord.separationMode == SeparationMode.BELOW) {
                separatorQueued = true;
            }

            if (!groupRecord.subGroups.isEmpty()) {
                processingPath.add(new ToolBarGroupRecordPathNode(groupRecord.subGroups));
            }
        }
    }

    @Nonnull
    private static JComponent createToolBarComponent(Action action) {
        if (SwingUtilities.isEventDispatchThread()) {
            return createToolBarComponentInt(action);
        }

        final JComponent[] result = new JComponent[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = createToolBarComponentInt(action);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ToolBarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result[0];
    }

    @Nonnull
    private static JComponent createToolBarComponentInt(Action action) {
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

    private static void addToolbarSeparator(JToolBar targetToolBar) {
        if (SwingUtilities.isEventDispatchThread()) {
            targetToolBar.addSeparator();
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    targetToolBar.addSeparator();
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(ToolBarManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Nonnull
    private static JComponent createDefaultToolBarItem(Action action) {
        JButton newItem = new JButton(action);
        newItem.setFocusable(false);
        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return newItem;
    }

    public void finishToolbarAction(Action action, ActionContextService activationUpdateService) {
        if (action != null) {
            activationUpdateService.requestUpdate(action);
        }
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

    @Nullable
    private GroupToolBarContribution getGroup(String toolBarId, String groupId) {
        ToolBarDefinition toolBarDefinition = toolBars.get(toolBarId);
        for (ToolBarContribution contribution : toolBarDefinition.getContributions()) {
            if (contribution instanceof GroupToolBarContribution) {
                if (((GroupToolBarContribution) contribution).getGroupId().equals(groupId)) {
                    return (GroupToolBarContribution) contribution;
                }
            }
        }
        return null;
    }

    @Nullable
    private SeparationMode getSeparationMode(String toolBarId, ToolBarContribution contribution) {
        ToolBarDefinition toolBarDefinition = toolBars.get(toolBarId);
        List<ToolBarContributionRule> rules = toolBarDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (ToolBarContributionRule rule : rules) {
            if (rule instanceof SeparationToolBarContributionRule) {
                return ((SeparationToolBarContributionRule) rule).getSeparationMode();
            }
        }
        return null;
    }

    @Nullable
    private PositionMode getPositionMode(String toolBarId, ToolBarContribution contribution) {
        ToolBarDefinition toolBarDefinition = toolBars.get(toolBarId);
        List<ToolBarContributionRule> rules = toolBarDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (ToolBarContributionRule rule : rules) {
            if (rule instanceof PositionToolBarContributionRule) {
                return ((PositionToolBarContributionRule) rule).getPositionMode();
            }
        }
        return null;
    }

    @Nullable
    private String getParentGroup(String toolBarId, ToolBarContribution contribution) {
        ToolBarDefinition menuDefinition = toolBars.get(toolBarId);
        List<ToolBarContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (ToolBarContributionRule rule : rules) {
            if (rule instanceof GroupToolBarContributionRule) {
                return ((GroupToolBarContributionRule) rule).getGroupId();
            }
        }
        return null;
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

    @ParametersAreNonnullByDefault
    private class ToolBarGroupRecord {

        String groupId;
        SeparationMode separationMode;
        List<ToolBarGroupRecord> subGroups = new LinkedList<>();
        List<ToolBarContribution> contributions = new LinkedList<>();

        public ToolBarGroupRecord(String groupId) {
            this.groupId = groupId;
        }

        public ToolBarGroupRecord(String groupId, SeparationMode separationMode) {
            this(groupId);
            this.separationMode = separationMode;
        }
    }

    @ParametersAreNonnullByDefault
    private class ToolBarGroupRecordPathNode {

        List<ToolBarGroupRecord> records;
        int childIndex;

        public ToolBarGroupRecordPathNode(List<ToolBarGroupRecord> records) {
            this.records = records;
        }
    }
}
