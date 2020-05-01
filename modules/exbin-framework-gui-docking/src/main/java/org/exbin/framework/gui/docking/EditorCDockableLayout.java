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
