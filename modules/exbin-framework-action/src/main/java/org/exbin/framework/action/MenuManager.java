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

import java.awt.event.ActionEvent;
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
import org.exbin.framework.action.api.ActionMenuContribution;
import org.exbin.framework.action.api.ActionMenuCreation;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.api.DirectSubMenuContribution;
import org.exbin.framework.action.api.GroupMenuContribution;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuContributionRule;
import org.exbin.framework.action.api.NextToMode;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.RelativeMenuContributionRule;
import org.exbin.framework.action.api.SeparationMenuContributionRule;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.SubMenuContribution;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.utils.UiUtils;

/**
 * Menu manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuManager {

    /**
     * Menu records: menu id -> menu definition.
     */
    private Map<String, MenuDefinition> menus = new HashMap<>();

    /**
     * Menu modified flags.
     */
    // private Set<String> menuModified = new HashSet<>();
    /**
     * Map of plugins usage per menu id.
     */
    // private Map<String, String> pluginsUsage = new HashMap<>();
    public MenuManager() {
    }

    public void buildMenu(JMenu targetMenu, String menuId, ComponentActivationService activationUpdateService) {
        buildMenu(new MenuWrapper(targetMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JPopupMenu targetMenu, String menuId, ComponentActivationService activationUpdateService) {
        buildMenu(new PopupMenuWrapper(targetMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JMenuBar targetMenuBar, String menuId, ComponentActivationService activationUpdateService) {
        buildMenu(new MenuBarWrapper(targetMenuBar), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    private void buildMenu(MenuTarget targetMenu, String menuId, ComponentActivationService activationUpdateService) {
        MenuDefinition menuDef = menus.get(menuId);

        if (menuDef == null) {
            return;
        }

        Map<String, List<MenuContribution>> beforeItem = new HashMap<>();
        Map<String, List<MenuContribution>> afterItem = new HashMap<>();

        List<MenuGroupRecord> groupRecords = new LinkedList<>();

        // Create list of build-in groups
        Map<String, MenuGroupRecord> groupsMap = new HashMap<>();
        for (PositionMode mode : PositionMode.values()) {
            MenuGroupRecord menuGroupRecord = new MenuGroupRecord(mode.name());
            groupsMap.put(mode.name(), menuGroupRecord);
            groupRecords.add(menuGroupRecord);
        }

        // Build full tree of groups
        for (MenuContribution contribution : menuDef.getContributions()) {
            if (!(contribution instanceof GroupMenuContribution)) {
                continue;
            }
            String groupId = ((GroupMenuContribution) contribution).getGroupId();
            SeparationMode separationMode = getSeparationMode(menuId, contribution);
            String parentGroupId = getParentGroup(menuId, contribution);
            if (parentGroupId != null) {
                MenuGroupRecord groupRecord = groupsMap.get(parentGroupId);
                MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            } else {
                PositionMode positionMode = getPositionMode(menuId, contribution);
                if (positionMode == null) {
                    positionMode = PositionMode.DEFAULT;
                }
                MenuGroupRecord groupRecord = groupsMap.get(positionMode.name());
                MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId);
                menuGroupRecord.separationMode = separationMode;
                groupRecord.subGroups.add(menuGroupRecord);
                groupsMap.put(groupId, menuGroupRecord);
            }
        }

        // Go thru all contributions and link them to its target group
        for (MenuContribution contribution : menuDef.getContributions()) {
            if ((contribution instanceof GroupMenuContribution)) {
                continue;
            }
            PositionMode positionMode = getPositionMode(menuId, contribution);
            String parentGroupId = getParentGroup(menuId, contribution);
            if (positionMode != null) {
                MenuGroupRecord menuGroupRecord = groupsMap.get(positionMode.name());
                menuGroupRecord.contributions.add(contribution);
            } else {
                if (parentGroupId != null) {
                    MenuGroupRecord menuGroupRecord = groupsMap.get(parentGroupId);
                    menuGroupRecord.contributions.add(contribution);
                } else {
                    // TODO Rework for multiple rules and other stuff
                    RelativeMenuContributionRule relativeContributionRule = getRelativeToRule(menuId, contribution);
                    if (relativeContributionRule != null) {
                        switch (relativeContributionRule.getNextToMode()) {
                            case BEFORE: {
                                List<MenuContribution> contributions = beforeItem.get(relativeContributionRule.getContributionActionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    beforeItem.put(relativeContributionRule.getContributionActionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            case AFTER: {
                                List<MenuContribution> contributions = afterItem.get(relativeContributionRule.getContributionActionId());
                                if (contributions == null) {
                                    contributions = new LinkedList<>();
                                    afterItem.put(relativeContributionRule.getContributionActionId(), contributions);
                                }
                                contributions.add(contribution);
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        }
                    } else {
                        MenuGroupRecord menuGroupRecord = groupsMap.get(PositionMode.DEFAULT.name());
                        menuGroupRecord.contributions.add(contribution);
                    }
                }
            }
        }

        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        processMenuGroup(groupRecords, beforeItem, afterItem, targetMenu, buttonGroups, menuId, activationUpdateService);
    }

    private void processMenuGroup(List<MenuGroupRecord> groups, Map<String, List<MenuContribution>> beforeItem, Map<String, List<MenuContribution>> afterItem, final MenuTarget targetMenu, final Map<String, ButtonGroup> buttonGroups, String menuId, ComponentActivationService activationUpdateService) {
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
            pathNode.childIndex++;

            if (itemsAdded && (groupRecord.separationMode == SeparationMode.ABOVE || groupRecord.separationMode == SeparationMode.AROUND)) {
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
                    ProcessedContribution processed;
                    if (next.contribution instanceof ActionMenuContribution) {
                        processed = new ProcessedContribution() {
                            JMenuItem menuItem;
                            String actionId;

                            @Override
                            public void process() {
                                Action action = ((ActionMenuContribution) next.contribution).getAction();
                                actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                                menuItem = actionModule.actionToMenuItem(action, buttonGroups);
                            }

                            @Nonnull
                            @Override
                            public String getName() {
                                return menuItem.getText();
                            }

                            @Nonnull
                            @Override
                            String getActionId() {
                                return actionId == null ? "" : actionId;
                            }

                            @Override
                            public boolean shouldCreate() {
                                if (targetMenu.isPopup()) {
                                    Action action = ((ActionMenuContribution) next.contribution).getAction();
                                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                    if (menuCreation != null) {
                                        return menuCreation.shouldCreate(menuId);
                                    }
                                }

                                return true;
                            }

                            @Override
                            public void finish() {
                                Action action = ((ActionMenuContribution) next.contribution).getAction();
                                if (targetMenu.isPopup()) {
                                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                    if (menuCreation != null) {
                                        menuCreation.onCreate(menuItem, menuId);
                                    }
                                }

                                targetMenu.add(menuItem);
                                finishMenuAction(action, activationUpdateService);
                            }
                        };
                    } else if (next.contribution instanceof SubMenuContribution) {
                        processed = new ProcessedContribution() {
                            JMenu subMenu;
                            String actionId;

                            @Override
                            public void process() {
                                SubMenuContribution subMenuContribution = (SubMenuContribution) next.contribution;
                                subMenu = UiUtils.createMenu();
                                Action action = subMenuContribution.getAction();
                                actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                                subMenu.setAction(action);
                                buildMenu(new MenuWrapper(subMenu, targetMenu.isPopup()), subMenuContribution.getMenuId(), activationUpdateService);
                            }

                            @Nonnull
                            @Override
                            public String getName() {
                                return subMenu.getText();
                            }

                            @Nonnull
                            @Override
                            String getActionId() {
                                return actionId == null ? "" : actionId;
                            }

                            @Override
                            public boolean shouldCreate() {
                                if (targetMenu.isPopup()) {
                                    Action action = subMenu.getAction();
                                    if (action != null) {
                                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                        if (menuCreation != null) {
                                            return menuCreation.shouldCreate(menuId);
                                        }
                                    }
                                }

                                return subMenu.getMenuComponentCount() > 0;
                            }

                            @Override
                            public void finish() {
                                Action action = subMenu.getAction();
                                if (targetMenu.isPopup() && action != null) {
                                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                    if (menuCreation != null) {
                                        menuCreation.onCreate(subMenu, menuId);
                                    }
                                }

                                targetMenu.add(subMenu);
                                finishMenuAction(action, activationUpdateService);
                            }
                        };
                    } else if (next.contribution instanceof DirectSubMenuContribution) {
                        processed = new ProcessedContribution() {
                            DirectSubMenuContribution directMenuContribution;
                            String actionId;

                            @Override
                            public void process() {
                                directMenuContribution = (DirectSubMenuContribution) next.contribution;
                                Action action = directMenuContribution.getMenu().getAction();
                                if (action != null) {
                                    actionId = (String) action.getValue(ActionConsts.ACTION_ID);
                                }
                            }

                            @Nonnull
                            @Override
                            public String getName() {
                                return directMenuContribution.getMenu().getName();
                            }

                            @Nonnull
                            @Override
                            String getActionId() {
                                return actionId == null ? "" : actionId;
                            }

                            @Override
                            public boolean shouldCreate() {
                                if (targetMenu.isPopup()) {
                                    Action action = directMenuContribution.getMenu().getAction();
                                    if (action != null) {
                                        ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                        if (menuCreation != null) {
                                            return menuCreation.shouldCreate(menuId);
                                        }
                                    }
                                }

                                return true;
                            }

                            @Override
                            public void finish() {
                                JMenu menuItem = directMenuContribution.getMenu();
                                Action action = directMenuContribution.getMenu().getAction();
                                if (targetMenu.isPopup() && action != null) {
                                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                                    if (menuCreation != null) {
                                        menuCreation.onCreate(menuItem, menuId);
                                    }
                                }

                                targetMenu.add(menuItem);
                                finishMenu(menuItem, activationUpdateService);
                                finishMenuAction(action, activationUpdateService);
                            }
                        };
                    } else if (next.contribution instanceof DirectSubMenuContribution) {
                        continue;
                    } else {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    processed.process();
                    if (next.parent == null) {
                        rootProcessed = processed;
                    }
                    String actionId = processed.getActionId();
                    NextToMode nextToMode = next.nextToMode;
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

                    List<MenuContribution> nextToBefore = beforeItem.get(actionId);
                    if (nextToBefore != null) {
                        nextToBefore.forEach((menuContribution) -> {
                            queue.add(new QueuedContribution(processed, menuContribution, NextToMode.BEFORE));
                        });
                    }

                    List<MenuContribution> nextToAfter = afterItem.get(actionId);
                    if (nextToAfter != null) {
                        nextToAfter.forEach((menuContribution) -> {
                            queue.add(new QueuedContribution(processed, menuContribution, NextToMode.AFTER));
                        });
                    }
                }

                // Perform insertion of all processed menu contributions
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
                                    targetMenu.addSeparator();
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

            if (itemsAdded && groupRecord.separationMode == SeparationMode.AROUND || groupRecord.separationMode == SeparationMode.BELOW) {
                itemsAdded = false;
                separatorQueued = true;
            }

            if (!groupRecord.subGroups.isEmpty()) {
                processingPath.add(new MenuGroupRecordPathNode(groupRecord.subGroups));
            }
        }
    }

    private void finishMenuAction(@Nullable Action action, ComponentActivationService activationUpdateService) {
        if (action != null) {
            activationUpdateService.requestUpdate(action);
        }
    }

    private void finishMenu(JMenu menu, ComponentActivationService activationUpdateService) {
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
    private SeparationMode getSeparationMode(String menuId, MenuContribution contribution) {
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
    private PositionMode getPositionMode(String menuId, MenuContribution contribution) {
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

        @Nonnull
        abstract String getName();

        abstract boolean shouldCreate();

        abstract void finish();
    }

    private static class QueuedContribution {

        ProcessedContribution parent;
        MenuContribution contribution;
        NextToMode nextToMode = null;

        public QueuedContribution(ProcessedContribution parent, MenuContribution contribution) {
            this.parent = parent;
            this.contribution = contribution;
        }

        public QueuedContribution(ProcessedContribution parent, MenuContribution contribution, NextToMode nextToMode) {
            this.parent = parent;
            this.contribution = contribution;
            this.nextToMode = nextToMode;
        }
    }

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

    public void registerMenu(String menuId, String pluginId) {
        ObjectUtils.requireNonNull(menuId);
        ObjectUtils.requireNonNull(pluginId);

        MenuDefinition menu = menus.get(menuId);
        if (menu != null) {
            if (menu.getPluginId().isPresent()) {
                throw new IllegalStateException("Menu with ID " + menuId + " already exists.");
            } else {
                menu.setPluginId(pluginId);
            }
        }

        MenuDefinition menuDefinition = new MenuDefinition(pluginId);
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
    public MenuContribution registerMenuItem(String menuId, String pluginId, JMenu menu) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        DirectSubMenuContribution menuContribution = new DirectSubMenuContribution(menu);
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
        SeparationMode separationMode;
        List<MenuGroupRecord> subGroups = new LinkedList<>();
        List<MenuContribution> contributions = new LinkedList<>();

        public MenuGroupRecord(String groupId) {
            this.groupId = groupId;
        }

        public MenuGroupRecord(String groupId, SeparationMode separationMode) {
            this(groupId);
            this.separationMode = separationMode;
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
    private static interface MenuTarget {

        void add(JMenu menuItem);

        void add(JMenuItem menuItem);

        void addSeparator();

        boolean isPopup();
    }

    @ParametersAreNonnullByDefault
    private static class MenuBarWrapper implements MenuTarget {

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
    private static class PopupMenuWrapper implements MenuTarget {

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
    private static class MenuWrapper implements MenuTarget {

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
    }
}
