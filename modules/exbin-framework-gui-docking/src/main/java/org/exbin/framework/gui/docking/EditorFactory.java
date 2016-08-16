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
package org.exbin.framework.gui.docking;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

/**
 * Editor dockable.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
public class EditorFactory implements MultipleCDockableFactory<EditorCDockable, EditorCDockableLayout> {

    @Override
    public EditorCDockableLayout write(EditorCDockable dockable) {
        EditorCDockableLayout layout = (EditorCDockableLayout) create();
        layout.setContent(dockable.getContent());
        return layout;
    }

    @Override
    public EditorCDockable read(EditorCDockableLayout layout) {
        EditorCDockable dockable = new EditorCDockable(this);
        dockable.setContent(layout.getContent());
        return dockable;
    }

    @Override
    public boolean match(EditorCDockable dockable, EditorCDockableLayout layout) {
        return false;
    }

    @Override
    public EditorCDockableLayout create() {
        return new EditorCDockableLayout();
    }
}
