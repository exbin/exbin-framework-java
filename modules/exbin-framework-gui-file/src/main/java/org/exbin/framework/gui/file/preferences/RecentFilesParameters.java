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
package org.exbin.framework.gui.file.preferences;

import javax.annotation.Nullable;
import org.exbin.framework.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Wave editor color preferences.
 *
 * @version 0.2.0 2019/06/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RecentFilesParameters {

    public static final String PREFERENCES_RECENTFILE_PATH_PREFIX = "recentFile.path.";
    public static final String PREFEFRENCES_RECENTFILE_MODULE_PREFIX = "recentFile.module.";
    public static final String PREFERENCES_RECENFILE_MODE_PREFIX = "recentFile.mode.";

    private final Preferences preferences;

    public RecentFilesParameters(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nullable
    public String getFilePath(int index) {
        return preferences.get(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(index));
    }

    @Nullable
    public String getModuleName(int index) {
        return preferences.get(PREFEFRENCES_RECENTFILE_MODULE_PREFIX + String.valueOf(index));
    }

    @Nullable
    public String getFileMode(int index) {
        return preferences.get(PREFERENCES_RECENFILE_MODE_PREFIX + String.valueOf(index));
    }

    public void setFilePath(String value, int index) {
        preferences.put(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(index), value);
    }

    public void setModuleName(String value, int index) {
        preferences.put(PREFEFRENCES_RECENTFILE_MODULE_PREFIX + String.valueOf(index), value);
    }

    public void setFileMode(String value, int index) {
        preferences.put(PREFERENCES_RECENFILE_MODE_PREFIX + String.valueOf(index), value);
    }

    public void remove(int index) {
        preferences.remove(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(index));
    }
}
