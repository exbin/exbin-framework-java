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
package org.exbin.framework.gui.update.service;

import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.gui.update.api.VersionNumbers;

/**
 * Check for update service.
 *
 * @version 0.2.0 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CheckForUpdateService {

    /**
     * Performs check for updates.
     *
     * @return check for updates result
     */
    @Nonnull
    CheckForUpdateResult checkForUpdate();

    /**
     * Returns version of the current application.
     *
     * @return version
     */
    @Nonnull
    VersionNumbers getCurrentVersion();

    /**
     * Returns version of update if available.
     *
     * @return version of update
     */
    @Nullable
    VersionNumbers getUpdateVersion();

    void performCheckForUpdates(BackgroundCheckListener listener);

    @Nullable
    URL getDownloadUrl();

    /**
     * Enumeration of result types.
     */
    public static enum CheckForUpdateResult {
        UPDATE_URL_NOT_SET,
        NO_CONNECTION,
        CONNECTION_ISSUE,
        NOT_FOUND,
        NO_UPDATE_AVAILABLE,
        UPDATE_FOUND
    }

    @ParametersAreNonnullByDefault
    public interface BackgroundCheckListener {

        void checkFinished(CheckForUpdateResult result, VersionNumbers updateVersion);
    }
}
