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
package org.exbin.framework.gui.file.api;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for file handling actions.
 *
 * @version 0.2.2 2021/10/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileActionsApi {

    void openFile(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    void saveFile(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    void saveAsFile(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @param fileHandler file handler
     * @param fileTypes file types handler
     * @param usedDirectory used directory
     * @return true if successful
     */
    boolean showAskForSaveDialog(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @return true if allowed
     */
    boolean showAskToOverwrite();

    @Nonnull
    OpenFileResult showOpenFileDialog(FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    @Nonnull
    OpenFileResult showOpenFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory);

    @Nonnull
    OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory);

    @Nonnull
    OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory);

    public class OpenFileResult {

        public int dialogResult;
        @Nullable
        public File selectedFile;
        @Nullable
        public FileType fileType;
    }
}
