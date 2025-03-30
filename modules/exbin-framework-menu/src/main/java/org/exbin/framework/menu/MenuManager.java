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
                BuilderGroupRecord subGroup = section.groupsMap.get(groupId);
                if (subGroup == null) {
                    subGroup = new BuilderGroupRecord(groupId);
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

            if (section.processingState == SectionProcessingState.START) {
                if (section.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || section.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                    section.separatorQueued = true;
                }
                section.processingState = SectionProcessingState.ITEMS;
            }
            
            if (section.processingState == SectionProcessingState.ITEMS) {
                
            }

            if (section.processingGroup >= section.groups.size()) {
                if (section.processingState == SectionProcessingState.GROUPS) {
                    if (section.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW || section.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                        section.separatorQueued = true;
                    }
                    section.processingState = SectionProcessingState.END;
                }

                processing.remove(processing.size() - 1);
                continue;
            }
            
            // TODO: Groups ordering / contribution.afterItems;
            BuilderGroupRecord groups = section.groups.get(section.processingGroup);

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
                if (contribution.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || contribution.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                    section.separatorQueued = true;
                }
                boolean isPopup = outputMenu.isPopup();
                if (contribution instanceof BuilderActionRecord) {
                    BuilderActionRecord contributionRecord = (BuilderActionRecord) contribution;
                    if (contributionRecord.shouldCreate(isPopup, menuId, section.subMenuId)) {
                        JMenuItem menuItem = contributionRecord.createItem(isPopup, menuId, section.subMenuId, buttonGroups);
                        if (section.separatorQueued) {
                            section.outputMenu.addSeparator();
                            section.separatorQueued = false;
                        }
                        section.outputMenu.add(menuItem);
                        contributionRecord.finishItem(menuItem, activationUpdateService);
                    }
                } else if (contribution instanceof BuilderDirectSubMenuRecord) {
                    BuilderDirectSubMenuRecord contributionRecord = (BuilderDirectSubMenuRecord) contribution;
                    if (contributionRecord.shouldCreate(isPopup, menuId, section.subMenuId)) {
                        JMenuItem menuItem = contributionRecord.createItem(isPopup, menuId, section.subMenuId, buttonGroups);
                        if (section.separatorQueued) {
                            section.outputMenu.addSeparator();
                            section.separatorQueued = false;
                        }
                        section.outputMenu.add(menuItem);
                        contributionRecord.finishItem(menuItem, activationUpdateService);
                    }
                } else if (contribution instanceof BuilderMenuRecord) {
                    BuilderMenuRecord contributionRecord = (BuilderMenuRecord) contribution;
                    if (contributionRecord.shouldCreate(isPopup, menuId, section.subMenuId)) {
                        BuilderSectionRecord subSection = builderRecord.subMenusMap.get(contributionRecord.contributionId);
                        if (subSection != null) {
                            JMenu subMenu = contributionRecord.createItem(isPopup, menuId, section.subMenuId, buttonGroups);
                            Action action = contributionRecord.contribution.getAction();
                            subMenu.setAction(action);
                            if (section.separatorQueued) {
                                section.outputMenu.addSeparator();
                                section.separatorQueued = false;
                            }
                            section.outputMenu.add(subMenu);
                            contributionRecord.finishItem(subMenu, activationUpdateService);
                            subSection.outputMenu = new MenuWrapper(subMenu);
                            processing.add(subSection);
                        }
                    }
                }
                if (contribution.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW || contribution.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                    section.separatorQueued = true;
                }
                groupSection.items.remove(0);
            }
        }
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

        BuilderGroupRecord groupRecord = subMenuSection.groupsMap.get(groupId);
        if (groupRecord == null) {
            groupRecord = new BuilderGroupRecord(groupId);
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

    private static void finishMenuAction(@Nullable Action action, ActionContextService activationUpdateService) {
        if (action != null) {
            activationUpdateService.requestUpdate(action);
        }
    }

    private static void finishMenuItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
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

        List<BuilderGroupRecord> groups = new ArrayList<>();
        Map<String, BuilderGroupRecord> groupsMap = new HashMap<>();
        List<BuilderContributionRecord> items = new ArrayList<>();
        Map<String, BuilderContributionRecord> itemsMap = new HashMap<>();

        SectionProcessingState processingState = SectionProcessingState.START;
        boolean separatorQueued = false;
        Map<String, List<String>> afterMap = new HashMap<>();
        Set<String> processedGroups = new HashSet<>();
        Set<String> processedItems = new HashSet<>();

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
    
    private enum SectionProcessingState {
        START,
        ITEMS,
        GROUPS,
        END
    }

    @ParametersAreNonnullByDefault
    private static class BuilderGroupRecord {
        
        String groupId;

        SeparationMenuContributionRule.SeparationMode separationMode;
        final Set<String> afterItems = new HashSet<>();

        Map<PositionMenuContributionRule.PositionMode, BuilderSectionRecord> positions;
        PositionMenuContributionRule.PositionMode processingPosition = PositionMenuContributionRule.PositionMode.TOP;

        public BuilderGroupRecord(String groupId) {
            this.groupId = groupId;
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
    
        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (isPopup) {
                Action action = ((ActionMenuContribution) contribution).getAction();
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    return menuCreation.shouldCreate(menuId, subMenuId);
                }
            }

            return true;
        }

        @Nonnull
        public JMenuItem createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            Action action = contribution.getAction();
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);

            if (isPopup) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            return menuItem;
        }
        
        public void finishItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
            Action menuItemAction = menuItem.getAction();
            if (menuItemAction != null) {
                MenuManager.finishMenuAction(menuItemAction, activationUpdateService);
            }
        }
    }
    
    @ParametersAreNonnullByDefault
    private static class BuilderDirectSubMenuRecord extends BuilderContributionRecord {

        DirectSubMenuContribution contribution;
        JMenuItem menuItem;

        public BuilderDirectSubMenuRecord(DirectSubMenuContribution contribution) {
            this.contribution = contribution;
        }
    
        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (menuItem == null) {
                menuItem = contribution.getMenuItemProvider().createMenuItem();
            }

            if (isPopup) {
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

        @Nonnull
        public JMenuItem createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            if (menuItem == null) {
                menuItem = contribution.getMenuItemProvider().createMenuItem();
            }

            Action action = menuItem.getAction();
            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            return menuItem;
        }

        public void finishItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
            MenuManager.finishMenuItem(menuItem, activationUpdateService);
            MenuManager.finishMenuAction(menuItem.getAction(), activationUpdateService);
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderMenuRecord extends BuilderContributionRecord {

        SubMenuContribution contribution;
        String contributionId;

        public BuilderMenuRecord(SubMenuContribution contribution) {
            this.contribution = contribution;
            this.contributionId = contribution.getSubMenuId();
        }

        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (isPopup) {
                Action action = contribution.getAction();
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    return menuCreation.shouldCreate(menuId, subMenuId);
                }
            }

            return true; // TODO menuItem.getMenuComponentCount() > 0;
        }

        @Nonnull
        public JMenu createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            JMenu subMenu = UiUtils.createMenu();

            Action action = contribution.getAction();
            subMenu.setAction(action);
            if (isPopup) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(subMenu, menuId, subMenuId);
                }
            }
            
            return subMenu;
        }

        public void finishItem(JMenu menu, ActionContextService activationUpdateService) {
            MenuManager.finishMenuAction(menu.getAction(), activationUpdateService);
        }
    }

    private static class BuilderContributionRecord {

        SeparationMenuContributionRule.SeparationMode separationMode;
        final Set<String> afterItems = new HashSet<>();

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
