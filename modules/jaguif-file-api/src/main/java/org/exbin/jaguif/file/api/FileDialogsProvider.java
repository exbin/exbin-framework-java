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
package org.exbin.jaguif.file.api;

import java.awt.Component;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * File dialogs provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileDialogsProvider {

    /**
     * Returns provider name.
     *
     * @return provider name
     */
    @Nonnull
    String getProviderName();

    /**
     * Shows open file dialog.
     *
     * @param parentComponent parent component
     * @param fileTypes file types
     * @param selectedFile selected file
     * @param usedDirectory used directory
     * @param dialogName dialog name
     * @return open file result
     */
    @Nonnull
    OpenFileResult showOpenFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName);

    /**
     * Shows save file dialog.
     *
     * @param parentComponent parent component
     * @param fileTypes file types
     * @param selectedFile selected file
     * @param usedDirectory used directory
     * @param dialogName dialog name
     * @return open file result
     */
    @Nonnull
    OpenFileResult showSaveFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName);
}
