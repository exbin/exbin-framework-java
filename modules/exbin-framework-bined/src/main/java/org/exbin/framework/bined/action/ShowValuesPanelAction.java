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
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.file.api.FileHandlerApi;

/**
 * Show values panel action.
 *
 * @version 0.2.0 2021/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowValuesPanelAction extends AbstractAction {

    public static final String ACTION_ID = "showValuesPanelAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public ShowValuesPanelAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandlerApi> activeFile = editorProvider.getActiveFile();
        if (activeFile.isEmpty()) {
            throw new IllegalStateException();
        }

        BinEdComponentPanel component = ((BinEdFileHandler) activeFile.get()).getComponent();
        setShowValuesPanel(!component.isShowValuesPanel());
    }

    public void setShowValuesPanel(boolean show) {
        Optional<FileHandlerApi> activeFile = editorProvider.getActiveFile();
        if (activeFile.isEmpty()) {
            throw new IllegalStateException();
        }

        BinEdComponentPanel component = ((BinEdFileHandler) activeFile.get()).getComponent();
        component.setShowValuesPanel(show);
        putValue(Action.SELECTED_KEY, show);
    }
}
