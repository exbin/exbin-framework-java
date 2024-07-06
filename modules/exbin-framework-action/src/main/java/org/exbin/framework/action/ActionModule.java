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

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.action.popup.api.ActionPopupModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.ClipboardUtils;

/**
 * Implementation of action module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionModule implements ActionModuleApi {

    private ClipboardActions clipboardActions = null;
    private ClipboardTextActions clipboardTextActions = null;
    private MenuManager menuManager = null;
    private ToolBarManager toolBarManager = null;
    private ResourceBundle resourceBundle;

    public ActionModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ActionModule.class);
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

    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.addComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.removeComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void fillPopupMenu(JPopupMenu popupMenu, int position) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.fillDefaultEditPopupMenu(popupMenu, position);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, String actionId) {
        initAction(action, bundle, action.getClass(), actionId);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ActionConsts.ACTION_NAME_POSTFIX));
        action.putValue(ActionConsts.ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX)) {
            action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX))));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX)) {
            action.putValue(Action.LARGE_ICON_KEY, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX))));
        }
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action) {
        return actionToMenuItem(action, null);
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        JMenuItem menuItem;
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    menuItem = new JCheckBoxMenuItem(action);
                    break;
                }
                case RADIO: {
                    menuItem = new JRadioButtonMenuItem(action);
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
                    menuItem = new JMenuItem(action);
                }
            }
        } else {
            menuItem = new JMenuItem(action);
        }

        Object dialogMode = action.getValue(ActionConsts.ACTION_DIALOG_MODE);
        if (dialogMode instanceof Boolean && ((Boolean) dialogMode)) {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            menuItem.setText(languageModule.getActionWithDialogText(menuItem.getText()));
        }

        return menuItem;
    }

    @Nonnull
    private MenuManager getMenuManager() {
        if (menuManager == null) {
            menuManager = new MenuManager();
        }

        return menuManager;
    }

    @Nonnull
    private ToolBarManager getToolBarManager() {
        if (toolBarManager == null) {
            toolBarManager = new ToolBarManager();
        }

        return toolBarManager;
    }

    @Override
    public void buildMenu(JPopupMenu targetMenu, String menuId, ComponentActivationService activationUpdateService) {
        getMenuManager().buildMenu(targetMenu, menuId, activationUpdateService);
    }

    @Override
    public void buildMenu(JMenuBar targetMenuBar, String menuId, ComponentActivationService activationUpdateService) {
        getMenuManager().buildMenu(targetMenuBar, menuId, activationUpdateService);
    }

    @Override
    public void registerMenu(String menuId, String pluginId) {
        getMenuManager().registerMenu(menuId, pluginId);
    }

    @Override
    public void registerMenuGroup(String menuId, MenuGroup menuGroup) {
        getMenuManager().registerMenuGroup(menuId, menuGroup);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, JMenu menu, MenuPosition position) {
        getMenuManager().registerMenuItem(menuId, pluginId, menu, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, JMenuItem item, MenuPosition position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, Action action, MenuPosition position) {
        getMenuManager().registerMenuItem(menuId, pluginId, action, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction, MenuPosition position) {
        getMenuManager().registerMenuItem(menuId, pluginId, subMenuId, subMenuAction, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position) {
        getMenuManager().registerMenuItem(menuId, pluginId, subMenuId, subMenuName, position);
    }

    @Override
    public void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode) {
        registerClipboardMenuItems(getClipboardActions(), menuId, moduleId, separationMode);
    }

    @Override
    public void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMode separationMode) {
        registerMenuGroup(menuId, new MenuGroup(CLIPBOARD_ACTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP), separationMode));
        registerMenuItem(menuId, moduleId, actions.createCutAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.createCopyAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.createPasteAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.createDeleteAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.createSelectAllAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    @Override
    public void registerClipboardTextActions() {
        getClipboardTextActions();
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.registerDefaultClipboardPopupMenu(resourceBundle, ActionModule.class);
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ComponentActivationService activationUpdateService) {
        getToolBarManager().buildToolBar(targetToolBar, toolBarId, activationUpdateService);
    }

    @Override
    public void registerToolBar(String toolBarId, String pluginId) {
        getToolBarManager().registerToolBar(toolBarId, pluginId);
    }

    @Override
    public void registerToolBarGroup(String toolBarId, ToolBarGroup toolBarGroup) {
        getToolBarManager().registerToolBarGroup(toolBarId, toolBarGroup);
    }

    @Override
    public void registerToolBarItem(String toolBarId, String pluginId, Action action, ToolBarPosition position) {
        getToolBarManager().registerToolBarItem(toolBarId, pluginId, action, position);
    }

    @Override
    public void registerMenuClipboardActions() {
        registerClipboardMenuItems(ActionConsts.EDIT_MENU_ID, MODULE_ID, SeparationMode.NONE);
    }

    @Override
    public void registerToolBarClipboardActions() {
        getClipboardActions();
        registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, new ToolBarGroup(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.createCutAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.createCopyAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.createPasteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.createDeleteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
//        getClipboardActions().setClipboardActionsHandler(clipboardHandler);
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        return menuManager.menuGroupExists(menuId, groupId);
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
    @Override
    public void unregisterMenu(String menuId) {
        getMenuManager().unregisterMenu(menuId);
    }

    public void registerClipboardFlavorListener(ComponentActivationManager activationManager) {
        ClipboardUtils.getClipboard().addFlavorListener(new FlavorListener() {

            private final ClipboardFlavorState clipboardFlavorState = new ClipboardFlavorState();

            @Override
            public void flavorsChanged(FlavorEvent fe) {
                activationManager.updateActionsForComponent(ClipboardFlavorState.class, clipboardFlavorState);
            }
        });
    }
}
