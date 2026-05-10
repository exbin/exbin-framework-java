/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.license.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;

/**
 * Interface of the license management module.
 */
@ParametersAreNonnullByDefault
public interface LicenseModuleApi extends Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(LicenseModuleApi.class);
    public static final String HELP_ABOUT_MENU_GROUP_ID = MODULE_ID + ".helpAboutMenuGroup";
    public static final String ABOUT_PAGES_ID = "about";

    /**
     * Returns main license management.
     *
     * @return license management
     */
    @Nonnull
    LicenseManagement getLicenseManagement();

    /**
     * Returns about application action.
     *
     * @return action
     */
    @Nonnull
    Action createAboutAction();

    /**
     * Registers About action in default menu.
     */
    void registerDefaultMenuItem();

    /**
     * Sets single side component for about dialog.
     *
     * @param sideComponent component
     */
    void setAboutDialogSideComponent(JComponent sideComponent);

    /**
     * Creates main about panel.
     *
     * @return about panel
     */
    @Nonnull
    JComponent createAboutPanel();

    /**
     * Registers basic about pages.
     */
    void registerBasicPages();
}
