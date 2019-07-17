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
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for framework component module.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiComponentModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiComponentModuleApi.class);

    @Nonnull
    JPanel getTableEditPanel();

    /**
     * Creates new instance of the edit item actions set.
     *
     * @param editItemActionsHandler move item actions handler
     * @return edit item actions set
     */
    @Nonnull
    EditItemActions createEditItemActions(EditItemActionsHandler editItemActionsHandler);

    /**
     * Creates new instance of the move item actions set.
     *
     * @param moveItemActionsHandler move item actions handler
     * @return move item actions set
     */
    @Nonnull
    MoveItemActions createMoveItemActions(MoveItemActionsHandler moveItemActionsHandler);

    /**
     * Returns new instance of dialog control panel.
     *
     * @param handler dialog control panel handler
     * @return dialog control panel
     */
    @Nonnull
    JPanel createDialogControlPanel(DialogControlPanelHandler handler);
}
