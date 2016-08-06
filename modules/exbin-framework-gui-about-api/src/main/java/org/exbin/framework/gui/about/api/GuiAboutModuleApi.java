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
package org.exbin.framework.gui.about.api;

import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.api.XBApplicationModule;

/**
 * Interface of the XBUP framework about module.
 *
 * @version 0.2.0 2016/08/06
 * @author ExBin Project (http://exbin.org)
 */
public interface GuiAboutModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiAboutModuleApi.class);
    public static final String HELP_ABOUT_MENU_GROUP_ID = MODULE_ID + ".helpAboutMenuGroup";

    /**
     * Returns about application action.
     *
     * @return action
     */
    Action getAboutAction();

    void registerDefaultMenuItem();

    /**
     * Sets single side component for about dialog.
     *
     * @param sideComponent component
     */
    void setAboutDialogSideComponent(JComponent sideComponent);
}
