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
package org.exbin.framework.gui.menu.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for clipboard handler for visual component / context menu.
 *
 * @version 0.2.0 2016/01/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ClipboardActionsHandler {

    /**
     * Performs cut to clipboard operation.
     */
    void performCut();

    /**
     * Performs copy to clipboard operation.
     */
    void performCopy();

    /**
     * Performs paste from clipboard operation.
     */
    void performPaste();

    /**
     * Performs delete selection operation.
     */
    void performDelete();

    /**
     * Performs select all operation. (should include focus request)
     */
    void performSelectAll();

    /**
     * Returns if selection for clipboard operation is available.
     *
     * @return true if selection is available
     */
    boolean isSelection();

    /**
     * Returns whether it is possible to change components data using clipboard
     * operations.
     *
     * @return true if component is editable
     */
    boolean isEditable();

    /**
     * Returns whether it is possible to execute select all operation.
     *
     * @return true if can perform select all
     */
    boolean canSelectAll();

    /**
     * Returns whether it is possible to paste current content of the clipboard.
     *
     * @return true if can perform paste
     */
    boolean canPaste();

    /**
     * Sets listener for clipboard actions related updates.
     *
     * @param updateListener update listener
     */
    void setUpdateListener(ClipboardActionsUpdateListener updateListener);
}
