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

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;
import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Editor dockable.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
public class EditorCDockableLayout implements MultipleCDockableLayout {

    private Component content;

    public EditorCDockableLayout() {
    }

    public void setContent(Component content) {
        this.content = content;
    }

    public Component getContent() {
        return content;
    }

    @Override
    public void readStream(DataInputStream in) throws IOException {
//        content = in.readUTF();
    }

    @Override
    public void readXML(XElement element) {
//        content = element.getString();
    }

    @Override
    public void writeStream(DataOutputStream out) throws IOException {
//        out.writeUTF(content);
    }

    @Override
    public void writeXML(XElement element) {
//        element.setString(content);
    }
}
