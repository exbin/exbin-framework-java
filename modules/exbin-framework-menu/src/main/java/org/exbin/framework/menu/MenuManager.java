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
package org.exbin.framework.menu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.menu.api.DirectSubMenuContribution;
import org.exbin.framework.menu.api.GroupMenuContribution;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuContributionRule;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.RelativeMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.menu.api.SubMenuContributionRule;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.menu.api.MenuItemProvider;

/**
 * Menu manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuManager {

    private final Map<String, MenuDefinition> menus = new HashMap<>();

    public MenuManager() {
    }

    public void buildMenu(JMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new MenuWrapper(outputMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JPopupMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new PopupMenuWrapper(outputMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JMenuBar outputMenuBar, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new MenuBarWrapper(outputMenuBar), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    private void buildMenu(MenuOutput outputMenu, String menuId, ActionContextService activationUpdateService) {
        BuilderRecord builderRecord = new BuilderRecord();
        MenuDefinition menuDef = menus.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();

        // Build contributions tree
        for (MenuContribution contribution : menuDef.getContributions()) {
            String parentGroupId = null;
            String parentSubMenuId = null;
            PositionMenuContributionRule.PositionMode positionMode = null;
            SeparationMenuContributionRule.SeparationMode separationMode = null;
            List<String> afterIds = new ArrayList<>();
            List<String> beforeIds = new ArrayList<>();
            List<MenuContributionRule> rules = menuDef.getRules().get(contribution);
            for (MenuContributionRule rule : rules) {
                if (rule instanceof PositionMenuContributionRule) {
                    positionMode = ((PositionMenuContributionRule) rule).getPositionMode();
                } else if (rule instanceof SeparationMenuContributionRule) {
                    separationMode = ((SeparationMenuContributionRule) rule).getSeparationMode();
                } else if (rule instanceof RelativeMenuContributionRule) {
                    RelativeMenuContributionRule.NextToMode nextToMode = ((RelativeMenuContributionRule) rule).getNextToMode();
                    String contributionId = ((RelativeMenuContributionRule) rule).getContributionId();
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
                } else if (rule instanceof GroupMenuContributionRule) {
                    parentGroupId = ((GroupMenuContributionRule) rule).getGroupId();
                } else if (rule instanceof SubMenuContributionRule) {
                    parentSubMenuId = ((SubMenuContributionRule) rule).getSubMenuId();
                }
            }

            
            BuilderSectionRecord section = createSection(builderRecord, parentSubMenuId, positionMode, parentGroupId);

            if (contribution instanceof GroupMenuContribution) {
                String groupId = ((GroupMenuContribution) contribution).getGroupId();
                BuilderGroup2Record subGroup = section.groupsMap.get(groupId);
                if (subGroup == null) {
                    subGroup = new BuilderGroup2Record(groupId);
                    section.groupsMap.put(groupId, subGroup);
                    section.groups.add(subGroup);
                }
                subGroup.separationMode = separationMode;
                subGroup.afterItems.addAll(afterIds);
            } else {
                BuilderContributionRecord contributionRecord;
                String contributionId = null;
                if (contribution instanceof SubMenuContribution) {
                    contributionRecord = new BuilderMenuRecord((SubMenuContribution) contribution);
                } else if (contribution instanceof DirectSubMenuContribution) {
                    contributionRecord = new BuilderDirectSubMenuRecord(((DirectSubMenuContribution) contribution));
                } else if (contribution instanceof ActionMenuContribution) {
                    contributionRecord = new BuilderActionRecord((ActionMenuContribution) contribution);
                } else {
                    throw new IllegalStateException("Unsupported contribution type: " + contribution.getClass().getName());
                }

                if (contributionId != null && section.itemsMap.containsKey(contributionId)) {
                    throw new IllegalStateException("Contribution with id " + contributionId + " already exists");
                }

                contributionRecord.separationMode = separationMode;
                contributionRecord.afterItems.addAll(afterIds);

                // Convert before rules to after rules
                List<String> defferedAfterIds = section.afterMap.remove(contributionId);
                if (defferedAfterIds != null) {
                    contributionRecord.afterItems.addAll(defferedAfterIds);
                }
                for (String itemId : beforeIds) {
                    BuilderContributionRecord itemRecord = section.itemsMap.get(itemId);
                    if (itemRecord != null) {
                        itemRecord.afterItems.add(contributionId);
                    } else {
                        List<String> itemAfterIds = section.afterMap.get(itemId);
                        if (itemAfterIds == null) {
                            itemAfterIds = new ArrayList<>();
                            itemAfterIds.add(contributionId);
                            section.afterMap.put(itemId, itemAfterIds);
                        } else {
                            itemAfterIds.add(contributionId);
                        }
                    }
                }

                section.items.add(contributionRecord);
                if (contributionId != null) {
                    section.itemsMap.put(contributionId, contributionRecord);
                }
            }
        }

        // Generate menu
        List<BuilderSectionRecord> processing = new ArrayList<>();
        BuilderSectionRecord rootSection = builderRecord.subMenusMap.get("");
        rootSection.outputMenu = outputMenu;
        processing.add(rootSection);
        while (!processing.isEmpty()) {
            BuilderSectionRecord section = processing.get(processing.size() - 1);

            if (section.processingGroup >= section.groups.size()) {
                processing.remove(processing.size() - 1);
                continue;
            }
            
            // TODO: Groups ordering / contribution.afterItems;
            BuilderGroup2Record groups = section.groups.get(section.processingGroup);

            BuilderSectionRecord groupSection = groups == null || groups.positions == null ? null : groups.positions.get(groups.processingPosition);
            if (groupSection == null || groupSection.items.isEmpty()) {
                if (groups.processingPosition == PositionMenuContributionRule.PositionMode.BOTTOM_LAST) {
                    section.processingGroup++;
                } else {
                    groups.processingPosition = PositionMenuContributionRule.PositionMode.values()[groups.processingPosition.ordinal() + 1];
                }
                continue;
            }

            BuilderContributionRecord contribution = groupSection.items.get(0);
            // TODO: Items ordering / contribution.afterItems;
            {
                if (contribution instanceof BuilderActionRecord) {
                    Action action = ((BuilderActionRecord) contribution).contribution.getAction();
                    ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                    JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);
                    section.outputMenu.add(menuItem);
                } else if (contribution instanceof BuilderDirectSubMenuRecord) {
                    section.outputMenu.add(((BuilderDirectSubMenuRecord) contribution).menuItem);
                } else if (contribution instanceof BuilderMenuRecord) {
                    BuilderMenuRecord menuRecord = (BuilderMenuRecord) contribution;
                    BuilderSectionRecord subSection = builderRecord.subMenusMap.get(menuRecord.contributionId);
                    if (subSection != null) {
                        JMenu subMenu = UiUtils.createMenu();
                        Action action = menuRecord.contribution.getAction();
                        subMenu.setAction(action);
                        section.outputMenu.add(subMenu);
                        subSection.outputMenu = new MenuWrapper(subMenu);
                        processing.add(subSection);
                    }
                }
                groupSection.items.remove(0);
            }
        }

/*        
        // Sub menus
        for (MenuContribution contribution : menuDef.getContributions()) {
            if (!(contribution instanceof SubMenuContribution)) {
                continue;
            }

            SubMenuContribution subMenuContribution = ((SubMenuContribution) contribution);
            String subMenuId = ((SubMenuContribution) contribution).getSubMenuId();
            BuilderSubMenuRecord subMenuRecord = subMenus.get(subMenuId);
            if (subMenuRecord == null) {
                JMenu subMenu = UiUtils.createMenu();
                Action action = subMenuContribution.getAction();
                subMenu.setAction(action);
                subMenuRecord = new BuilderSubMenuRecord(subMenuId, new MenuWrapper(subMenu, outputMenu.isPopup()));
                subMenus.put(subMenuId, subMenuRecord);
                for (PositionMenuContributionRule.PositionMode mode : PositionMenuContributionRule.PositionMode.values()) {
                    BuilderGroupRecord menuGroupRecord = new BuilderGroupRecord(mode.name(), subMenuRecord.menuOutput);
                    subMenuRecord.groupsMap.put(mode.name(), menuGroupRecord);
                    subMenuRecord.groupRecords.add(menuGroupRecord);
                }
            }
        } */
        /*
        Map<String, BuilderSubMenuRecord> subMenus = collectSubMenus(outputMenu, menuId);

        if (subMenus == null) {
            return;
        }

        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builderRecord.add(new BuilderMenuRecord(subMenus.get("")));
        while (!builderRecord.isEmpty()) {
            BuilderMenuRecord processingNode = builderRecord.get(builderRecord.size() - 1);
            BuilderSubMenuRecord subMenu = processingNode.subMenu;
            String subMenuId = subMenu.subMenuId;
            MenuOutput output = subMenu.menuOutput;

            if (processingNode.currentGroup == processingNode.groups.size()) {
                builderRecord.remove(builderRecord.size() - 1);
                continue;
            }

            BuilderGroupOrder groupRecordNode = processingNode.groups.get(processingNode.currentGroup);

            if (groupRecordNode.currentGroupRecord == groupRecordNode.records.size()) {
                processingNode.currentGroup++;
                continue;
            }

            BuilderGroupRecord groupRecord = groupRecordNode.records.get(groupRecordNode.currentGroupRecord);

            if (groupRecordNode.currentContribution == groupRecord.contributions.size()) {
                groupRecordNode.currentContribution = 0;
                groupRecordNode.currentGroupRecord++;
                continue;
            }

                if (groupRecordNode.rootProcessed != null) {
                    // Perform insertion of all processed menuItem contributions
                    List<OrderingContribution> orderingPath = new LinkedList<>();

                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, groupRecordNode.rootProcessed));
                    while (!orderingPath.isEmpty()) {
                        OrderingContribution orderingContribution = orderingPath.get(orderingPath.size() - 1);
                        switch (orderingContribution.mode) {
                            case BEFORE: {
                                if (orderingContribution.handler.before.isEmpty()) {
                                    orderingContribution.mode = OrderingMode.ITEM;
                                } else {
                                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, orderingContribution.handler.before.remove(0)));
                                }
                                break;
                            }
                            case ITEM: {
                                boolean itemAdded = orderingContribution.handler.shouldCreate();
                                if (itemAdded) {
                                    if (processingNode.separatorQueued) {
                                        output.addSeparator();
                                        processingNode.separatorQueued = false;
                                    }
                                    orderingContribution.handler.finish();
                                }

                                processingNode.itemsAdded |= itemAdded;
                                orderingContribution.mode = OrderingMode.AFTER;
                                break;
                            }
                            case AFTER: {
                                if (orderingContribution.handler.after.isEmpty()) {
                                    orderingPath.remove(orderingPath.size() - 1);
                                } else {
                                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, orderingContribution.handler.after.remove(0)));
                                }
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        }
                    }

                    if (processingNode.itemsAdded && groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW) {
                        processingNode.itemsAdded = false;
                        processingNode.separatorQueued = true;
                    }

                    / *                if (!groupRecord.subGroups.isEmpty()) {
                    ProcessingNode subGroupNode = new ProcessingNode(processingNode.subMenu);
                    // TODO subGroupNode.records = groupRecord.subGroups;
                    processingPath.add(subGroupNode);
                } * /
                    groupRecordNode.rootProcessed = null;
                }

            MenuContribution contribution = groupRecord.contributions.get(groupRecordNode.currentContribution);
            groupRecordNode.currentContribution++;

            if (processingNode.itemsAdded && (groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND)) {
                processingNode.itemsAdded = false;
                processingNode.separatorQueued = true;
            }
            
            // Process contribution
            ContributionHandler handler = createProcessedContribution(output, contribution, menuId, subMenuId, subMenu, subMenus, buttonGroups, activationUpdateService);
            handler.process();
            
            if (contribution instanceof SubMenuContribution) {
                BuilderSubMenuRecord subMenuRecord = subMenus.get(((SubMenuContribution) contribution).getSubMenuId());
                builderRecord.add(new BuilderMenuRecord(subMenuRecord));
            } */

