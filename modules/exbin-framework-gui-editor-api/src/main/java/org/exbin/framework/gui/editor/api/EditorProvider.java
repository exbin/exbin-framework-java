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
package org.exbin.framework.gui.editor.api;

import java.beans.PropertyChangeListener;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.gui.file.api.FileHandlerApi;

/**
 * XBUP framework editor interface.
 *
 * @version 0.2.2 2021/09/28
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditorProvider {

    /**
     * Returns main editor component.
     *
     * @return component
     */
    @Nonnull
    JComponent getEditorComponent();

    /**
     * Returns active file.
     *
     * @return acftive file
     */
    FileHandlerApi getActiveFile();

    /**
     * Changes passing listener.
     *
     * @param propertyChangeListener change listener
     */
    void setPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    /**
     * Sets modification listener.
     *
     * @param editorModificationListener editor modification listener
     */
    void setModificationListener(EditorModificationListener editorModificationListener);

    /**
     * Gets window title related to last opened or saved file.
     *
     * @param parentTitle title of window/frame
     * @return title related to last opened file
     */
    @Nonnull
    String getWindowTitle(String parentTitle);

    /**
     * Creates new empty file.
     */
    void newFile();

    /**
     * Interface for editor modifications listener.
     */
    public static interface EditorModificationListener {

        void modified();
    }
}
