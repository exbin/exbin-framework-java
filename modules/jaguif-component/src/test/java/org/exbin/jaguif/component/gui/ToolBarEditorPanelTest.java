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
package org.exbin.jaguif.component.gui;

import org.exbin.jaguif.action.ActionModule;
import org.exbin.jaguif.action.api.clipboard.EmptyTextClipboardSupport;
import org.exbin.jaguif.action.api.clipboard.TextClipboardController;
import org.exbin.jaguif.operation.undo.OperationUndoModule;
import org.exbin.jaguif.operation.undo.api.EmptyUndoRedo;
import org.exbin.jaguif.operation.undo.api.UndoRedoState;
import org.exbin.jaguif.utils.TestApplication;
import org.exbin.jaguif.utils.UiUtils;
import org.exbin.jaguif.utils.UtilsModule;
import org.exbin.jaguif.utils.WindowUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for ToolBarEditorPanel.
 */
public class ToolBarEditorPanelTest {

    @Test
    @Ignore
    public void testPanel() {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.jaguif.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.jaguif.language.api.utils.TestLanguageModule());
            OperationUndoModule operationUndoModule = new OperationUndoModule();
            testApplication.addModule(OperationUndoModule.MODULE_ID, operationUndoModule);
            ActionModule actionModule = new ActionModule();
            testApplication.addModule(ActionModule.MODULE_ID, actionModule);

            ToolBarEditorPanel toolBarEditorPanel = new ToolBarEditorPanel();
            UndoRedoState undoRedoHandler = new EmptyUndoRedo();
            toolBarEditorPanel.setUndoHandler(undoRedoHandler, operationUndoModule.createUndoActions());
            TextClipboardController clipboardActionsHandler = new EmptyTextClipboardSupport();
            toolBarEditorPanel.setClipboardHandler(clipboardActionsHandler, actionModule.getClipboardActions());
            WindowUtils.invokeWindow(toolBarEditorPanel);
        });

        UiUtils.waitForUiThread();
    }
}
