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

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for file types passing.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileTypes {

    /**
     * Returns true for all file types filter (asterisk filter).
     *
     * @return true if allowed
     */
    boolean allowAllFiles();

    /**
     * Returns file type of specific id if present.
     *
     * @param fileTypeId file type id
     * @return file type
     */
    @Nonnull
    Optional<FileType> getFileType(String fileTypeId);

    /**
     * Returns collection of all file types.
     *
     * @return collection of gile types
     */
    @Nonnull
    Collection<FileType> getFileTypes();
}
