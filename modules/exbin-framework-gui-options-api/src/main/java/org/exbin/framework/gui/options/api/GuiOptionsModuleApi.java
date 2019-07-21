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
package org.exbin.framework.gui.options.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for framework options module.
 *
 * @version 0.2.1 2019/07/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiOptionsModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiOptionsModuleApi.class);
    public static String TOOLS_OPTIONS_MENU_GROUP_ID = MODULE_ID + ".toolsOptionsMenuGroup";

    @Nonnull
    Action getOptionsAction();

    /**
     * Adds options panel to given path.
     *
     * @param optionsPage options panel
     * @param path path to use for options panel tree
     */
    void addOptionsPage(OptionsPage<?> optionsPage, List<OptionsPathItem> path);

    /**
     * Adds options panel to given path with default name.
     *
     * @param optionsPage options panel
     * @param parentPath path string to use for options panel tree with strings
     * separated by /
     */
    void addOptionsPage(OptionsPage<?> optionsPage, String parentPath);

    /**
     * Adds options panel to default path and name.
     *
     * @param optionsPage options panel
     */
    void addOptionsPage(OptionsPage<?> optionsPage);

    /**
     * Extends main options panel.
     *
     * @param optionsPage options panel
     */
    void extendMainOptionsPage(OptionsPage<?> optionsPage);

    /**
     * Extends appearance options panel.
     *
     * @param optionsPage options panel
     */
    void extendAppearanceOptionsPage(OptionsPage<?> optionsPage);

    /**
     * Registers options menu action in default position.
     */
    void registerMenuAction();

    /**
     * Loads all settings from preferences and applies it.
     */
    void initialLoadFromPreferences();
}
