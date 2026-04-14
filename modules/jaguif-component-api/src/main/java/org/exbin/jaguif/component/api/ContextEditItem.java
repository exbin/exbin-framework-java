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
package org.exbin.jaguif.component.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.StateUpdateType;

/**
 * Interface for edit item context.
 */
@ParametersAreNonnullByDefault
public interface ContextEditItem {

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
     * Returns true if it is allowed to add new item.
     *
     * @return true if item can be added
     */
    boolean canAddItem();

    /**
     * Returns true if it is allowed to edit currently selected item.
     *
     * @return true if item can be edited
     */
    boolean canEditItem();

    /**
     * Returns true if it is allowed to delete currently selected item.
     *
     * @return true if item can be deleted
     */
    boolean canDeleteItem();

    public enum UpdateType implements StateUpdateType {
        EDIT_STATE
    }
}
