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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * File types from collection of types.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultFileTypes implements FileTypes {

    private final Collection<FileType> fileTypes;

    public DefaultFileTypes(FileType fileType) {
        this.fileTypes = new ArrayList<>();
        this.fileTypes.add(fileType);
    }

    public DefaultFileTypes(Collection<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    @Override
    public boolean allowAllFiles() {
        return fileTypes.isEmpty();
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType(String fileTypeId) {
        return fileTypes.stream().filter((t) -> fileTypeId.equals(t.getFileTypeId())).findFirst();
    }

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return fileTypes;
    }
}
