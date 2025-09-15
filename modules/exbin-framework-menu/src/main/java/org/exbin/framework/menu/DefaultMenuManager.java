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
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.TreeContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.menu.api.MenuManager;

/**
 * Default menu manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMenuManager extends TreeContributionManager implements MenuManager {

    public DefaultMenuManager() {
    }

    @Override
    public void buildMenu(JMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new MenuWrapper(outputMenu, activationUpdateService, buttonGroups), menuId);
        activationUpdateService.requestUpdate();
    }

    @Override
    public void buildMenu(JPopupMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new PopupMenuWrapper(outputMenu, activationUpdateService, buttonGroups), menuId);
        activationUpdateService.requestUpdate();
    }

    @Override
    public void buildMenu(JMenuBar outputMenuBar, String menuId, ActionContextService activationUpdateService) {
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new MenuBarWrapper(outputMenuBar, activationUpdateService, buttonGroups), menuId);
        activationUpdateService.requestUpdate();
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            return false;
        }

        for (SequenceContribution contribution : definition.getContributions()) {
            if (contribution instanceof GroupSequenceContribution && ((GroupSequenceContribution) contribution).getGroupId().equals(groupId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void unregisterMenu(String menuId) {
        unregisterDefinition(menuId);
    }

    @Override
    public void registerMenu(String menuId, String moduleId) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition != null) {
            return;
        }

        registerDefinition(menuId, moduleId);
    }

    @Nonnull
    @Override
    public ActionMenuContribution registerMenuItem(String menuId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition(moduleId);
            definitions.put(menuId, definition);
        }

        ActionMenuContribution menuContribution = new ActionMenuContribution(action);
        definition.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, String subMenuName) {
        Action subMenuAction = new AbstractAction(subMenuName) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        return registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, Action subMenuAction) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition(moduleId);
            definitions.put(menuId, definition);
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction);
        definition.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public DirectMenuContribution registerMenuItem(String menuId, String moduleId, MenuItemProvider menuItemProvider) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition(moduleId);
            definitions.put(menuId, definition);
        }

        DirectMenuContribution menuContribution = new DirectMenuContribution(menuItemProvider);
        definition.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerMenuGroup(String menuId, String moduleId, String groupId) {
        return registerContributionGroup(menuId, moduleId, groupId);
    }

    @Override
    public void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionMenuContribution) {
                    actions.add(((ActionMenuContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @Nonnull
    private static JMenuItem createMenuItem(Action action, Map<String, ButtonGroup> buttonGroups) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);
        return menuItem;
    }

    private static void finishMenuAction(@Nullable Action action, ActionContextService activationUpdateService) {
        if (action == null) {
            return;
        }

        activationUpdateService.requestUpdate(action);
    }

    private static void finishMenuItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
        if (menuItem == null) {
            return;
        }

        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, activationUpdateService);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, activationUpdateService);
            }
        }
    }

    private static void finishMenu(JMenu menu, ActionContextService activationUpdateService) {
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

    @ParametersAreNonnullByDefault
    private static class MenuWrapper implements TreeContributionSequenceOutput {

        private final JMenu menu;
        private final ActionContextService activationUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;
        private final boolean isPopup;

        public MenuWrapper(JMenu menu, ActionContextService activationUpdateService, Map<String, ButtonGroup> buttonGroups, boolean isPopup) {
            this.menu = menu;
            this.activationUpdateService = activationUpdateService;
            this.buttonGroups = buttonGroups;
            this.isPopup = isPopup;
        }

        public MenuWrapper(JMenu menu, ActionContextService activationUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this(menu, activationUpdateService, buttonGroups, false);
        }

        @Override
        public boolean initItem(ItemSequenceContribution itemContribution, String definitionId, String subId) {
            Action action;
            JMenuItem menuItem;
            if (itemContribution instanceof ActionMenuContribution) {
                menuItem = null;
                action = ((ActionMenuContribution) itemContribution).getAction();
            } else {
                menuItem = ((DirectMenuContribution) itemContribution).getMenuItem();
                action = menuItem.getAction();
            }
            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            if (itemContribution instanceof ActionMenuContribution) {
                menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) itemContribution).setMenuItem(menuItem);
            }

            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            if (itemContribution instanceof DirectMenuContribution) {
                menu.add(((DirectMenuContribution) itemContribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) itemContribution).getMenuItem();
            menu.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, activationUpdateService);
        }

        @Override
        public boolean initItem(SubSequenceContribution itemContribution, String definitionId, String subId) {
            Action action = ((SubMenuContribution) itemContribution).getAction();
            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            JMenu subMenu = UiUtils.createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) itemContribution).setSubMenu(subMenu);
            ((SubMenuContribution) itemContribution).setSubOutput(new MenuWrapper(subMenu, activationUpdateService, buttonGroups, isPopup));

            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(subMenu, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(SubSequenceContribution itemContribution) {
            JMenu subMenu = ((SubMenuContribution) itemContribution).getSubMenu();
            menu.add(subMenu);
            DefaultMenuManager.finishMenuItem(subMenu, activationUpdateService);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Nonnull
        public JMenu getMenu() {
            return menu;
        }

        @Override
        public boolean isEmpty() {
            return menu.getItemCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuBarWrapper implements TreeContributionSequenceOutput {

        private final JMenuBar menuBar;
        private final ActionContextService activationUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;

        public MenuBarWrapper(JMenuBar menuBar, ActionContextService activationUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this.menuBar = menuBar;
            this.activationUpdateService = activationUpdateService;
            this.buttonGroups = buttonGroups;
        }

        @Override
        public boolean initItem(ItemSequenceContribution itemContribution, String definitionId, String subId) {
            if (itemContribution instanceof ActionMenuContribution) {
                Action action = ((ActionMenuContribution) itemContribution).getAction();
                JMenuItem menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) itemContribution).setMenuItem(menuItem);
            }

            return true;
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            if (itemContribution instanceof DirectMenuContribution) {
                menuBar.add(((DirectMenuContribution) itemContribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) itemContribution).getMenuItem();
            menuBar.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, activationUpdateService);
        }

        @Override
        public boolean initItem(SubSequenceContribution itemContribution, String definitionId, String subId) {
            Action action = ((SubMenuContribution) itemContribution).getAction();

            JMenu subMenu = UiUtils.createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) itemContribution).setSubMenu(subMenu);
            ((SubMenuContribution) itemContribution).setSubOutput(new MenuWrapper(subMenu, activationUpdateService, buttonGroups));

            return true;
        }

        @Override
        public void add(SubSequenceContribution itemContribution) {
            JMenu subMenu = ((SubMenuContribution) itemContribution).getSubMenu();
            menuBar.add(subMenu);
            DefaultMenuManager.finishMenuItem(subMenu, activationUpdateService);
        }

        @Override
        public void addSeparator() {
        }

        @Override
        public boolean isEmpty() {
            return menuBar.getMenuCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class PopupMenuWrapper implements TreeContributionSequenceOutput {

        private final JPopupMenu menu;
        private final ActionContextService activationUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;

        public PopupMenuWrapper(JPopupMenu menu, ActionContextService activationUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this.menu = menu;
            this.activationUpdateService = activationUpdateService;
            this.buttonGroups = buttonGroups;
        }

        @Override
        public boolean initItem(ItemSequenceContribution itemContribution, String definitionId, String subId) {
            Action action;
            JMenuItem menuItem;
            if (itemContribution instanceof ActionMenuContribution) {
                menuItem = null;
                action = ((ActionMenuContribution) itemContribution).getAction();
            } else {
                menuItem = ((DirectMenuContribution) itemContribution).getMenuItem();
                action = menuItem.getAction();
            }
            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            if (itemContribution instanceof ActionMenuContribution) {
                menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) itemContribution).setMenuItem(menuItem);
            }

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            if (itemContribution instanceof DirectMenuContribution) {
                menu.add(((DirectMenuContribution) itemContribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) itemContribution).getMenuItem();
            menu.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, activationUpdateService);
        }

        @Override
        public boolean initItem(SubSequenceContribution itemContribution, String definitionId, String subId) {
            Action action = ((SubMenuContribution) itemContribution).getAction();

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            JMenu subMenu = UiUtils.createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) itemContribution).setSubMenu(subMenu);
            ((SubMenuContribution) itemContribution).setSubOutput(new MenuWrapper(subMenu, activationUpdateService, buttonGroups, true));

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(subMenu, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(SubSequenceContribution itemContribution) {
            JMenu subMenu = ((SubMenuContribution) itemContribution).getSubMenu();
            menu.add(subMenu);
            DefaultMenuManager.finishMenuItem(subMenu, activationUpdateService);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Override
        public boolean isEmpty() {
            return menu.getComponentCount() == 0;
        }
    }
}
