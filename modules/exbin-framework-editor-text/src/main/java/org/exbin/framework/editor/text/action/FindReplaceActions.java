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
package org.exbin.framework.editor.text.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.editor.text.EditorTextPanelComponent;
import org.exbin.framework.editor.text.gui.FindTextPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.editor.text.service.TextSearchService;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Find/replace actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void showFindDialog(TextPanel textPanel, FindDialogMode findDialogMode) {
        final WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        final FindTextPanel findPanel = new FindTextPanel();
        findPanel.setShallReplace(findDialogMode == FindDialogMode.REPLACE);
        findPanel.setSelected();
        DefaultControlPanel controlPanel = new DefaultControlPanel(findPanel.getResourceBundle());
        final WindowHandler dialog = windowModule.createDialog(findPanel, controlPanel);
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType == DefaultControlController.ControlActionType.OK) {
                TextSearchService.FindTextParameters findTextParameters = new TextSearchService.FindTextParameters();
                findTextParameters.setFindText(findPanel.getFindText());
                findTextParameters.setSearchFromStart(findPanel.isSearchFromStart());
                findTextParameters.setShallReplace(findPanel.isShallReplace());
                findTextParameters.setReplaceText(findPanel.getReplaceText());

                textPanel.findText(findTextParameters);
            }

            dialog.close();
            dialog.dispose();
        });
        windowModule.addHeaderPanel(dialog.getWindow(), findPanel.getClass(), findPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, findPanel.getResourceBundle());
        dialog.showCentered(frameModule.getFrame());
    }

    @Nonnull
    public EditFindAction createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        editFindAction.setup(resourceBundle);
        return editFindAction;
    }

    @Nonnull
    public EditFindAgainAction createEditFindAgainAction() {
        EditFindAgainAction editFindAgainAction = new EditFindAgainAction();
        editFindAgainAction.setup(resourceBundle);
        return editFindAgainAction;
    }

    @Nonnull
    public EditReplaceAction createEditReplaceAction() {
        EditReplaceAction editReplaceAction = new EditReplaceAction();
        editReplaceAction.setup(resourceBundle);
        return editReplaceAction;
    }

    @ParametersAreNonnullByDefault
    public class EditFindAction extends AbstractAction {

        public static final String ACTION_ID = "editFindAction";

        private TextPanel textPanel;

        public EditFindAction() {
        }

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ActionContextChangeManager manager) {
                    manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(instance != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog((TextPanel) textPanel, FindDialogMode.FIND);
        }
    }

    @ParametersAreNonnullByDefault
    public class EditFindAgainAction extends AbstractAction {

        public static final String ACTION_ID = "editFindAgainAction";

        private TextPanel textPanel;

        public EditFindAgainAction() {
        }

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ActionContextChangeManager manager) {
                    manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(instance != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog(textPanel, FindDialogMode.FIND);
        }
    }

    @ParametersAreNonnullByDefault
    public class EditReplaceAction extends AbstractAction {

        public static final String ACTION_ID = "editReplaceAction";

        private TextPanel textPanel;

        public EditReplaceAction() {
        }

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ActionContextChangeManager manager) {
                    manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(instance != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog(textPanel, FindDialogMode.REPLACE);
        }
    }

    public enum FindDialogMode {
        FIND,
        REPLACE
    }
}
