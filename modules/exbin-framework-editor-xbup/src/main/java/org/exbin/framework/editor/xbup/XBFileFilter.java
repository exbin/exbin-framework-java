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

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.filechooser.FileFilter;
import static org.exbin.framework.editor.xbup.EditorXbupModule.XB_FILE_TYPE;
import org.exbin.framework.file.api.FileType;

/**
 * File types with just all files filter.
 *
 * @version 0.2.2 2021/10/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBFileFilter extends FileFilter implements FileType {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = getExtension(file);
        if (extension != null) {
            if (extension.length() >= 2) {
                return extension.substring(0, 2).equals("xb");
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "All XB Files (*.xb*)";
    }

    @Nonnull
    @Override
    public String getFileTypeId() {
        return XB_FILE_TYPE;
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
        int i = str.lastIndexOf('.');

        if (i > 0 && i < str.length() - 1) {
            ext = str.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
