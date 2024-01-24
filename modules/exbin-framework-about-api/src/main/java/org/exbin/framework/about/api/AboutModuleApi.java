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
package org.exbin.framework.about.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface of the XBUP framework about module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface AboutModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(AboutModuleApi.class);
    public static final String HELP_ABOUT_MENU_GROUP_ID = MODULE_ID + ".helpAboutMenuGroup";

    /**
     * Returns about application action.
     *
     * @return action
     */
    @Nonnull
    Action getAboutAction();

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
}
