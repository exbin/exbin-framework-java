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
package org.exbin.framework.editor.xbup;

import java.util.ResourceBundle;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.DocumentPropertiesAction;
import org.exbin.framework.editor.xbup.action.ItemPropertiesAction;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Properties handler.
 *
 * @version 0.2.1 2020/03/02
 * @author ExBin Project (http://exbin.org)
 */
public class PropertiesHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private DocumentPropertiesAction propertiesAction;
    private ItemPropertiesAction itemPropertiesAction;
    private boolean devMode;

    public PropertiesHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        propertiesAction = new DocumentPropertiesAction((DocumentViewerProvider) editorProvider);

        itemPropertiesAction = new ItemPropertiesAction((DocumentViewerProvider) editorProvider);
        itemPropertiesAction.setDevMode(devMode);
    }

    public Action getPropertiesAction() {
        return propertiesAction;
    }

    public Action getItemPropertiesAction() {
        return itemPropertiesAction;
    }

    void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    void setEditEnabled(boolean editEnabled) {
        itemPropertiesAction.setEnabled(editEnabled);
    }
}
