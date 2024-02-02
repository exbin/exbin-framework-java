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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionToolBarContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarContribution;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.action.gui.DropDownButton;

/**
 * Toolbar handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarHandler {

    /**
     * Tool bar records: tool bar id -> tool bar definition.
     */
    private Map<String, ToolBarDefinition> toolBars = new HashMap<>();

    /**
     * Tool bar group records: tool bar id -> tool bar group.
     */
    private Map<String, List<ToolBarGroup>> toolBarGroups = new HashMap<>();

    /**
     * Tool bar modified flags.
     */
    private Set<String> toolBarModified = new HashSet<>();

    /**
     * Map of plugins usage per tool bar id.
     */
    private Map<String, String> pluginsUsage = new HashMap<>();

    public ToolBarHandler() {
    }

    public void buildToolBar(JToolBar targetToolBar, String toolBarId) {
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
        List<ToolBarGroup> groups = toolBarGroups.get(toolBarId);
        if (groups != null) {
            for (ToolBarGroup group : groups) {
                String groupId = group.getGroupId();
                SeparationMode separationMode = group.getSeparationMode();
                ToolBarPosition position = group.getPosition();
                if (position.getBasicMode() != PositionMode.UNSPECIFIED) {
                    ToolBarGroupRecord groupRecord = groupsMap.get(position.getBasicMode().name());
                    ToolBarGroupRecord toolBarGroupRecord = new ToolBarGroupRecord(groupId);
                    toolBarGroupRecord.separationMode = separationMode;
                    groupRecord.subGroups.add(toolBarGroupRecord);
                    groupsMap.put(groupId, toolBarGroupRecord);
                } else {
                    ToolBarGroupRecord groupRecord = groupsMap.get(position.getGroupId());
                    ToolBarGroupRecord toolBarGroupRecord = new ToolBarGroupRecord(groupId);
                    toolBarGroupRecord.separationMode = separationMode;
                    groupRecord.subGroups.add(toolBarGroupRecord);
                    groupsMap.put(groupId, toolBarGroupRecord);
                }
            }
        }

        // Go thru all contributions and link them to its target group
        for (ToolBarContribution contribution : toolBarDef.getContributions()) {
            ToolBarPosition toolBarPosition = contribution.getToolBarPosition();
            if (toolBarPosition.getBasicMode() != PositionMode.UNSPECIFIED) {
                ToolBarGroupRecord toolBarGroupRecord = groupsMap.get(toolBarPosition.getBasicMode().name());
                toolBarGroupRecord.contributions.add(contribution);
            } else {
                ToolBarGroupRecord toolBarGroupRecord = groupsMap.get(toolBarPosition.getGroupId());
                toolBarGroupRecord.contributions.add(contribution);
            }
        }

        processToolBarGroup(groupRecords, targetToolBar);
    }

    private void processToolBarGroup(List<ToolBarGroupRecord> groups, JToolBar targetToolBar) {
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
                targetToolBar.addSeparator();
                separatorQueued = false;
            }

            for (ToolBarContribution contribution : groupRecord.contributions) {
                if (separatorQueued) {
                    targetToolBar.addSeparator();
                    separatorQueued = false;
                }

                if (contribution instanceof ActionToolBarContribution) {
                    Action action = ((ActionToolBarContribution) contribution).getAction();
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

                    targetToolBar.add(toolBarItem);
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
    private JComponent createDefaultToolBarItem(Action action) {
        JButton newItem = new JButton(action);
        newItem.setFocusable(false);
        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return newItem;
    }

    public void registerToolBar(String toolBarId, String pluginId) {
        if (toolBarId == null) {
            throw new NullPointerException("Tool Bar Id cannot be null");
        }
        if (pluginId == null) {
            throw new NullPointerException("Plugin Id cannot be null");
        }

        ToolBarDefinition toolBar = toolBars.get(toolBarId);
        if (toolBar != null) {
            throw new IllegalStateException("Tool bar with ID " + toolBarId + " already exists.");
        }

        ToolBarDefinition toolBarDefinition = new ToolBarDefinition(pluginId);
        toolBars.put(toolBarId, toolBarDefinition);
    }

    public void registerToolBarGroup(String toolBarId, ToolBarGroup toolBarGroup) {
        List<ToolBarGroup> groups = toolBarGroups.get(toolBarId);
        if (groups == null) {
            groups = new LinkedList<>();
            toolBarGroups.put(toolBarId, groups);
        }
        groups.add(toolBarGroup);
    }

    public void registerToolBarItem(String toolBarId, String pluginId, Action action, ToolBarPosition position) {
        ToolBarDefinition toolBarDef = toolBars.get(toolBarId);
        if (toolBarDef == null) {
            throw new IllegalStateException("Tool bar with Id " + toolBarId + " doesn't exist");
        }

        ActionToolBarContribution toolBarContribution = new ActionToolBarContribution(action, position);
        toolBarDef.getContributions().add(toolBarContribution);
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
