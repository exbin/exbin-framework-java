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
package org.exbin.framework.editor.picture;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Image File Filter.
 *
 * @version 0.2.0 2016/01/27
 * @author ExBin Project (http://exbin.org)
 */
public class PictureFileType extends FileFilter implements FileType {

    private String ext;

    public PictureFileType(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            return extension.toLowerCase().equals(getExt());
        }
        return false;
    }

    //The description of this filter
    @Override
    public String getDescription() {
        return "Images " + getExt().toUpperCase() + " (*."+getExt()+")";
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public String getFileTypeId() {
        return "XBPictureEditor.PictureFileType" + ext;
    }
}
