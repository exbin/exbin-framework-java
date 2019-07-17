/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
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
