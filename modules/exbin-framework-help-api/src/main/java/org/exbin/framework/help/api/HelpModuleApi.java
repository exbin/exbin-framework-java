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
package org.exbin.framework.help.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for XBUP framework help module.
 *
 * @version 0.2.0 2016/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface HelpModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(HelpModuleApi.class);

    /**
     * Registers help action to main frame menu.
     */
    void registerMainMenu();

    /**
     * Returns help action.
     *
     * @return help action
     */
    @Nonnull
    Action getHelpAction();
}
