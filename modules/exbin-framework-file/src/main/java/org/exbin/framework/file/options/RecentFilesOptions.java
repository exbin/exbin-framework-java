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
package org.exbin.framework.file.options;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Recently opened files preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RecentFilesOptions implements OptionsData {

    public static final String KEY_RECENTFILE_PATH_PREFIX = "recentFile.path.";
    public static final String KEY_RECENTFILE_MODULE_PREFIX = "recentFile.module.";
    public static final String KEY_RECENFILE_MODE_PREFIX = "recentFile.mode.";

    private final OptionsStorage storage;

    public RecentFilesOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public Optional<String> getFilePath(int index) {
        return storage.get(KEY_RECENTFILE_PATH_PREFIX + String.valueOf(index));
    }

    @Nonnull
    public Optional<String> getModuleName(int index) {
        return storage.get(KEY_RECENTFILE_MODULE_PREFIX + String.valueOf(index));
    }

    @Nonnull
    public Optional<String> getFileMode(int index) {
        return storage.get(KEY_RECENFILE_MODE_PREFIX + String.valueOf(index));
    }

    public void setFilePath(String value, int index) {
        storage.put(KEY_RECENTFILE_PATH_PREFIX + String.valueOf(index), value);
    }

    public void setModuleName(String value, int index) {
        storage.put(KEY_RECENTFILE_MODULE_PREFIX + String.valueOf(index), value);
    }

    public void setFileMode(String value, int index) {
        storage.put(KEY_RECENFILE_MODE_PREFIX + String.valueOf(index), value);
    }

    public void remove(int index) {
        storage.remove(KEY_RECENTFILE_PATH_PREFIX + String.valueOf(index));
    }

    @Override
    public void copyTo(OptionsData options) {
        RecentFilesOptions with = (RecentFilesOptions) options;
        int index = 0;
        do {
            Optional<String> filePath = getFilePath(index);
            if (!filePath.isPresent()) {
                break;
            }
            with.setFilePath(filePath.get(), index);
            with.setFileMode(getFileMode(index).orElse(null), index);
            with.setModuleName(getModuleName(index).orElse(null), index);

            index++;
        } while (index < 15);
    }
}