//            // Process all contributions, but don't insert them yet
/*            List<QueuedContribution> queue = new LinkedList<>();
            queue.add(new QueuedContribution(null, contribution));
            while (!queue.isEmpty()) {
                final QueuedContribution next = queue.remove(0);
                ContributionHandler handler = createProcessedContribution(output, next.contribution, menuId, subMenuId, subMenu, subMenus, activationUpdateService);

                handler.process();

                if (next.parent == null) {
                    groupRecordNode.rootProcessed = handler;
                }
                String actionId = handler.getActionId();
                RelativeMenuContributionRule.NextToMode nextToMode = next.nextToMode;
                if (nextToMode != null) {
                    switch (nextToMode) {
                        case BEFORE: {
                            next.parent.before.add(handler);
                            break;
                        }
                        case AFTER: {
                            next.parent.after.add(handler);
                            break;
                        }
                    }
                }

                List<MenuContribution> nextToBefore = subMenu.beforeItem.get(actionId);
                if (nextToBefore != null) {
                    nextToBefore.forEach((menuContribution) -> {
                        queue.add(new QueuedContribution(handler, menuContribution, RelativeMenuContributionRule.NextToMode.BEFORE));
                    });
                }

                List<MenuContribution> nextToAfter = subMenu.afterItem.get(actionId);
                if (nextToAfter != null) {
                    nextToAfter.forEach((menuContribution) -> {
                        queue.add(new QueuedContribution(handler, menuContribution, RelativeMenuContributionRule.NextToMode.AFTER));
                    });
                }

            }
        } */
    }

    @Nonnull
    private BuilderSectionRecord createSection(BuilderRecord builderRecord, @Nullable String subMenuId, @Nullable PositionMenuContributionRule.PositionMode positionMode, @Nullable String groupId) {
        if (subMenuId == null) {
            subMenuId = "";
        }
        if (positionMode == null) {
            positionMode = PositionMenuContributionRule.PositionMode.DEFAULT;
        }
        BuilderSectionRecord subMenuSection = builderRecord.subMenusMap.get(subMenuId);
        if (subMenuSection == null) {
            subMenuSection = new BuilderSectionRecord(subMenuId, positionMode, null, builderRecord.menuOutput);
            builderRecord.subMenusMap.put(subMenuId, subMenuSection);
        }
        if (groupId == null && positionMode == PositionMenuContributionRule.PositionMode.DEFAULT) {
            return subMenuSection;
        }

        if (groupId == null) {
            groupId = "";
        }

        BuilderGroup2Record groupRecord = subMenuSection.groupsMap.get(groupId);
        if (groupRecord == null) {
            groupRecord = new BuilderGroup2Record(groupId);
            subMenuSection.groupsMap.put(groupId, groupRecord);
            subMenuSection.groups.add(groupRecord);
        }
        BuilderSectionRecord section = groupRecord.positions == null ? null : groupRecord.positions.get(positionMode);
        if (section == null) {
            section = new BuilderSectionRecord(subMenuId, positionMode, groupId, subMenuSection.outputMenu);
            if (groupRecord.positions == null) {
                groupRecord.positions = new HashMap<>();
            }
            groupRecord.positions.put(positionMode, section);
        }

        return section;
    }

    @Nullable
    private Map<String, BuilderSubMenuRecord> collectSubMenus(MenuOutput outputMenu, String menuId) {
        MenuDefinition menuDef = menus.get(menuId);

        if (menuDef == null) {
            return null;
        }

        Map<String, BuilderSubMenuRecord> subMenus = new HashMap<>();

        // Create list of build-in groups
        BuilderSubMenuRecord rootRecord = new BuilderSubMenuRecord("", outputMenu);
        for (PositionMenuContributionRule.PositionMode mode : PositionMenuContributionRule.PositionMode.values()) {
            BuilderGroupRecord menuGroupRecord = new BuilderGroupRecord(mode.name(), outputMenu);
            rootRecord.groupsMap.put(mode.name(), menuGroupRecord);
            rootRecord.groupRecords.add(menuGroupRecord);
        }
        subMenus.put("", rootRecord);

        // Collect submenus
        for (MenuContribution contribution : menuDef.getContributions()) {
            if (!(contribution instanceof SubMenuContribution)) {
                continue;
            }

            SubMenuContribution subMenuContribution = ((SubMenuContribution) contribution);
            String subMenuId = ((SubMenuContribution) contribution).getSubMenuId();
            BuilderSubMenuRecord subMenuRecord = subMenus.get(subMenuId);
            if (subMenuRecord == null) {
                JMenu subMenu = UiUtils.createMenu();
                Action action = subMenuContribution.getAction();
                subMenu.setAction(action);
                subMenuRecord = new BuilderSubMenuRecord(subMenuId, new MenuWrapper(subMenu, outputMenu.isPopup()));
                subMenus.put(subMenuId, subMenuRecord);
                for (PositionMenuContributionRule.PositionMode mode : PositionMenuContributionRule.PositionMode.values()) {
                    BuilderGroupRecord menuGroupRecord = new BuilderGroupRecord(mode.name(), subMenuRecord.menuOutput);
                    subMenuRecord.groupsMap.put(mode.name(), menuGroupRecord);
                    subMenuRecord.groupRecords.add(menuGroupRecord);
                }
            }
        }

        // Build full tree of groups
        /* for (MenuContribution contribution : menuDef.getContributions()) {
            if (!(contribution instanceof GroupMenuContribution)) {
                continue;
            }
            String groupId = ((GroupMenuContribution) contribution).getGroupId();
            SeparationMenuContributionRule.SeparationMode separationMode = getSeparationMode(menuId, contribution);
            String parentGroupId = getParentGroup(menuId, contribution);
            String subMenuId = getSubMenuId(menuId, contribution);
            BuilderSubMenuRecord subMenu = subMenus.get(subMenuId);
            if (parentGroupId != null) {
                BuilderGroupRecord groupRecord = subMenu.groupsMap.get(parentGroupId);
                BuilderGroupRecord menuGroupRecord = new BuilderGroupRecord(groupId, subMenu.menuOutput);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                subMenu.groupsMap.put(groupId, menuGroupRecord);
            } else {
                PositionMenuContributionRule.PositionMode positionMode = getPositionMode(menuId, contribution);
                if (positionMode == null) {
                    positionMode = PositionMenuContributionRule.PositionMode.DEFAULT;
                }
                BuilderGroupRecord groupRecord = subMenu.groupsMap.get(positionMode.name());
                BuilderGroupRecord menuGroupRecord = new BuilderGroupRecord(groupId, subMenu.menuOutput);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                subMenu.groupsMap.put(groupId, menuGroupRecord);
            }
        }

        // Go thru all contributions and link them to its target group
        for (MenuContribution contribution : menuDef.getContributions()) {
            if ((contribution instanceof GroupMenuContribution)) {
                continue;
            }
            PositionMenuContributionRule.PositionMode positionMode = getPositionMode(menuId, contribution);
            String parentGroupId = getParentGroup(menuId, contribution);
            String subMenuId = getSubMenuId(menuId, contribution);
            BuilderSubMenuRecord subMenu = subMenus.get(subMenuId);
            if (positionMode != null) {
                BuilderGroupRecord menuGroupRecord = subMenu.groupsMap.get(positionMode.name());
                if (menuGroupRecord == null) {
                    throw new InvalidParameterException("Invalid parent group: " + positionMode.name());
                }
                menuGroupRecord.contributions.add(contribution);
            } else {
                if (parentGroupId != null) {
                    BuilderGroupRecord menuGroupRecord = subMenu.groupsMap.get(parentGroupId);
                    if (menuGroupRecord == null) {
                        throw new InvalidParameterException("Invalid parent group: " + parentGroupId);
                    }
                    menuGroupRecord.contributions.add(contribution);
                } else {
                    // TODO Rework for multiple rules and other stuff
                    RelativeMenuContributionRule relativeContributionRule = getRelativeToRule(menuId, contribution);
                    if (relativeContributionRule != null) {
                        // TODO
/ *                        switch (relativeContributionRule.getNextToMode()) {
                            case BEFORE: {
                                List<MenuContribution> contributions = subMenu.beforeItem.get(relativeContributionRule.getContributionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    subMenu.beforeItem.put(relativeContributionRule.getContributionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            case AFTER: {
                                List<MenuContribution> contributions = subMenu.afterItem.get(relativeContributionRule.getContributionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    subMenu.afterItem.put(relativeContributionRule.getContributionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        } * /
                    } else {
                        BuilderGroupRecord menuGroupRecord = subMenu.groupsMap.get(PositionMenuContributionRule.PositionMode.DEFAULT.name());
                        menuGroupRecord.contributions.add(contribution);
                    }
                }
            }
        } */

        return subMenus;
    }

    @Nonnull
    private ContributionHandler createProcessedContribution(MenuOutput output, MenuContribution contribution, String menuId, String subMenuId, BuilderSubMenuRecord subMenu, Map<String, BuilderSubMenuRecord> subMenus, Map<String, ButtonGroup> buttonGroups, ActionContextService activationUpdateService) {
        ContributionHandler processed;
        if (contribution instanceof ActionMenuContribution) {
            processed = new ContributionHandler() {
                JMenuItem menuItem;
                String actionId;

                @Override
                public void process() {
                    Action action = ((ActionMenuContribution) contribution).getAction();
                    actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                    ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                    menuItem = actionModule.actionToMenuItem(action, buttonGroups);
                }

                @Nonnull
                @Override
                String getActionId() {
                    return actionId == null ? "" : actionId;
                }

                @Override
                public boolean shouldCreate() {
                    if (output.isPopup()) {
                        Action action = ((ActionMenuContribution) contribution).getAction();
                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                        if (menuCreation != null) {
                            return menuCreation.shouldCreate(menuId, subMenuId);
                        }
                    }

                    return true;
                }

                @Override
                public void finish() {
                    Action action = ((ActionMenuContribution) contribution).getAction();
                    if (output.isPopup()) {
                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                        if (menuCreation != null) {
                            menuCreation.onCreate(menuItem, menuId, subMenuId);
                        }
                    }

                    output.add(menuItem);
                    Action menuItemAction = menuItem.getAction();
                    if (menuItemAction != null) {
                        finishMenuAction(menuItemAction, activationUpdateService);
                    }
                }
            };
        } else if (contribution instanceof SubMenuContribution) {
            processed = new ContributionHandler() {
                JMenu menuItem;
                String actionId;

                @Override
                public void process() {
                    SubMenuContribution subMenuContribution = (SubMenuContribution) contribution;
                    String subMenuId = subMenuContribution.getSubMenuId();
                    BuilderSubMenuRecord subMenuRecord = subMenus.get(subMenuId);
                    menuItem = ((MenuWrapper) subMenuRecord.getOutput()).menu;
                    Action action = subMenuContribution.getAction();
                    actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                    menuItem.setAction(action);
                }

                @Nonnull
                @Override
                String getActionId() {
                    return actionId == null ? "" : actionId;
                }

                @Override
                public boolean shouldCreate() {
                    if (output.isPopup()) {
                        Action action = menuItem.getAction();
                        if (action != null) {
                            ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                            if (menuCreation != null) {
                                return menuCreation.shouldCreate(menuId, subMenuId);
                            }
                        }
                    }

                    return menuItem.getMenuComponentCount() > 0;
                }

                @Override
                public void finish() {
                    Action action = menuItem.getAction();
                    if (output.isPopup() && action != null) {
                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                        if (menuCreation != null) {
                            menuCreation.onCreate(menuItem, menuId, subMenuId);
                        }
                    }

                    output.add(menuItem);
                    finishMenu(menuItem, activationUpdateService);
                    finishMenuAction(action, activationUpdateService);
                }
            };
        } else if (contribution instanceof DirectSubMenuContribution) {
            processed = new ContributionHandler() {
                DirectSubMenuContribution directMenuContribution;
                JMenuItem menuItem;
                String actionId;

                @Override
                public void process() {
                    directMenuContribution = (DirectSubMenuContribution) contribution;
                    menuItem = directMenuContribution.getMenuItemProvider().createMenuItem();
                    Action action = menuItem.getAction();
                    if (action != null) {
                        actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                    }
                }

                @Nonnull
                @Override
                String getActionId() {
                    return actionId == null ? "" : actionId;
                }

                @Override
                public boolean shouldCreate() {
                    if (output.isPopup()) {
                        Action action = menuItem.getAction();
                        if (action != null) {
                            ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                            if (menuCreation != null) {
                                return menuCreation.shouldCreate(menuId, subMenuId);
                            }
                        }
                    }

                    return true;
                }

                @Override
                public void finish() {
                    Action action = menuItem.getAction();
                    if (output.isPopup() && action != null) {
                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                        if (menuCreation != null) {
                            menuCreation.onCreate(menuItem, menuId, subMenuId);
                        }
                    }

                    output.add(menuItem);
                    finishMenuItem(menuItem, activationUpdateService);
                    finishMenuAction(action, activationUpdateService);
                }
            };
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        return processed;
    }

    private void finishMenuAction(@Nullable Action action, ActionContextService activationUpdateService) {
        if (action != null) {
            activationUpdateService.requestUpdate(action);
        }
    }

    private void finishMenuItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, activationUpdateService);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, activationUpdateService);
            }
        }
    }

    private void finishMenu(JMenu menu, ActionContextService activationUpdateService) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            if (menuItem == null) {
                continue;
            }
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, activationUpdateService);
            }
            if (menuItem instanceof JMenu) {
                finishMenu((JMenu) menuItem, activationUpdateService);
            }
        }
    }

    boolean menuGroupExists(String menuId, String groupId) {
        MenuDefinition menuDefs = menus.get(menuId);
        if (menuDefs == null) {
            return false;
        }

        if (menuDefs.getContributions().stream().anyMatch((contribution) -> (contribution instanceof GroupMenuContribution && ((GroupMenuContribution) contribution).getGroupId().equals(groupId)))) {
            return true;
        }

        return false;
    }

    /*    @Nullable
    private GroupMenuContribution getGroup(String menuId, String groupId) {
        MenuDefinition menuDefinition = menus.get(menuId);
        for (MenuContribution contribution : menuDefinition.getContributions()) {
            if (contribution instanceof GroupMenuContribution) {
                if (((GroupMenuContribution) contribution).getGroupId().equals(groupId)) {
                    return (GroupMenuContribution) contribution;
                }
            }
        }
        return null;
    }

    @Nullable
    private SeparationMenuContributionRule.SeparationMode getSeparationMode(String menuId, MenuContribution contribution) {
        MenuDefinition menuDefinition = menus.get(menuId);
        List<MenuContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (MenuContributionRule rule : rules) {
            if (rule instanceof SeparationMenuContributionRule) {
                return ((SeparationMenuContributionRule) rule).getSeparationMode();
            }
        }
        return null;
    }

    @Nullable
    private PositionMenuContributionRule.PositionMode getPositionMode(String menuId, MenuContribution contribution) {
        MenuDefinition menuDefinition = menus.get(menuId);
        List<MenuContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (MenuContributionRule rule : rules) {
            if (rule instanceof PositionMenuContributionRule) {
                return ((PositionMenuContributionRule) rule).getPositionMode();
            }
        }
        return null;
    }

    @Nullable
    private String getParentGroup(String menuId, MenuContribution contribution) {
        MenuDefinition menuDefinition = menus.get(menuId);
        List<MenuContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (MenuContributionRule rule : rules) {
            if (rule instanceof GroupMenuContributionRule) {
                return ((GroupMenuContributionRule) rule).getGroupId();
            }
        }
        return null;
    }

    @Nullable
    private RelativeMenuContributionRule getRelativeToRule(String menuId, MenuContribution contribution) {
        MenuDefinition menuDefinition = menus.get(menuId);
        List<MenuContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return null;
        }
        for (MenuContributionRule rule : rules) {
            if (rule instanceof RelativeMenuContributionRule) {
                return (RelativeMenuContributionRule) rule;
            }
        }
        return null;
    }

    @Nonnull
    private String getSubMenuId(String menuId, MenuContribution contribution) {
        MenuDefinition menuDefinition = menus.get(menuId);
        List<MenuContributionRule> rules = menuDefinition.getRules().get(contribution);
        if (rules == null) {
            return "";
        }
        for (MenuContributionRule rule : rules) {
            if (rule instanceof SubMenuContributionRule) {
                return ((SubMenuContributionRule) rule).getSubMenuId();
            }
        }
        return "";
    } */
    public void unregisterMenu(String menuId) {
        MenuDefinition definition = menus.get(menuId);
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
            menus.remove(menuId);
        }
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (MenuDefinition menuDef : menus.values()) {
            for (MenuContribution contribution : menuDef.getContributions()) {
                if (contribution instanceof ActionMenuContribution) {
                    actions.add(((ActionMenuContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @ParametersAreNonnullByDefault
    private static class QueuedContribution {

        ContributionHandler parent;
        MenuContribution contribution;
        RelativeMenuContributionRule.NextToMode nextToMode = null;

        public QueuedContribution(@Nullable ContributionHandler parent, MenuContribution contribution) {
            this.parent = parent;
            this.contribution = contribution;
        }

        public QueuedContribution(@Nullable ContributionHandler parent, MenuContribution contribution, RelativeMenuContributionRule.NextToMode nextToMode) {
            this.parent = parent;
            this.contribution = contribution;
            this.nextToMode = nextToMode;
        }
    }

    @ParametersAreNonnullByDefault
    private static class OrderingContribution {

        OrderingMode mode;
        ContributionHandler handler;

        public OrderingContribution(OrderingMode mode, ContributionHandler handler) {
            this.mode = mode;
            this.handler = handler;
        }
    }

    private enum OrderingMode {
        BEFORE, ITEM, AFTER
    };

    public void registerMenu(String menuId, String moduleId) {
        ObjectUtils.requireNonNull(menuId);
        ObjectUtils.requireNonNull(moduleId);

        MenuDefinition menu = menus.get(menuId);
        if (menu != null) {
            if (menu.getModuleId().isPresent()) {
                throw new IllegalStateException("Menu with ID " + menuId + " already exists.");
            } else {
                menu.setModuleId(moduleId);
                return;
            }
        }

        MenuDefinition menuDefinition = new MenuDefinition(moduleId);
        menus.put(menuId, menuDefinition);
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, Action action) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        ActionMenuContribution menuContribution = new ActionMenuContribution(action);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName) {
        Action subMenuAction = new AbstractAction(subMenuName) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        return registerMenuItem(menuId, pluginId, subMenuId, subMenuAction);
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, MenuItemProvider menuProvider) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        DirectSubMenuContribution menuContribution = new DirectSubMenuContribution(menuProvider);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuGroup(String menuId, String pluginId, String groupId) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        GroupMenuContribution groupContribution = new GroupMenuContribution(groupId);
        menuDef.getContributions().add(groupContribution);
        return groupContribution;
    }

    public void registerMenuRule(MenuContribution contribution, MenuContributionRule rule) {
        MenuDefinition match = null;
        for (MenuDefinition menuDef : menus.values()) {
            if (menuDef.getContributions().contains(contribution)) {
                match = menuDef;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid menu contribution rule");
        }

        List<MenuContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    @ParametersAreNonnullByDefault
    private static class BuilderRecord {

        MenuOutput menuOutput;
        Map<String, BuilderSectionRecord> subMenusMap = new HashMap<>();
    }

    @ParametersAreNonnullByDefault
    private static class BuilderSectionRecord {

        String subMenuId;
        String groupId;
        PositionMenuContributionRule.PositionMode positionMode;
        MenuOutput outputMenu;

        SeparationMenuContributionRule.SeparationMode separationMode;
        final Set<String> afterItems = new HashSet<>();

        Map<String, BuilderGroup2Record> groupsMap = new HashMap<>();
        Map<String, BuilderContributionRecord> itemsMap = new HashMap<>();
        Map<String, List<String>> afterMap = new HashMap<>();
        Set<String> processedItems = new HashSet<>();

        List<BuilderGroup2Record> groups = new ArrayList<>();
        List<BuilderContributionRecord> items = new ArrayList<>();
        
        int processingGroup = 0;

        public BuilderSectionRecord(String subMenuId, PositionMenuContributionRule.PositionMode positionMode, @Nullable String groupId, MenuOutput outputMenu) {
            this.subMenuId = subMenuId;
            this.positionMode = positionMode;
            this.groupId = groupId;
            this.outputMenu = outputMenu;
        }

        public BuilderSectionRecord(String subMenuId, PositionMenuContributionRule.PositionMode positionMode, @Nullable String groupId, MenuOutput outputMenu, SeparationMenuContributionRule.SeparationMode separationMode) {
            this(subMenuId, positionMode, groupId, outputMenu);
            this.separationMode = separationMode;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderActionRecord extends BuilderContributionRecord {

        final ActionMenuContribution contribution;
        final String contributionId;

        public BuilderActionRecord(ActionMenuContribution contribution) {
            this.contribution = contribution;
            contributionId = (String) contribution.getAction().getValue(ActionConsts.ACTION_ID);
        }
    
        /* public void process() {
            Action action = ((ActionMenuContribution) contribution).getAction();
            actionId = (String) action.getValue(ActionConsts.ACTION_ID);
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            menuItem = actionModule.actionToMenuItem(action, buttonGroups);
        }

        public boolean shouldCreate() {
            if (output.isPopup()) {
                Action action = ((ActionMenuContribution) contribution).getAction();
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    return menuCreation.shouldCreate(menuId, subMenuId);
                }
            }

            return true;
        }

        public void finish() {
            Action action = ((ActionMenuContribution) contribution).getAction();
            if (output.isPopup()) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            output.add(menuItem);
            Action menuItemAction = menuItem.getAction();
            if (menuItemAction != null) {
                finishMenuAction(menuItemAction, activationUpdateService);
            }
        } */
    }
    
    private static class BuilderGroup2Record {
        
        String groupId;

        SeparationMenuContributionRule.SeparationMode separationMode;
        final Set<String> afterItems = new HashSet<>();

        Map<PositionMenuContributionRule.PositionMode, BuilderSectionRecord> positions;
        PositionMenuContributionRule.PositionMode processingPosition = PositionMenuContributionRule.PositionMode.TOP;

        public BuilderGroup2Record(String groupId) {
            this.groupId = groupId;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderDirectSubMenuRecord extends BuilderContributionRecord {

        DirectSubMenuContribution contribution;
        String contributionId = null;
        JMenuItem menuItem;

        public BuilderDirectSubMenuRecord(DirectSubMenuContribution contribution) {
            this.contribution = contribution;
            menuItem = contribution.getMenuItemProvider().createMenuItem();
            Action action = menuItem.getAction();
            if (action != null) {
                contributionId = (String) action.getValue(ActionConsts.ACTION_ID);
            }
        }
    
    /*    public boolean shouldCreate() {
            if (output.isPopup()) {
                Action action = menuItem.getAction();
                if (action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        return menuCreation.shouldCreate(menuId, subMenuId);
                    }
                }
            }

            return true;
        }

        public void finish() {
            Action action = menuItem.getAction();
            if (output.isPopup() && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            output.add(menuItem);
            finishMenuItem(menuItem, activationUpdateService);
            finishMenuAction(action, activationUpdateService);
        } */
    }

    @ParametersAreNonnullByDefault
    private static class BuilderMenuRecord extends BuilderContributionRecord {

        SubMenuContribution contribution;
        String contributionId;
//        JMenuItem menuItem;

        public BuilderMenuRecord(SubMenuContribution contribution) {
            this.contribution = contribution;
            this.contributionId = contribution.getSubMenuId();
        }

/*        public void process() {
            SubMenuContribution subMenuContribution = (SubMenuContribution) contribution;
            String subMenuId = subMenuContribution.getSubMenuId();
            BuilderSubMenuRecord subMenuRecord = subMenus.get(subMenuId);
            menuItem = ((MenuWrapper) subMenuRecord.getOutput()).menu;
            Action action = subMenuContribution.getAction();
            actionId = (String) action.getValue(ActionConsts.ACTION_ID);
            menuItem.setAction(action);
        }

        @Override
        public boolean shouldCreate() {
            if (output.isPopup()) {
                Action action = menuItem.getAction();
                if (action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        return menuCreation.shouldCreate(menuId, subMenuId);
                    }
                }
            }

            return menuItem.getMenuComponentCount() > 0;
        }

        public void finish() {
            Action action = menuItem.getAction();
            if (output.isPopup() && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            output.add(menuItem);
            finishMenu(menuItem, activationUpdateService);
            finishMenuAction(action, activationUpdateService);
        } */
    }

    private static class BuilderContributionRecord {

        SeparationMenuContributionRule.SeparationMode separationMode;
        final Set<String> afterItems = new HashSet<>();

    }

    @ParametersAreNonnullByDefault
    private static class BuilderSubMenuRecord {

        String subMenuId;
        MenuOutput menuOutput;
        List<BuilderGroupRecord> groupRecords = new LinkedList<>();
        Map<String, BuilderGroupRecord> groupsMap = new HashMap<>();
        List<ContributionHandler> items = new ArrayList<>();
        // list of contribution assigned to specific action Id
        Map<String, List<ContributionHandler>> beforeItem = new HashMap<>();
        // list of contribution assigned to specific action Id
        Map<String, List<ContributionHandler>> afterItem = new HashMap<>();

        public BuilderSubMenuRecord(String subMenuId, MenuOutput menuOutput) {
            this.subMenuId = subMenuId;
            this.menuOutput = menuOutput;
        }

        @Nonnull
        public String getSubMenuId() {
            return subMenuId;
        }

        @Nonnull
        public MenuOutput getOutput() {
            return menuOutput;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderGroupRecord {

        String groupId;
        MenuOutput outputMenu;
        SeparationMenuContributionRule.SeparationMode separationMode;
        PositionMenuContributionRule.PositionMode positionMode;
        List<BuilderGroupRecord> subGroups = new LinkedList<>();
        List<MenuContribution> contributions = new LinkedList<>();

        public BuilderGroupRecord(String groupId, MenuOutput outputMenu) {
            this.groupId = groupId;
            this.outputMenu = outputMenu;
        }

        public BuilderGroupRecord(String groupId, MenuOutput outputMenu, SeparationMenuContributionRule.SeparationMode separationMode) {
            this(groupId, outputMenu);
            this.separationMode = separationMode;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderGroupOrder {

        List<BuilderGroupRecord> records;
        int currentGroupRecord = 0;
        int currentContribution = 0;
        ContributionHandler rootProcessed = null;

        public BuilderGroupOrder(List<BuilderGroupRecord> records) {
            this.records = records;
        }
    }

    private static abstract class ContributionHandler {

        List<ContributionHandler> before = new LinkedList<>();
        List<ContributionHandler> after = new LinkedList<>();

        abstract void process();

        @Nonnull
        abstract String getActionId();

        abstract boolean shouldCreate();

        abstract void finish();
    }

    @ParametersAreNonnullByDefault
    private static interface MenuOutput {

        void add(JMenu menuItem);

        void add(JMenuItem menuItem);

        void addSeparator();

        boolean isPopup();
    }

    @ParametersAreNonnullByDefault
    private static class MenuBarWrapper implements MenuOutput {

        private final JMenuBar menuBar;

        public MenuBarWrapper(JMenuBar menuBar) {
            this.menuBar = menuBar;
        }

        @Override
        public void add(JMenu menuItem) {
            menuBar.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menuBar.add(menuItem);
        }

        @Override
        public void addSeparator() {
        }

        @Override
        public boolean isPopup() {
            return false;
        }
    }

    @ParametersAreNonnullByDefault
    private static class PopupMenuWrapper implements MenuOutput {

        private final JPopupMenu menu;

        public PopupMenuWrapper(JPopupMenu menu) {
            this.menu = menu;
        }

        @Override
        public void add(JMenu menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Override
        public boolean isPopup() {
            return true;
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuWrapper implements MenuOutput {

        private final JMenu menu;
        private final boolean popup;

        public MenuWrapper(JMenu menu, boolean popup) {
            this.menu = menu;
            this.popup = popup;
        }

        public MenuWrapper(JMenu menu) {
            this.menu = menu;
            this.popup = false;
        }

        @Override
        public void add(JMenu menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Override
        public boolean isPopup() {
            return popup;
        }

        @Nonnull
        public JMenu getMenu() {
            return menu;
        }
    }
}
