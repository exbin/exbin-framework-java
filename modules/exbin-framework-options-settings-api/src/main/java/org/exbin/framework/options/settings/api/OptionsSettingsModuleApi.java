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
package org.exbin.framework.options.settings.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework options settings module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsSettingsModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(OptionsSettingsModuleApi.class);
    public static String TOOLS_OPTIONS_MENU_GROUP_ID = MODULE_ID + ".toolsOptionsMenuGroup";

    /**
     * Returns options settings management.
     *
     * @return options settings manager
     */
    @Nonnull
    OptionsSettingsManagement getMainSettingsManager();

    /**
     * Returns options settings management.
     *
     * @param moduleId module id
     * @return
     */
    @Nonnull
    OptionsSettingsManagement getSettingsManagement(String moduleId);

    /**
     * Creates open options dialog action.
     *
     * @return options action
     */
    @Nonnull
    Action createSettingsAction();

    /**
     * Registers options menu action in default position.
     */
    void registerMenuAction();

    /**
     * Loads all settings from preferences and applies it.
     */
    void initialLoadFromPreferences();

    /**
     * Returns options settings panel type.
     *
     * @return options settings panel type
     */
    @Nonnull
    SettingsPanelType getSettingsPanelType();

    /**
     * Sets options settings panel type.
     *
     * @param settingsPanelType options settings panel type
     */
    void setSettingsPanelType(SettingsPanelType settingsPanelType);

    /**
     * Sets root options caption.
     *
     * @return caption
     */
    @Nonnull
    Optional<String> getOptionsRootCaption();

    /**
     * Sets root options caption.
     *
     * @param optionsRootCaption caption
     */
    void setOptionsRootCaption(@Nullable String optionsRootCaption);

    /**
     * Creates settings options overrides.
     *
     * @param settingsOptionsProvider settings options provider
     * @return settings options overrides
     */
    @Nonnull
    SettingsOptionsOverrides createSettingsOptionsOverrides(SettingsOptionsProvider settingsOptionsProvider);
}
