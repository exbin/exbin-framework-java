/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.gui.action.api.ActionMenuContribution;
import org.exbin.framework.gui.action.api.DirectMenuContribution;
import org.exbin.framework.gui.action.api.MenuContribution;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.action.api.SubMenuContribution;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Menu handler.
 *
 * @version 0.2.0 2016/07/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuHandler {

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

    public MenuHandler() {
    }

    public void buildMenu(JPopupMenu targetMenu, String menuId) {
        MenuHandler.this.buildMenu(new PopupMenuWrapper(targetMenu), menuId);
    }

    private void buildMenu(MenuTarget targetMenu, String menuId) {
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
                if (position.getBasicMode() != null) {
                    MenuGroupRecord groupRecord = groupsMap.get(position.getBasicMode().name());
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
            if (menuPosition.getBasicMode() != null) {
                MenuGroupRecord menuGroupRecord = groupsMap.get(menuPosition.getBasicMode().name());
                menuGroupRecord.contributions.add(contribution);
            } else if (menuPosition.getNextToMode() != null) {
                switch (menuPosition.getNextToMode()) {
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
            } else {
                MenuGroupRecord menuGroupRecord = groupsMap.get(menuPosition.getGroupId());
                menuGroupRecord.contributions.add(contribution);
            }
        }

        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        processMenuGroup(groupRecords, beforeItem, afterItem, targetMenu, buttonGroups);
    }

    private void processMenuGroup(List<MenuGroupRecord> groups, Map<String, List<MenuContribution>> beforeItem, Map<String, List<MenuContribution>> afterItem, final MenuTarget targetMenu, final Map<String, ButtonGroup> buttonGroups) {
        List<MenuGroupRecordPathNode> processingPath = new LinkedList<>();
        processingPath.add(new MenuGroupRecordPathNode(groups));

        boolean separatorQueued = false;
        boolean menuContinues = false;

        while (!processingPath.isEmpty()) {
            MenuGroupRecordPathNode pathNode = processingPath.get(processingPath.size() - 1);
            if (pathNode.childIndex == pathNode.records.size()) {
                processingPath.remove(processingPath.size() - 1);
                continue;
            }

            MenuGroupRecord groupRecord = pathNode.records.get(pathNode.childIndex);
            pathNode.childIndex++;

            if ((groupRecord.separationMode == SeparationMode.ABOVE || groupRecord.separationMode == SeparationMode.AROUND) && menuContinues) {
                targetMenu.addSeparator();
                separatorQueued = false;
            }

            for (MenuContribution contribution : groupRecord.contributions) {
                if (separatorQueued) {
                    targetMenu.addSeparator();
                    separatorQueued = false;
                }

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
                                menuItem = ActionUtils.actionToMenuItem(action, buttonGroups);
                            }

                            @Override
                            public String getName() {
                                return menuItem.getText();
                            }

                            @Override
                            public void finish() {
                                targetMenu.add(menuItem);
                            }
                        };
                    } else if (next.contribution instanceof SubMenuContribution) {
                        processed = new ProcessedContribution() {
                            JMenu subMenu;

                            @Override
                            public void process() {
                                SubMenuContribution subMenuContribution = (SubMenuContribution) next.contribution;
                                subMenu = new JMenu();
                                MenuHandler.this.buildMenu(subMenu.getPopupMenu(), subMenuContribution.getMenuId());
                                subMenu.setText(subMenuContribution.getName());
                            }

                            @Override
                            public String getName() {
                                return subMenu.getText();
                            }

                            @Override
                            public void finish() {
                                if (subMenu.getMenuComponentCount() > 0) {
                                    targetMenu.add(subMenu);
                                }
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
                            public void finish() {
                                targetMenu.add(directMenuContribution.getMenu());
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
                    if (next.contribution.getMenuPosition().getNextToMode() != null) {
                        switch (next.contribution.getMenuPosition().getNextToMode()) {
                            case BEFORE: {
                                next.parent.before.add(processed);
                                break;
                            }
                            case AFTER: {
                                next.parent.after.add(processed);
                                break;
                            }
                            default:
                                throw new IllegalStateException();
                        }
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
                            orderingContribution.processed.finish();
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

                menuContinues = true;
            }

            if (groupRecord.separationMode == SeparationMode.AROUND || groupRecord.separationMode == SeparationMode.BELOW) {
                separatorQueued = true;
            }

            if (!groupRecord.subGroups.isEmpty()) {
                processingPath.add(new MenuGroupRecordPathNode(groupRecord.subGroups));
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

    public void buildMenu(JMenuBar targetMenuBar, String menuId) {
        buildMenu(new MenuBarWrapper(targetMenuBar), menuId);
    }

    public void registerMenu(String menuId, String pluginId) {
        if (menuId == null) {
            throw new NullPointerException("Menu Id cannot be null");
        }
        if (pluginId == null) {
            throw new NullPointerException("Plugin Id cannot be null");
        }

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
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            throw new IllegalStateException("Menu with Id " + menuId + " doesn't exist");
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuName, position);
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
    }
}
