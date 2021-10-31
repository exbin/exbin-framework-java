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
package org.exbin.framework.editor.wave.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.AudioEditor;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.wave.gui.PropertiesPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Properties action.
 *
 * @version 0.2.1 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PropertiesAction extends AbstractAction {

    public static final String ACTION_ID = "propertiesAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public PropertiesAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        AudioPanel audioPanel = (AudioPanel) activeFile.get().getComponent();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);

        PropertiesPanel propertiesPanel = new PropertiesPanel();
        propertiesPanel.setDocument((AudioEditor) editorProvider);
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(propertiesPanel, controlPanel);

        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, propertiesPanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }
}
