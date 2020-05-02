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
package org.exbin.framework.bined.handler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.PropertiesPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;

/**
 * Properties handler.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
public class PropertiesHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action propertiesAction;

    public PropertiesHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        propertiesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                PropertiesPanel propertiesPanel = new PropertiesPanel();
                propertiesPanel.setEditorProvider(editorProvider);
                CloseControlPanel controlPanel = new CloseControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(propertiesPanel, controlPanel);

                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, propertiesPanel.getResourceBundle());
                controlPanel.setHandler(() -> {
                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered((Component) e.getSource());
            }
        };
        ActionUtils.setupAction(propertiesAction, resourceBundle, "propertiesAction");
        propertiesAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getPropertiesAction() {
        return propertiesAction;
    }
}
