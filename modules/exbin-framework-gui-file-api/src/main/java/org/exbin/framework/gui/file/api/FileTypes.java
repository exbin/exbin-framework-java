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

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for file types passing.
 *
 * @version 0.2.2 2021/10/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileTypes {

    boolean allowAllFiles();

    @Nonnull
    Optional<FileType> getFileType(String fileTypeId);

    @Nonnull
    List<FileType> getFileTypes();
}
