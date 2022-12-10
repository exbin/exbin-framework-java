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
package org.exbin.framework.editor.wave;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.file.api.FileType;

/**
 * File Filter for audio files.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioFileType extends FileFilter implements FileType {

    private String ext;

    public AudioFileType(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String extension = getExtension(file);
        if (extension != null) {
            return extension.toLowerCase().equals(getExt());
        }
        return false;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Audio files " + getExt().toUpperCase() + " (*." + getExt() + ")";
    }

    @Nullable
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @Nullable
    public String getExt() {
        return ext;
    }

    public void setExt(@Nullable String ext) {
        this.ext = ext;
    }

    @Nonnull
    @Override
    public String getFileTypeId() {
        return "XBWaveEditor.AudioFileFilter" + ext;
    }
}
