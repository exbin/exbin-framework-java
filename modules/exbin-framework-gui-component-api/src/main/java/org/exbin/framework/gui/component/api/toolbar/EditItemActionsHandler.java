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
package org.exbin.framework.gui.component.api.toolbar;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for clipboard handler for visual component / context menu.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditItemActionsHandler {

    /**
     * Adds new item.
     */
    void performAddItem();

    /**
     * Edits currently selected item.
     */
    void performEditItem();

    /**
     * Deletes currently selected item(s).
     */
    void performDeleteItem();

    /**
     * Returns if selection for clipboard operation is available.
     *
     * @return true if selection is available
     */
    boolean isSelection();

    /**
     * Returns true if it is possible to edit currently selected item.
     *
     * @return true if item is editable
     */
    boolean isEditable();

    /**
     * Set listener for actions related updates.
     *
     * @param updateListener update listener
     */
    void setUpdateListener(EditItemActionsUpdateListener updateListener);
}
