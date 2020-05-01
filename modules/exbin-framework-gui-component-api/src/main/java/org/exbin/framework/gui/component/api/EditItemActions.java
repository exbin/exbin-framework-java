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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Interface for item movement action set.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditItemActions {

    /**
     * Sets edit action handler.
     *
     * @param actionsHandler actions handler
     */
    void setEditItemActionsHandler(EditItemActionsHandler actionsHandler);

    /**
     * Returns add item action.
     *
     * @return add item action
     */
    @Nonnull
    Action getAddItemAction();

    /**
     * Returns edit item action.
     *
     * @return edit item action
     */
    @Nonnull
    Action getEditItemAction();

    /**
     * Returns delete item action.
     *
     * @return delete item action
     */
    @Nonnull
    Action getDeleteItemAction();

    /**
     * Updates state of these actions according to handler.
     */
    void updateEditItemActions();
}
