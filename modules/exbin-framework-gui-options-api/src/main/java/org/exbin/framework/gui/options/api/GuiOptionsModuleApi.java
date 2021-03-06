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
