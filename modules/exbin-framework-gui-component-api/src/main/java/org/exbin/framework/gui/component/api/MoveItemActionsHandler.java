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
package org.exbin.framework.gui.component.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for clipboard handler for visual component / context menu.
 *
 * @version 0.2.1 2017/02/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MoveItemActionsHandler {

    /**
     * Moves selected items one level up.
     */
    void performMoveUp();

    /**
     * Moves selected items one level down.
     */
    void performMoveDown();

    /**
     * Moves selected items top.
     */
    void performMoveTop();

    /**
     * Moves selected items bottom.
     */
    void performMoveBottom();

    /**
     * Returns if selection for clipboard operation is available.
     *
     * @return true if selection is available
     */
    boolean isSelection();

    /**
     * Returns true if it is possible to move currently selected item.
     *
     * @return true if component is editable
     */
    boolean isEditable();

    /**
     * Set listener for actions related updates.
     *
     * @param updateListener update listener
     */
    void setUpdateListener(MoveItemActionsUpdateListener updateListener);
}
