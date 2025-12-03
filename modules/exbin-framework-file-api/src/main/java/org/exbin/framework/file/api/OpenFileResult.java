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
package org.exbin.framework.file.api;

import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Open file result.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@Immutable
public class OpenFileResult {

    protected final int dialogResult;
    @Nullable
    protected final File selectedFile;
    @Nullable
    protected final FileType fileType;

    public OpenFileResult(int dialogResult, @Nullable File selectedFile, @Nullable FileType fileType) {
        this.dialogResult = dialogResult;
        this.selectedFile = selectedFile;
        this.fileType = fileType;
    }

    /**
     * Returns dialog regult.
     * <p>
     * Typically JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION
     *
     * @return dialog result
     */
    public int getDialogResult() {
        return dialogResult;
    }

    @Nonnull
    public Optional<File> getSelectedFile() {
        return Optional.ofNullable(selectedFile);
    }

    @Nonnull
    public Optional<FileType> getFileType() {
        return Optional.ofNullable(fileType);
    }
}
