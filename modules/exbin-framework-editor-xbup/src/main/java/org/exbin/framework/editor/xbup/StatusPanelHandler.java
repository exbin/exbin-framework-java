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
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.client.api.ClientConnectionEvent;
import org.exbin.framework.client.api.ClientConnectionListener;
import org.exbin.framework.editor.xbup.panel.XBDocStatusPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Status panel handler.
 *
 * @version 0.2.1 2019/06/28
 * @author ExBin Project (http://exbin.org)
 */
public class StatusPanelHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private XBDocStatusPanel docStatusPanel;

    public StatusPanelHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    void init() {
    }

    public XBDocStatusPanel getDocStatusPanel() {
        if (docStatusPanel == null) {
            docStatusPanel = new XBDocStatusPanel();
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            frameModule.registerStatusBar(EditorXbupModule.MODULE_ID, EditorXbupModule.DOC_STATUS_BAR_ID, docStatusPanel);
            frameModule.switchStatusBar(EditorXbupModule.DOC_STATUS_BAR_ID);
            // ((XBDocumentPanel) getEditorProvider()).registerTextStatus(docStatusPanel);
        }

        return docStatusPanel;
    }

    public ClientConnectionListener getClientConnectionListener() {
        return (ClientConnectionEvent connectionEvent) -> {
            docStatusPanel.setConnectionStatus(connectionEvent.getConnectionStatus());
        };
    }
}
