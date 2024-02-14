/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.MultiEditorProvider;

/**
 * Close all files action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CloseAllFileAction extends AbstractAction {

    public static final String ACTION_ID = "fileCloseAllAction";

    private ResourceBundle resourceBundle;
    private EditorProvider editorProvider;

    public CloseAllFileAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Override
            public void register(ComponentActivationManager manager) {
                manager.registerUpdateListener(EditorProvider.class, (instance) -> {
                    editorProvider = instance;
                    setEnabled(editorProvider instanceof MultiEditorProvider);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (editorProvider instanceof MultiEditorProvider) {
            ((MultiEditorProvider) editorProvider).closeAllFiles();
        }
    }
}
