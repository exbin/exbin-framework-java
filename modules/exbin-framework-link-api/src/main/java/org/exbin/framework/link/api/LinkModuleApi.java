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
package org.exbin.framework.link.api;

import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for framework link support module.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface LinkModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(LinkModuleApi.class);

    /**
     * Registers online help action to main frame menu.
     */
    void registerOnlineHelpMenu();

    /**
     * Returns online help action.
     *
     * @return online help action
     */
    @Nonnull
    Action getOnlineHelpAction();

    /**
     * Sets online help URL.
     *
     * @param onlineHelpUrl url
     */
    void setOnlineHelpUrl(URL onlineHelpUrl);
}
