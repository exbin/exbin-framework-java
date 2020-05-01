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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Property panel control handler.
 *
 * @version 0.2.0 2016/02/11
 * @author ExBin Project (http://exbin.org)
 */
public class PropertyPanelHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action viewPropertyPanelAction;

    public PropertyPanelHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        viewPropertyPanelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
//                if (editorProvider instanceof DocumentViewerProvider) {
//                    ((DocumentViewerProvider) editorProvider).setShowPropertiesPanel(((JMenuItem) e.getSource()).isSelected());
//                }
            }
        };
        ActionUtils.setupAction(viewPropertyPanelAction, resourceBundle, "viewPropertyPanelAction");
        viewPropertyPanelAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        viewPropertyPanelAction.putValue(Action.SELECTED_KEY, true);
    }

    public Action getViewPropertyPanelAction() {
        return viewPropertyPanelAction;
    }
}
