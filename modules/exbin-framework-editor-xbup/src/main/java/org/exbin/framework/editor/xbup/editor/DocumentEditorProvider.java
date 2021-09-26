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
package org.exbin.framework.editor.xbup.editor;

import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.swing.JPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;

/**
 * XBUP editor module.
 *
 * @version 0.2.0 2015/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class DocumentEditorProvider implements EditorProvider {

    @Override
    public JPanel getEditorComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public Optional<String> getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFileType(FileType ft) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void newFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
