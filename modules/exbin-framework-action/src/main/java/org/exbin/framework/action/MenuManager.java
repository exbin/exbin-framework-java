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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.exbin.framework.action.api.DirectMenuContribution;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.NextToMode;
import org.exbin.framework.action.api.PositionMode;
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
     * Menu group records: menu id -> menu group.
     */
    private Map<String, List<MenuGroup>> menuGroups = new HashMap<>();

    /**
     * Menu modified flags.
     */
    private Set<String> menuModified = new HashSet<>();

    /**
     * Map of plugins usage per menu id.
     */
    private Map<String, String> pluginsUsage = new HashMap<>();

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
        List<MenuGroup> groups = menuGroups.get(menuId);
        if (groups != null) {
            for (MenuGroup group : groups) {
                String groupId = group.getGroupId();
                SeparationMode separationMode = group.getSeparationMode();
                MenuPosition position = group.getPosition();
                PositionMode basicMode = position.getBasicMode();
                if (basicMode != PositionMode.UNSPECIFIED) {
                    MenuGroupRecord groupRecord = groupsMap.get(basicMode.name());
                    MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId);
                    menuGroupRecord.separationMode = separationMode;
                    groupRecord.subGroups.add(menuGroupRecord);
                    groupsMap.put(groupId, menuGroupRecord);
                } else {
                    MenuGroupRecord groupRecord = groupsMap.get(position.getGroupId());
                    MenuGroupRecord menuGroupRecord = new MenuGroupRecord(groupId);
                    menuGroupRecord.separationMode = separationMode;
                    groupRecord.subGroups.add(menuGroupRecord);
                    groupsMap.put(groupId, menuGroupRecord);
                }
            }
        }

        // Go thru all contributions and link them to its target group
        for (MenuContribution contribution : menuDef.getContributions()) {
            MenuPosition menuPosition = contribution.getMenuPosition();
            PositionMode basicMode = menuPosition.getBasicMode();
            NextToMode nextToMode = menuPosition.getNextToMode();
            if (basicMode != PositionMode.UNSPECIFIED) {
                MenuGroupRecord menuGroupRecord = groupsMap.get(basicMode.name());
                menuGroupRecord.contributions.add(contribution);
            } else {
                switch (nextToMode) {
                    case UNSPECIFIED: {
                        MenuGroupRecord menuGroupRecord = groupsMap.get(menuPosition.getGroupId());
                        menuGroupRecord.contributions.add(contribution);
                        break;
                    }
                    case BEFORE: {
                        List<MenuContribution> contributions = beforeItem.get(menuPosition.getGroupId());
                        if (contributions == null) {
                            contributions = new LinkedList<>();
                            beforeItem.put(menuPosition.getGroupId(), contributions);
                        }
                        contributions.add(contribution);
                        break;
                    }
                    case AFTER: {
                        List<MenuContribution> contributions = afterItem.get(menuPosition.getGroupId());
                        if (contributions == null) {
                            contributions = new LinkedList<>();
                            afterItem.put(menuPosition.getGroupId(), contributions);
                        }
                        contributions.add(contribution);
                        break;
                    }
                    default:
                        throw new IllegalStateException();
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

                            @Override
                            public void process() {
                                Action action = ((ActionMenuContribution) next.contribution).getAction();
                                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                                menuItem = actionModule.actionToMenuItem(action, buttonGroups);
                            }

                            @Override
                            public String getName() {
                                return menuItem.getText();
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

                            @Override
                            public void process() {
                                SubMenuContribution subMenuContribution = (SubMenuContribution) next.contribution;
                                subMenu = UiUtils.createMenu();
                                subMenu.setAction(subMenuContribution.getAction());
                                buildMenu(new MenuWrapper(subMenu, targetMenu.isPopup()), subMenuContribution.getMenuId(), activationUpdateService);
                            }

                            @Override
                            public String getName() {
                                return subMenu.getText();
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
                    } else if (next.contribution instanceof DirectMenuContribution) {
                        processed = new ProcessedContribution() {
                            DirectMenuContribution directMenuContribution;

                            @Override
                            public void process() {
                                directMenuContribution = (DirectMenuContribution) next.contribution;
                            }

                            @Override
                            public String getName() {
                                return directMenuContribution.getMenu().getName();
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
                    } else {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    processed.process();
                    if (next.parent == null) {
                        rootProcessed = processed;
                    }
                    String name = processed.getName();
                    NextToMode nextToMode = next.contribution.getMenuPosition().getNextToMode();
                    switch (nextToMode) {
                        case BEFORE: {
                            next.parent.before.add(processed);
                            break;
                        }
                        case AFTER: {
                            next.parent.after.add(processed);
                            break;
                        }
                        case UNSPECIFIED: {
                            break;
                        }
                        default:
                            throw new IllegalStateException();
                    }
                    List<MenuContribution> nextToBefore = beforeItem.get(name);
                    if (nextToBefore != null) {
                        nextToBefore.forEach((menuContribution) -> {
                            queue.add(new QueuedContribution(processed, menuContribution));
                        });
                    }

                    List<MenuContribution> nextToAfter = afterItem.get(name);
                    if (nextToAfter != null) {
                        nextToAfter.forEach((menuContribution) -> {
                            queue.add(new QueuedContribution(processed, menuContribution));
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
        List<MenuGroup> menuGroupDefs = menuGroups.get(menuId);
        if (menuGroupDefs == null) {
            return false;
        }

        if (menuGroupDefs.stream().anyMatch((menuGroup) -> (groupId.equals(menuGroup.getGroupId())))) {
            return true;
        }

        return false;
    }

    public void unregisterMenu(String menuId) {
        MenuDefinition definition = menus.get(menuId);
        if (definition != null) {
            // TODO clear pointers to improve garbage collection performance?
//            List<MenuContribution> contributions = definition.getContributions();
//            for (MenuContribution contribution : contributions) {
//                contribution.
//            }

            for (Map.Entry<String, List<MenuGroup>> usage : menuGroups.entrySet()) {
                if (menuId.equals(usage.getKey())) {
                    menuGroups.remove(usage.getKey());
                    break;
                }
            }

            for (Map.Entry<String, String> usage : pluginsUsage.entrySet()) {
                if (menuId.equals(usage.getValue())) {
                    pluginsUsage.remove(usage.getKey());
                    break;
                }
            }
            menus.remove(menuId);
        }
    }

    private static abstract class ProcessedContribution {

        List<ProcessedContribution> before = new LinkedList<>();
        List<ProcessedContribution> after = new LinkedList<>();

        abstract void process();

        abstract String getName();

        abstract boolean shouldCreate();

        abstract void finish();
    }

    private static class QueuedContribution {

        ProcessedContribution parent;
        MenuContribution contribution;

        public QueuedContribution(ProcessedContribution parent, MenuContribution contribution) {
            this.parent = parent;
            this.contribution = contribution;
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
            throw new IllegalStateException("Menu with ID " + menuId + " already exists.");
        }

        MenuDefinition menuDefinition = new MenuDefinition(pluginId);
        menus.put(menuId, menuDefinition);
    }

    public void registerMenuGroup(String menuId, MenuGroup menuGroup) {
        List<MenuGroup> groups = menuGroups.get(menuId);
        if (groups == null) {
            groups = new LinkedList<>();
            menuGroups.put(menuId, groups);
        }
        groups.add(menuGroup);
    }

    public void registerMenuItem(String menuId, String pluginId, Action action, MenuPosition position) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            throw new IllegalStateException("Menu with Id " + menuId + " doesn't exist");
        }

        ActionMenuContribution menuContribution = new ActionMenuContribution(action, position);
        menuDef.getContributions().add(menuContribution);
    }

    public void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position) {
        Action subMenuAction = new AbstractAction(subMenuName) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        registerMenuItem(menuId, pluginId, subMenuId, subMenuAction, position);
    }

    public void registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction, MenuPosition position) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            throw new IllegalStateException("Menu with Id " + menuId + " doesn't exist");
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction, position);
        menuDef.getContributions().add(menuContribution);
    }

    public void registerMenuItem(String menuId, String pluginId, JMenu menu, MenuPosition position) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            throw new IllegalStateException("Menu with Id " + menuId + " doesn't exist");
        }

        DirectMenuContribution menuContribution = new DirectMenuContribution(menu, position);
        menuDef.getContributions().add(menuContribution);
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
