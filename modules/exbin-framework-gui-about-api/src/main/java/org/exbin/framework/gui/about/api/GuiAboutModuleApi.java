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
package org.exbin.framework.gui.about.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface of the XBUP framework about module.
 *
 * @version 0.2.0 2016/08/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiAboutModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiAboutModuleApi.class);
    public static final String HELP_ABOUT_MENU_GROUP_ID = MODULE_ID + ".helpAboutMenuGroup";

    /**
     * Returns about application action.
     *
     * @return action
     */
    @Nonnull
    Action getAboutAction();

    void registerDefaultMenuItem();

    /**
     * Sets single side component for about dialog.
     *
     * @param sideComponent component
     */
    void setAboutDialogSideComponent(JComponent sideComponent);
}
