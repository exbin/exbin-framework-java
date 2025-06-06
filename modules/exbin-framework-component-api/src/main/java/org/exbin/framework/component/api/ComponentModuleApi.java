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
package org.exbin.framework.component.api;

import org.exbin.framework.component.api.toolbar.MoveItemActions;
import org.exbin.framework.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.component.api.toolbar.MoveItemActionsHandler;
import org.exbin.framework.component.api.toolbar.EditItemActions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework component module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ComponentModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ComponentModuleApi.class);

    /**
     * Creates new instance of the edit item actions set.
     *
     * @param editItemActionsHandler move item actions handler
     * @return edit item actions set
     */
    @Nonnull
    EditItemActions createEditItemActions(EditItemActionsHandler editItemActionsHandler);

    /**
     * Creates new instance of the move item actions set.
     *
     * @param moveItemActionsHandler move item actions handler
     * @return move item actions set
     */
    @Nonnull
    MoveItemActions createMoveItemActions(MoveItemActionsHandler moveItemActionsHandler);

    /**
     * Returns new instance of dialog control panel.
     *
     * @param handler dialog control panel handler
     * @return dialog control panel
     */
    @Nonnull
    JPanel createDialogControlPanel(DialogControlPanelHandler handler);
}
