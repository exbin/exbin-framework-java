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
package org.exbin.framework.docking;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

/**
 * Editor dockable.
 *
 * @author ExBin Project (https://exbin.org)
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
