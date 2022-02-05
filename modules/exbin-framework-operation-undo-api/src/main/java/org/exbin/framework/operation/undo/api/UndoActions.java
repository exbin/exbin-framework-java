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
package org.exbin.framework.operation.undo.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Interface for undo action set.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface UndoActions {

    /**
     * Updates state of these actions according to undo handler.
     */
    void updateUndoActions();

    /**
     * Sets undo handler.
     *
     * @param undoHandler undo handler
     */
    void setUndoActionsHandler(UndoActionsHandler undoHandler);

    /**
     * Returns undo action.
     *
     * @return undo action
     */
    @Nonnull
    Action getUndoAction();

    /**
     * Returns redo action.
     *
     * @return redo action
     */
    @Nonnull
    Action getRedoAction();

    /**
     * Returns undo manager action.
     *
     * @return undo manager action
     */
    @Nonnull
    Action getUndoManagerAction();
}
