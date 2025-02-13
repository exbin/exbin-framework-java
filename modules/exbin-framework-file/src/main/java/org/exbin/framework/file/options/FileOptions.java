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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.file.FileDialogsType;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * File options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileOptions implements OptionsData {

    public static final String KEY_FILE_DIALOGS = "fileDialogs";

    private final OptionsStorage storage;

    public FileOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getFileDialogs() {
        return storage.get(KEY_FILE_DIALOGS, FileDialogsType.SWING.name());
    }

    public void setFileDialogs(String fileDialogs) {
        storage.put(KEY_FILE_DIALOGS, fileDialogs);
    }

    @Override
    public void copyTo(OptionsData options) {
        ((FileOptions) options).setFileDialogs(getFileDialogs());
    }
}
