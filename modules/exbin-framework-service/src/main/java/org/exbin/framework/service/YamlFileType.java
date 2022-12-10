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
package org.exbin.framework.service;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.file.api.FileType;

/**
 * YAML file type.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class YamlFileType extends FileFilter implements FileType {

    public static final String YAML_FILE_TYPE = "YamlFileType";

    public YamlFileType() {
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = getExtension(file);
        if (extension != null) {
            return "yaml".equals(extension);
        }
        return false;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "YAML File (*.yaml)";
    }

    @Nonnull
    @Override
    public String getFileTypeId() {
        return YAML_FILE_TYPE;
    }

    /**
     * Gets the extension part of file name.
     *
     * @param file Source file
     * @return extension part of file name
     */
    @Nullable
    public static String getExtension(File file) {
        String ext = null;
        String str = file.getName();
        int extPos = str.lastIndexOf('.');

        if (extPos > 0 && extPos < str.length() - 1) {
            ext = str.substring(extPos + 1).toLowerCase();
        }
        return ext;
    }
}
