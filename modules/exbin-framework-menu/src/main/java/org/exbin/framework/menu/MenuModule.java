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

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuContributionRule;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.menu.api.MenuModuleApi;

/**
 * Implementation of action module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuModule implements MenuModuleApi {

    private ClipboardActions clipboardActions = null;
    private ClipboardTextActions clipboardTextActions = null;
    private MenuManager menuManager = null;
    private ResourceBundle resourceBundle;

    public MenuModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(MenuModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public ClipboardActions getClipboardActions() {
        if (clipboardActions == null) {
            clipboardActions = new ClipboardActions();
            ensureSetup();
            clipboardActions.setup(resourceBundle);
        }

        return clipboardActions;
    }

    @Nonnull
    @Override
    public ClipboardTextActions getClipboardTextActions() {
        if (clipboardTextActions == null) {
            clipboardTextActions = new ClipboardTextActions();
            ensureSetup();
            clipboardTextActions.setup(resourceBundle);
        }

        return clipboardTextActions;
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action) {
        return actionToMenuItem(action, null);
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        return actionToMenuItemInt(action, buttonGroups);
    }

    @Nonnull
    private JMenuItem actionToMenuItemInt(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        JMenuItem menuItem;
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    menuItem = UiUtils.createCheckBoxMenuItem();
                    menuItem.setAction(action);
                    break;
                }
                case RADIO: {
                    menuItem = UiUtils.createRadioButtonMenuItem();
                    menuItem.setAction(action);
                    String radioGroup = (String) action.getValue(ActionConsts.ACTION_RADIO_GROUP);
                    if (buttonGroups != null) {
                        ButtonGroup buttonGroup = buttonGroups.get(radioGroup);
                        if (buttonGroup == null) {
                            buttonGroup = new ButtonGroup();
                            buttonGroups.put(radioGroup, buttonGroup);
                        }
                        buttonGroup.add(menuItem);
                    }
                    break;
                }
                default: {
                    menuItem = UiUtils.createMenuItem();
                    menuItem.setAction(action);
                }
            }
        } else {
            menuItem = UiUtils.createMenuItem();
            menuItem.setAction(action);
        }

        Object dialogMode = action.getValue(ActionConsts.ACTION_DIALOG_MODE);
        if (dialogMode instanceof Boolean && ((Boolean) dialogMode)) {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            menuItem.setText(languageModule.getActionWithDialogText(menuItem.getText()));
        }
        return menuItem;
    }

    @Nonnull
    @Override
    public List<Action> getMenuManagedActions() {
        List<Action> actions = new ArrayList<>();
        getMenuManager();
        actions.addAll(menuManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private MenuManager getMenuManager() {
        if (menuManager == null) {
            menuManager = new MenuManager();
        }

        return menuManager;
    }

    @Nonnull
    @Override
    public MenuManagement getMenuManagement(String menuId, String moduleId) {
        return new MenuManagement() {
            @Override
            public void registerMenu(String menuId) {
                getMenuManager().registerMenu(menuId, moduleId);
            }

            @Override
            public void unregisterMenu(String menuId) {
                getMenuManager().unregisterMenu(menuId);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, MenuItemProvider menuItemProvider) {
                return getMenuManager().registerMenuItem(menuId, moduleId, menuItemProvider);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, Action action) {
                return getMenuManager().registerMenuItem(menuId, moduleId, action);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, String subMenuId, Action subMenuAction) {
                return getMenuManager().registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, String subMenuId, String subMenuName) {
                return getMenuManager().registerMenuItem(menuId, moduleId, subMenuId, subMenuName);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuGroup(String menuId, String groupId) {
                return getMenuManager().registerMenuGroup(menuId, moduleId, groupId);
            }

            @Override
            public boolean menuGroupExists(String menuId, String groupId) {
                return menuManager.menuGroupExists(menuId, groupId);
            }

            @Override
            public void registerMenuRule(MenuContribution menuContribution, MenuContributionRule rule) {
                getMenuManager().registerMenuRule(menuContribution, rule);
            }
        };
    }

    @Nonnull
    @Override
    public MenuManagement getMainMenuManagement(String moduleId) {
        return getMenuManagement(ActionConsts.MAIN_MENU_ID, moduleId);
    }

    @Override
    public void buildMenu(JPopupMenu targetMenu, String menuId, ActionContextService activationUpdateService) {
        getMenuManager().buildMenu(targetMenu, menuId, activationUpdateService);
    }

    @Override
    public void buildMenu(JMenuBar targetMenuBar, String menuId, ActionContextService activationUpdateService) {
        getMenuManager().buildMenu(targetMenuBar, menuId, activationUpdateService);
    }

    @Override
    public void registerClipboardMenuItems(String menuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode) {
        registerClipboardMenuItems(getClipboardActions(), menuId, moduleId, separationMode);
    }

    @Override
    public void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode) {
        MenuManagement mgmt = getMainMenuManagement(moduleId);
        MenuContribution contribution = mgmt.registerMenuGroup(menuId, CLIPBOARD_ACTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(separationMode));
        contribution = mgmt.registerMenuItem(menuId, actions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    @Override
    public void registerMenuClipboardActions() {
        registerClipboardMenuItems(ActionConsts.EDIT_MENU_ID, MODULE_ID, SeparationMenuContributionRule.SeparationMode.NONE);
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
//        getClipboardActions().setClipboardActionsHandler(clipboardHandler);
    }

    /*
    @Nonnull
    @Override
    public ClipboardActionsApi createClipboardActions(ClipboardActionsHandler clipboardActionsHandler) {
        ClipboardActions customClipboardActions = new ClipboardActions();
        customClipboardActions.setup(resourceBundle);
        customClipboardActions.setClipboardActionsHandler(clipboardActionsHandler);
        return customClipboardActions;
    }
     */
    public void registerClipboardFlavorListener(ActionContextChangeManager activationManager) {
        ClipboardUtils.getClipboard().addFlavorListener(new FlavorListener() {

            private final ClipboardFlavorState clipboardFlavorState = new ClipboardFlavorState();

            @Override
            public void flavorsChanged(@Nonnull FlavorEvent fe) {
                activationManager.updateActionsForComponent(ClipboardFlavorState.class, clipboardFlavorState);
            }
        });
    }
}
