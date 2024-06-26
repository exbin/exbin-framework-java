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
package org.exbin.framework.operation.undo.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework undo/redo module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OperationUndoModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(OperationUndoModuleApi.class);
    public static final String UNDO_MENU_GROUP_ID = MODULE_ID + ".undoMenuGroup";
    public static final String UNDO_TOOL_BAR_GROUP_ID = MODULE_ID + ".undoToolBarGroup";

    /**
     * Registers undo/redo operations to main frame menu.
     */
    void registerMainMenu();

    /**
     * Registers undo/redo operations to main frame tool bar.
     */
    void registerMainToolBar();

    /**
     * Creates new instance of the undo actions set.
     *
     * @return undo actions set
     */
    @Nonnull
    UndoActions createUndoActions();
}
