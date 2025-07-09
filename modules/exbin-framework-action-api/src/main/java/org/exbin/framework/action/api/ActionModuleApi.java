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

import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.action.api.clipboard.TextClipboardSupported;

/**
 * Interface for action support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ActionModuleApi.class);

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
     * Registers clipboard handler for main clipboard actions.
     *
     * @param clipboardHandler clipboard handler
     */
    void registerClipboardHandler(TextClipboardSupported clipboardHandler);
}
