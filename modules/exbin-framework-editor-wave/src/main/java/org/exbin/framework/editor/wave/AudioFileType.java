/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.wave;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.gui.file.api.FileType;

/**
 * File Filter for audio files.
 *
 * @version 0.2.0 2016/01/23
 * @author ExBin Project (http://exbin.org)
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
