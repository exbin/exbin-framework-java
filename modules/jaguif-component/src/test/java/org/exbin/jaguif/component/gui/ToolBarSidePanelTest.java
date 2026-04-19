/*
 * Copyright (C) ExBin Project, https://exbin.org
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

import org.exbin.jaguif.component.ComponentModule;
import org.exbin.jaguif.component.api.action.EditItemActions;
import org.exbin.jaguif.component.api.action.EmptyContextEditItem;
import org.exbin.jaguif.component.api.action.MoveItemActions;
import org.exbin.jaguif.component.api.action.EmptyContextMoveItem;
import org.exbin.jaguif.utils.TestApplication;
import org.exbin.jaguif.utils.UtilsModule;
import org.exbin.jaguif.utils.WindowUtils;
import org.junit.Test;
import org.exbin.jaguif.component.api.ContextEditItem;
import org.exbin.jaguif.component.api.ContextMoveItem;
import org.exbin.jaguif.utils.UiUtils;
import org.junit.Ignore;

/**
 * Test for ToolBarEditorPanel.
 */
public class ToolBarSidePanelTest {

    @Test
    @Ignore
    public void testPanel() {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.jaguif.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.jaguif.language.api.TestLanguageModule());
            testApplication.addModule(org.exbin.jaguif.action.api.ActionModuleApi.MODULE_ID, new org.exbin.jaguif.action.ActionModule());
            ComponentModule guiComponentModule = new ComponentModule();
            testApplication.addModule(ComponentModule.MODULE_ID, guiComponentModule);

            ToolBarSidePanel toolBarSidePanel = new ToolBarSidePanel();
            ContextMoveItem moveItemActionsHandler = new EmptyContextMoveItem();
            MoveItemActions moveItemActions = guiComponentModule.createMoveItemActions(moveItemActionsHandler);
            toolBarSidePanel.addActions(moveItemActions);
            toolBarSidePanel.addSeparator();

            ContextEditItem editItemActionsHandler = new EmptyContextEditItem();
            EditItemActions editItemActions = guiComponentModule.createEditItemActions(editItemActionsHandler);
            toolBarSidePanel.addActions(editItemActions);
            WindowUtils.invokeWindow(toolBarSidePanel);
        });

        UiUtils.waitForUiThread();
    }
}
