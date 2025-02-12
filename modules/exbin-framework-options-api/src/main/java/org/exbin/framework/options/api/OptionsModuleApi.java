/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.options.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework options module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(OptionsModuleApi.class);
    public static String TOOLS_OPTIONS_MENU_GROUP_ID = MODULE_ID + ".toolsOptionsMenuGroup";

    /**
     * Returns options page management.
     *
     * @param moduleId module id
     * @return
     */
    @Nonnull
    OptionsPageManagement getOptionsPageManagement(String moduleId);

    /**
     * Creates open options dialog action.
     *
     * @return options action
     */
    @Nonnull
    Action createOptionsAction();

    /**
     * Registers options menu action in default position.
     */
    void registerMenuAction();

    /**
     * Loads all settings from preferences and applies it.
     */
    void initialLoadFromPreferences();

    /**
     * Invokes options changed event.
     */
    void notifyOptionsChanged();

    /**
     * Returns options panel type.
     *
     * @return options panel type
     */
    @Nonnull
    OptionsPanelType getOptionsPanelType();

    /**
     * Sets options panel type.
     *
     * @param optionsPanelType options panel type
     */
    void setOptionsPanelType(OptionsPanelType optionsPanelType);
}
