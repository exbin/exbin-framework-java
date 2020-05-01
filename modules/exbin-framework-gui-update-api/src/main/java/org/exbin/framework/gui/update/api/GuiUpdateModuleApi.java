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
package org.exbin.framework.gui.update.api;

import java.awt.Frame;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface of the framework update checking module.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiUpdateModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiUpdateModuleApi.class);

    /**
     * Returns check for available updates action.
     *
     * @return action
     */
    @Nonnull
    Action getCheckUpdateAction();

    void registerDefaultMenuItem();

    void registerOptionsPanels();

    /**
     * Returns URL of update data source.
     *
     * @return update URL
     */
    @Nullable
    URL getUpdateUrl();

    /**
     * Sets URL of update data source.
     *
     * @param updateUrl update URL
     */
    void setUpdateUrl(URL updateUrl);

    @Nullable
    public VersionNumbers getUpdateVersion();

    void setUpdateVersion(VersionNumbers updateVersion);

    @Nullable
    URL getUpdateDownloadUrl();

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
