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
package org.exbin.framework.action.api;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for action support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ActionModuleApi.class);
    public static final String CLIPBOARD_ACTIONS_MENU_GROUP_ID = MODULE_ID + ".clipboardActionsMenuGroup";
    public static final String CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID = MODULE_ID + ".clipboardActionsToolBarGroup";

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param actionId action identifier and bundle key prefix
     */
    void initAction(Action action, ResourceBundle bundle, String actionId);

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param resourceClass resourceClass
     * @param actionId action identifier and bundle key prefix
     */
    void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId);

    /**
     * Creates instance of action manager.
     *
     * @return action manager
     */
    @Nonnull
    ActionManager createActionManager();

    /**
     * Converts action to menu item.
     *
     * @param action action
     * @return menu item
     */
    @Nonnull
    JMenuItem actionToMenuItem(Action action);

    /**
     * Converts action to menu item.
     *
     * @param action action
     * @param buttonGroups button groups
     * @return menu item
     */
    @Nonnull
    JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups);

    /**
     * Returns menu management interface.
     *
     * @param moduleId module id
     * @return menu management interface
     */
    @Nonnull
    MenuManagement getMenuManagement(String moduleId);

    /**
     * Returns tool bar management interface.
     *
     * @param moduleId module id
     * @return tool bar management interface
     */
    @Nonnull
    ToolBarManagement getToolBarManagement(String moduleId);

    /**
     * Returns clipboard/editing actions.
     *
     * @return clipboard editing actions
     */
    @Nonnull
    ClipboardActionsApi getClipboardActions();

    /**
     * Returns clipboard/editing text actions.
     *
     * @return clipboard/editing text actions.
     */
    @Nonnull
    ClipboardActionsApi getClipboardTextActions();

    /**
     * Registers menu clipboard actions.
     */
    void registerMenuClipboardActions();

    /**
     * Fills given popup menu with default clipboard actions.
     *
     * @param popupMenu popup menu
     * @param position target index position or -1 for adding at the end
     */
    void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position);

    /**
     * Registers tool bar clipboard actions.
     */
    void registerToolBarClipboardActions();

    /**
     * Registers default clipboard menu items.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode);

    /**
     * Registers clipboard menu items.
     *
     * @param actions clipboard actions
     * @param menuId menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMode separationMode);

    /**
     * Registers clipboard handler for main clipboard actions.
     *
     * @param clipboardHandler clipboard handler
     */
    void registerClipboardHandler(ClipboardActionsHandler clipboardHandler);

    /**
     * Registers default clipboard actions in default popup menu.
     */
    void registerClipboardTextActions();

    /**
     * Adds component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    /**
     * Removes component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    /**
     * Returns list of action managed by menu and toolbar managers.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getAllManagedActions();
}
