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
package org.exbin.framework.editor.xbup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;

/**
 * File types with just all files filter.
 *
 * @version 0.2.2 2021/11/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBFileTypes implements FileTypes {
    
    private Map<String, FileType> fileTypes = new HashMap<>();

    public XBFileTypes() {
        XBFileFilter xbType = new XBFileFilter();
        fileTypes.put(xbType.getFileTypeId(), xbType);
    }

    @Override
    public boolean allowAllFiles() {
        return true;
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType(String fileTypeId) {
        return Optional.ofNullable(fileTypes.get(fileTypeId));
    }

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return fileTypes.values();
    }
}
