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
package org.exbin.framework.addon.update.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsData;

/**
 * Check for update on start options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateSettings implements SettingsData {

    public static final String KEY_CHECK_FOR_UPDATE_ON_START = "start.checkForUpdate";

    private final OptionsStorage storage;

    public CheckForUpdateSettings(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isShouldCheckForUpdate() {
        return storage.getBoolean(KEY_CHECK_FOR_UPDATE_ON_START, true);
    }

    public void setShouldCheckForUpdate(boolean shouldCheck) {
        storage.putBoolean(KEY_CHECK_FOR_UPDATE_ON_START, shouldCheck);
    }

    @Override
    public void copyTo(SettingsData options) {
        ((CheckForUpdateSettings) options).setShouldCheckForUpdate(isShouldCheckForUpdate());
    }
}
