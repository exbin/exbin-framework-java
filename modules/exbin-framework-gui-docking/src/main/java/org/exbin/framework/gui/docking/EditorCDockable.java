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

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import java.awt.Component;
import java.awt.GridLayout;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Editor dockable.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
public class EditorCDockable extends DefaultMultipleCDockable {

    public static final String UNDEFINED_NAME = "Untitled";
    private final EditorFactory factory;
    private Component content;

    public EditorCDockable(EditorFactory factory) {
        super(factory);
        this.factory = factory;

        setLayout(new GridLayout(1, 1));
        setTitleText(UNDEFINED_NAME);
        setTitleShown(false);
        setSingleTabShown(true);
        setStickySwitchable(false);
        setMinimizable(false);
        setExternalizable(false);
        setMaximizable(false);
        setCloseable(false);
    }

    public Component getContent() {
        return content;
    }

    public void setContent(Component content) {
        this.content = content;
        add(content);
        update();
    }

    public void update() {
        if (content instanceof EditorProvider) {
            EditorProvider editorProvider = ((EditorProvider) content);
            String name = editorProvider.getFileName();
            if (name == null) {
                name = UNDEFINED_NAME;
            }
            if (editorProvider.isModified()) {
                name += " *";
            }
            setTitleText(name);
        }
    }
}
