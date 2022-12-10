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
package org.exbin.framework.update.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.update.UpdateModule;
import org.exbin.framework.update.api.VersionNumbers;
import org.exbin.framework.update.service.CheckForUpdateService;
import org.exbin.framework.update.service.CheckForUpdateService.BackgroundCheckListener;
import org.exbin.framework.update.service.CheckForUpdateService.CheckForUpdateResult;

/**
 * Check for update service implementation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateServiceImpl implements CheckForUpdateService {

    private final UpdateModule guiUpdateModule;

    private Thread checkingThread = null;

    public CheckForUpdateServiceImpl(UpdateModule guiUpdateModule) {
        this.guiUpdateModule = guiUpdateModule;
    }

    @Override
    public VersionNumbers getCurrentVersion() {
        return guiUpdateModule.getCurrentVersion();
    }

    @Nullable
    @Override
    public VersionNumbers getUpdateVersion() {
        return guiUpdateModule.getUpdateVersion();
    }

    @Nullable
    @Override
    public URL getDownloadUrl() {
        return guiUpdateModule.getUpdateDownloadUrl();
    }

    @Override
    public void performCheckForUpdates(BackgroundCheckListener listener) {
        if (checkingThread != null) {
            checkingThread.interrupt();
        }
        checkingThread = new Thread(() -> {
            CheckForUpdateResult result = checkForUpdate();
            VersionNumbers updateVersion = guiUpdateModule.getUpdateVersion();
            listener.checkFinished(result, updateVersion);
        });
        checkingThread.start();
    }

    @Nonnull
    @Override
    public CheckForUpdateResult checkForUpdate() {
        URL checkUpdateUrl = guiUpdateModule.getUpdateUrl();
        if (checkUpdateUrl == null) {
            return CheckForUpdateResult.UPDATE_URL_NOT_SET;
        }

        try {
            VersionNumbers updateVersion;
            try (InputStream checkUpdateStream = checkUpdateUrl.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(checkUpdateStream))) {
                String line = reader.readLine();
                if (line == null) {
                    return CheckForUpdateResult.NOT_FOUND;
                }
                updateVersion = new VersionNumbers();
                updateVersion.versionFromString(line);
                guiUpdateModule.setUpdateVersion(updateVersion);
            }

            // Compare versions
            if (updateVersion.isGreaterThan(guiUpdateModule.getCurrentVersion())) {
                return CheckForUpdateResult.UPDATE_FOUND;
            }

            return CheckForUpdateResult.NO_UPDATE_AVAILABLE;
        } catch (FileNotFoundException ex) {
            return CheckForUpdateResult.NOT_FOUND;
        } catch (IOException ex) {
            return CheckForUpdateResult.CONNECTION_ISSUE;
        } catch (Exception ex) {
            return CheckForUpdateResult.CONNECTION_ISSUE;
        }
    }
}
