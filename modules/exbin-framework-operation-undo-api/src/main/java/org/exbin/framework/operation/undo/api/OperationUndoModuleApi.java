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
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.xbup.operation.undo.XBUndoHandler;

/**
 * Interface for framework undo/redo module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OperationUndoModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(OperationUndoModuleApi.class);

    /**
     * Returns undo handler.
     *
     * @return undo handler
     */
    @Nonnull
    XBUndoHandler getUndoHandler();

    /**
     * Sets current undo handler.
     *
     * @param undoHandler undo handler
     */
    void setUndoHandler(XBUndoHandler undoHandler);

    /**
     * Registers undo/redo operations to main frame menu.
     */
    void registerMainMenu();

    /**
     * Registers undo/redo operations to main frame menu.
     */
    void registerUndoManagerInMainMenu();

    /**
     * Registers undo/redo operations to main frame tool bar.
     */
    void registerMainToolBar();

    /**
     * Updates enablement of undo and redo operations.
     */
    void updateUndoStatus();

    /**
     * Opens undo manager dialog.
     */
    void openUndoManager();

    /**
     * Creates new instance of the undo actions set.
     *
     * @param undoActionsHandler clipboard handler
     * @return undo actions set
     */
    @Nonnull
    UndoActions createUndoActions(UndoActionsHandler undoActionsHandler);
}
