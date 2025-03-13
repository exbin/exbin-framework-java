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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    /**
     * Menu records: menuItem id -> menuItem definition.
     */
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
        Map<String, SubMenuRecord> subMenus = collectSubMenus(outputMenu, menuId);

        if (subMenus == null) {
            return;
        }

        for (Map.Entry<String, SubMenuRecord> entry : subMenus.entrySet()) {
            SubMenuRecord subMenu = entry.getValue();
            String subMenuId = entry.getKey();
            List<MenuGroupRecord> groups = subMenu.groupRecords;
            List<MenuGroupRecordPathNode> processingPath = new LinkedList<>();
            processingPath.add(new MenuGroupRecordPathNode(groups));

            boolean separatorQueued = false;
            boolean itemsAdded = false;

            while (!processingPath.isEmpty()) {
                MenuGroupRecordPathNode pathNode = processingPath.get(processingPath.size() - 1);
                if (pathNode.childIndex == pathNode.records.size()) {
                    processingPath.remove(processingPath.size() - 1);
                    continue;
                }

                MenuGroupRecord groupRecord = pathNode.records.get(pathNode.childIndex);
                MenuOutput output = groupRecord.outputMenu;
                pathNode.childIndex++;

                if (itemsAdded && (groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND)) {
                    itemsAdded = false;
                    separatorQueued = true;
                }

                for (MenuContribution contribution : groupRecord.contributions) {
                    // Process all contributions, but don't insert them yet
                    List<QueuedContribution> queue = new LinkedList<>();
                    queue.add(new QueuedContribution(null, contribution));
                    ProcessedContribution rootProcessed = null;
                    while (!queue.isEmpty()) {
                        final QueuedContribution next = queue.remove(0);
                        ProcessedContribution processed = createProcessedContribution(output, next.contribution, menuId, subMenuId, subMenu, subMenus, activationUpdateService);

                        processed.process();
                        if (next.parent == null) {
                            rootProcessed = processed;
                        }
                        String actionId = processed.getActionId();
                        RelativeMenuContributionRule.NextToMode nextToMode = next.nextToMode;
                        if (nextToMode != null) {
                            switch (nextToMode) {
                                case BEFORE: {
                                    next.parent.before.add(processed);
                                    break;
                                }
                                case AFTER: {
                                    next.parent.after.add(processed);
                                    break;
                                }
                            }
                        }

                        List<MenuContribution> nextToBefore = subMenu.beforeItem.get(actionId);
                        if (nextToBefore != null) {
                            nextToBefore.forEach((menuContribution) -> {
                                queue.add(new QueuedContribution(processed, menuContribution, RelativeMenuContributionRule.NextToMode.BEFORE));
                            });
                        }

                        List<MenuContribution> nextToAfter = subMenu.afterItem.get(actionId);
                        if (nextToAfter != null) {
                            nextToAfter.forEach((menuContribution) -> {
                                queue.add(new QueuedContribution(processed, menuContribution, RelativeMenuContributionRule.NextToMode.AFTER));
                            });
                        }
                    }

                    // Perform insertion of all processed menuItem contributions
                    List<OrderingContribution> orderingPath = new LinkedList<>();

                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, rootProcessed));
                    while (!orderingPath.isEmpty()) {
                        OrderingContribution orderingContribution = orderingPath.get(orderingPath.size() - 1);
                        switch (orderingContribution.mode) {
                            case BEFORE: {
                                if (orderingContribution.processed.before.isEmpty()) {
                                    orderingContribution.mode = OrderingMode.ITEM;
                                } else {
                                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, orderingContribution.processed.before.remove(0)));
                                }
                                break;
                            }
                            case ITEM: {
                                boolean itemAdded = orderingContribution.processed.shouldCreate();
                                if (itemAdded) {
                                    if (separatorQueued) {
                                        output.addSeparator();
                                        separatorQueued = false;
                                    }
                                    orderingContribution.processed.finish();
                                }

                                itemsAdded |= itemAdded;
                                orderingContribution.mode = OrderingMode.AFTER;
                                break;
                            }
                            case AFTER: {
                                if (orderingContribution.processed.after.isEmpty()) {
                                    orderingPath.remove(orderingPath.size() - 1);
                                } else {
                                    orderingPath.add(new OrderingContribution(OrderingMode.BEFORE, orderingContribution.processed.after.remove(0)));
                                }
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        }
                    }
                }

                if (itemsAdded && groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW) {
                    itemsAdded = false;
                    separatorQueued = true;
                }

                if (!groupRecord.subGroups.isEmpty()) {
                    processingPath.add(new MenuGroupRecordPathNode(groupRecord.subGroups));
                }
            }
        }
    }

    @Nullable
    private Map<String, SubMenuRecord> collectSubMenus(MenuOutput outputMenu, String menuId) {
        MenuDefinition menuDef = menus.get(menuId);

        if (menuDef == null) {
            return null;
        }

        Map<String, SubMenuRecord> subMenus = new HashMap<>();

        // Create list of build-in groups
        SubMenuRecord rootRecord = new SubMenuRecord("", outputMenu);
        for (PositionMenuContributionRule.PositionMode mode : PositionMenuContributionRule.PositionMode.values()) {
            MenuGroupRecord menuGroupRecord = new MenuGroupRecord(mode.name(), outputMenu);
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
            SubMenuRecord subMenuRecord = subMenus.get(subMenuId);
            if (subMenuRecord == null) {
                JMenu subMenu = UiUtils.createMenu();
                Action action = subMenuContribution.getAction();
                subMenu.setAction(action);
                subMenuRecord = new SubMenuRecord(subMenuId, new MenuWrapper(subMenu, outputMenu.isPopup()));
                subMenus.put(subMenuId, subMenuRecord);
                for (PositionMenuContributionRule.PositionMode mode : PositionMenuContributionRule.PositionMode.values()) {
                    MenuGroupRecord menuGroupRecord = new MenuGroupRecord(mode.name(), subMenuRecord.menuOutput);
                    subMenuRecord.groupsMap.put(mode.name(), menuGroupRecord);
                    subMenuRecord.groupRecords.add(menuGroupRecord);
                }
            }
        }

        // Build full tree of groups
        for (MenuContribution contribution : menuDef.getContributions()) {
            if (!(contribution instanceof GroupMenuContribution)) {
                continue;
            }
            String groupId = ((GroupMenuContribution) contribution).getGroupId();
            SeparationMenuContributionRule.SeparationMode separationMode = getSeparationMode(menuId, contribution);
            String parentGroupId = getParentGroup(menuId, contribution);
            String subMenuId = getSubMenuId(menuId, contribution);
            SubMenuRecord subMenu = subMenus.get(subMenuId);
            if (parentGroupId != null) {
                MenuGroupRecord groupRecord = subMenu.groupsMap.get(parentGroupId);
                MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId, subMenu.menuOutput);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                subMenu.groupsMap.put(groupId, menuGroupRecord);
            } else {
                PositionMenuContributionRule.PositionMode positionMode = getPositionMode(menuId, contribution);
                if (positionMode == null) {
                    positionMode = PositionMenuContributionRule.PositionMode.DEFAULT;
                }
                MenuGroupRecord groupRecord = subMenu.groupsMap.get(positionMode.name());
                MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId, subMenu.menuOutput);
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
            SubMenuRecord subMenu = subMenus.get(subMenuId);
            if (positionMode != null) {
                MenuGroupRecord menuGroupRecord = subMenu.groupsMap.get(positionMode.name());
                if (menuGroupRecord == null) {
                    throw new InvalidParameterException("Invalid parent group: " + positionMode.name());
                }
                menuGroupRecord.contributions.add(contribution);
            } else {
                if (parentGroupId != null) {
                    MenuGroupRecord menuGroupRecord = subMenu.groupsMap.get(parentGroupId);
                    if (menuGroupRecord == null) {
                        throw new InvalidParameterException("Invalid parent group: " + parentGroupId);
                    }
                    menuGroupRecord.contributions.add(contribution);
                } else {
                    // TODO Rework for multiple rules and other stuff
                    RelativeMenuContributionRule relativeContributionRule = getRelativeToRule(menuId, contribution);
                    if (relativeContributionRule != null) {
                        switch (relativeContributionRule.getNextToMode()) {
                            case BEFORE: {
                                List<MenuContribution> contributions = subMenu.beforeItem.get(relativeContributionRule.getContributionActionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    subMenu.beforeItem.put(relativeContributionRule.getContributionActionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            case AFTER: {
                                List<MenuContribution> contributions = subMenu.afterItem.get(relativeContributionRule.getContributionActionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    subMenu.afterItem.put(relativeContributionRule.getContributionActionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        }
                    } else {
                        MenuGroupRecord menuGroupRecord = subMenu.groupsMap.get(PositionMenuContributionRule.PositionMode.DEFAULT.name());
                        menuGroupRecord.contributions.add(contribution);
                    }
                }
            }
        }

        return subMenus;
    }

    @Nonnull
    private ProcessedContribution createProcessedContribution(MenuOutput output, MenuContribution contribution, String menuId, String subMenuId, SubMenuRecord subMenu, Map<String, SubMenuRecord> subMenus, ActionContextService activationUpdateService) {
        ProcessedContribution processed;
        if (contribution instanceof ActionMenuContribution) {
            processed = new ProcessedContribution() {
                JMenuItem menuItem;
                String actionId;

                @Override
                public void process() {
                    Action action = ((ActionMenuContribution) contribution).getAction();
                    actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                    ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                    menuItem = actionModule.actionToMenuItem(action, subMenu.buttonGroups);
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
            processed = new ProcessedContribution() {
                JMenu menuItem;
                String actionId;

                @Override
                public void process() {
                    SubMenuContribution subMenuContribution = (SubMenuContribution) contribution;
                    String subMenuId = subMenuContribution.getSubMenuId();
                    SubMenuRecord subMenuRecord = subMenus.get(subMenuId);
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

                    return true; // TODO menuItem.getMenuComponentCount() > 0;
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
            processed = new ProcessedContribution() {
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

    @Nullable
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
    }

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

    private static abstract class ProcessedContribution {

        List<ProcessedContribution> before = new LinkedList<>();
        List<ProcessedContribution> after = new LinkedList<>();

        abstract void process();

        @Nonnull
        abstract String getActionId();

        abstract boolean shouldCreate();

        abstract void finish();
    }

    @ParametersAreNonnullByDefault
    private static class QueuedContribution {

        ProcessedContribution parent;
        MenuContribution contribution;
        RelativeMenuContributionRule.NextToMode nextToMode = null;

        public QueuedContribution(@Nullable ProcessedContribution parent, MenuContribution contribution) {
            this.parent = parent;
            this.contribution = contribution;
        }

        public QueuedContribution(@Nullable ProcessedContribution parent, MenuContribution contribution, RelativeMenuContributionRule.NextToMode nextToMode) {
            this.parent = parent;
            this.contribution = contribution;
            this.nextToMode = nextToMode;
        }
    }

    @ParametersAreNonnullByDefault
    private static class OrderingContribution {

        OrderingMode mode;
        ProcessedContribution processed;

        public OrderingContribution(OrderingMode mode, ProcessedContribution processed) {
            this.mode = mode;
            this.processed = processed;
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
    private class MenuGroupRecord {

        String groupId;
        MenuOutput outputMenu;
        SeparationMenuContributionRule.SeparationMode separationMode;
        List<MenuGroupRecord> subGroups = new LinkedList<>();
        List<MenuContribution> contributions = new LinkedList<>();

        public MenuGroupRecord(String groupId, MenuOutput outputMenu) {
            this.groupId = groupId;
            this.outputMenu = outputMenu;
        }

        public MenuGroupRecord(String groupId, MenuOutput outputMenu, SeparationMenuContributionRule.SeparationMode separationMode) {
            this(groupId, outputMenu);
            this.separationMode = separationMode;
        }
    }

    @ParametersAreNonnullByDefault
    private class SubMenuRecord {

        String subMenuId;
        MenuOutput menuOutput;
        List<MenuGroupRecord> groupRecords = new LinkedList<>();
        Map<String, MenuGroupRecord> groupsMap = new HashMap<>();
        Map<String, List<MenuContribution>> beforeItem = new HashMap<>();
        Map<String, List<MenuContribution>> afterItem = new HashMap<>();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();

        public SubMenuRecord(String subMenuId, MenuOutput menuOutput) {
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
    private class MenuGroupRecordPathNode {

        List<MenuGroupRecord> records;
        int childIndex;

        public MenuGroupRecordPathNode(List<MenuGroupRecord> records) {
            this.records = records;
        }
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
