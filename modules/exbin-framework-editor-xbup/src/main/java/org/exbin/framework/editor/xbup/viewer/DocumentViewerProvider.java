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
package org.exbin.framework.editor.xbup.viewer;

import java.beans.PropertyChangeListener;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.editor.xbup.panel.XBDocumentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.undo.XBUndoHandler;

/**
 * Viewer provider.
 *
 * @version 0.2.1 2020/02/29
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentViewerProvider implements EditorProvider {

    private final XBDocumentPanel documentPanel;

    public DocumentViewerProvider(XBACatalog catalog, XBUndoHandler undoHandler) {
        documentPanel = new XBDocumentPanel(catalog, undoHandler);
    }

    @Nonnull
    @Override
    public JPanel getPanel() {
        return documentPanel;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
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

    @Override
    public URI getFileUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileType getFileType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFileType(FileType fileType) {
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
}
