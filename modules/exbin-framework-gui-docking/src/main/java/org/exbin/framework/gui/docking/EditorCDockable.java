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

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Optional;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileHandlerApi;

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
            Optional<FileHandlerApi> activeFile = editorProvider.getActiveFile();
            if (activeFile.isEmpty()) {
                return;
            }

            FileHandlerApi fileHandler = activeFile.get();
            Optional<String> fileName = fileHandler.getFileName();
            String name = fileName.isEmpty() ? UNDEFINED_NAME : fileName.get();
            if (fileHandler.isModified()) {
                name += " *";
            }
            setTitleText(name);
        }
    }
}
