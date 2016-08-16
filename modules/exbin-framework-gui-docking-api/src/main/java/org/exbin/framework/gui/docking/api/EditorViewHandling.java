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
package org.exbin.framework.gui.docking.api;

import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;

/**
 * Interface for editor view handling.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
public interface EditorViewHandling {

    /**
     * Adds new editor view.
     *
     * @param editorProvider editor provider
     */
    void addEditorView(EditorProvider editorProvider);

    /**
     * Removes and drops editor view.
     *
     * @param editorProvider editor provider
     */
    void removeEditorView(EditorProvider editorProvider);

    /**
     * Updates editor view. Specificaly name and modified state.
     *
     * @param editorProvider editor provider
     */
    void updateEditorView(EditorProvider editorProvider);

    /**
     * Sets multiple tabs editor handler.
     *
     * @param multiEditor multi editor provider
     */
    void setMultiEditorProvider(MultiEditorProvider multiEditor);
}
