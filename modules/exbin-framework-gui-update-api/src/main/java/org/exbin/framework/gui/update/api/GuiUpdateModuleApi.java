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
package org.exbin.framework.gui.update.api;

import java.awt.Frame;
import java.net.URL;
import javax.swing.Action;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.api.XBApplicationModule;

/**
 * Interface of the XBUP framework update checking module.
 *
 * @version 0.2.0 2016/07/16
 * @author ExBin Project (http://exbin.org)
 */
public interface GuiUpdateModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiUpdateModuleApi.class);

    /**
     * Returns update check action.
     *
     * @return action
     */
    Action getCheckUpdateAction();

    /**
     * Registers default menu item.
     */
    void registerDefaultMenuItem();

    void registerOptionsPanels();

    /**
     * Sets URL of update data source.
     *
     * @param updateUrl update URL
     */
    void setUpdateUrl(URL updateUrl);

    /**
     * Sets URL of download website for updated application.
     *
     * @param downloadUrl download URL
     */
    void setUpdateDownloadUrl(URL downloadUrl);

    /**
     * Performs check for update on application start.
     *
     * @param frame frame
     */
    void checkOnStart(Frame frame);
}
