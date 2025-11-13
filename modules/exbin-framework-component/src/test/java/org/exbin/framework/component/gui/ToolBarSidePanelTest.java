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
package org.exbin.framework.component.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.action.EditItemActions;
import org.exbin.framework.component.api.action.EditItemActionsHandlerEmpty;
import org.exbin.framework.component.api.action.MoveItemActions;
import org.exbin.framework.component.api.action.MoveItemActionsHandlerEmpty;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;
import org.junit.Test;
import org.exbin.framework.component.api.ContextEditItem;
import org.exbin.framework.component.api.ContextMoveItem;
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
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            testApplication.addModule(org.exbin.framework.action.api.ActionModuleApi.MODULE_ID, new org.exbin.framework.action.ActionModule());
            ComponentModule guiComponentModule = new ComponentModule();
            testApplication.addModule(ComponentModule.MODULE_ID, guiComponentModule);

            ToolBarSidePanel toolBarSidePanel = new ToolBarSidePanel();
            ContextMoveItem moveItemActionsHandler = new MoveItemActionsHandlerEmpty();
            MoveItemActions moveItemActions = guiComponentModule.createMoveItemActions(moveItemActionsHandler);
            toolBarSidePanel.addActions(moveItemActions);
            toolBarSidePanel.addSeparator();

            ContextEditItem editItemActionsHandler = new EditItemActionsHandlerEmpty();
            EditItemActions editItemActions = guiComponentModule.createEditItemActions(editItemActionsHandler);
            toolBarSidePanel.addActions(editItemActions);
            WindowUtils.invokeWindow(toolBarSidePanel);
        });

        Thread[] uiThread = new Thread[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                uiThread[0] = Thread.currentThread();
            });
            uiThread[0].join();
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ToolBarSidePanelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
