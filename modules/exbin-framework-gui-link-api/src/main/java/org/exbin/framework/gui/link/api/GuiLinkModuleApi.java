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
package org.exbin.framework.gui.link.api;

import java.net.URL;
import javax.swing.Action;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.api.XBApplicationModule;

/**
 * Interface for XBUP framework link support module.
 *
 * @version 0.2.0 2016/07/14
 * @author ExBin Project (http://exbin.org)
 */
public interface GuiLinkModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiLinkModuleApi.class);

    /**
     * Registers online help action to main frame menu.
     */
    void registerOnlineHelpMenu();

    /**
     * Returns online help action.
     *
     * @return online help action
     */
    Action getOnlineHelpAction();

    /**
     * Sets online help URL.
     *
     * @param onlineHelpUrl url
     */
    void setOnlineHelpUrl(URL onlineHelpUrl);
}