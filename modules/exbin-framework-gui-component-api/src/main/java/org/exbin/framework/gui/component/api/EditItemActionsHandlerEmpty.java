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

/**
 * Empty implementation for clipboard handler for visual component / context
 * menu.
 *
 * @version 0.2.1 2017/02/21
 * @author ExBin Project (http://exbin.org)
 */
public class EditItemActionsHandlerEmpty implements EditItemActionsHandler {

    @Override
    public void performAddItem() {
    }

    @Override
    public void performEditItem() {
    }

    @Override
    public void performDeleteItem() {
    }

    @Override
    public boolean isSelection() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setUpdateListener(EditItemActionsUpdateListener updateListener) {
    }
}
