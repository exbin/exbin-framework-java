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
package org.exbin.framework.editor.xbup.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.DocumentPropertiesPanel;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupFileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.CloseControlPanel;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.xbup.core.block.XBTBlock;

/**
 * Document properties action.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentPropertiesAction extends AbstractAction {

    public static final String ACTION_ID = "propertiesAction";

    private XbupEditorProvider editorProvider;
    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(DocumentPropertiesAction.class);

    public DocumentPropertiesAction() {
    }

    public void setup(XbupEditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        setEnabled(false);
        editorProvider.addItemSelectionListener((@Nullable XBTBlock item) -> {
            setEnabled(item != null);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XBApplication application = editorProvider.getApplication();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        DocumentPropertiesPanel propertiesPanel = new DocumentPropertiesPanel();
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            return;
        }
        XbupFileHandler xbupFile = (XbupFileHandler) editorProvider.getActiveFile().get();
        propertiesPanel.setDocument(xbupFile.getDoc());
        propertiesPanel.setDocumentUri(activeFile.get().getFileUri().orElse(null));
        CloseControlPanel controlPanel = new CloseControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(propertiesPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, propertiesPanel.getResourceBundle());
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered((Component) e.getSource());
    }
}
