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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Record of sub/child menu contribution.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SubMenuContribution implements MenuContribution {

    private final String menuId;
    private final String name;
    private final MenuPosition position;

    public SubMenuContribution(String menuId, String name, MenuPosition position) {
        this.menuId = menuId;
        this.name = name;
        this.position = position;
    }

    @Nonnull
    public String getMenuId() {
        return menuId;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public MenuPosition getMenuPosition() {
        return position;
    }
}
