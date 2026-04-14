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
package org.exbin.jaguif.document.text.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.document.text.EditorTextPanelComponent;
import org.exbin.jaguif.document.text.gui.FindTextPanel;
import org.exbin.jaguif.document.text.gui.TextPanel;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.utils.ActionUtils;
import org.exbin.jaguif.window.api.gui.DefaultControlPanel;
import org.exbin.jaguif.document.text.service.TextSearchService;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.window.api.controller.DefaultControlController;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Find/replace actions.
 * <p>
 * TODO: Drop in favor of exbin-framework-search
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void showFindDialog(DialogParentComponent dialogParentComponent, TextPanel textPanel, FindDialogMode findDialogMode) {
        final WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
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
        dialog.showCentered(dialogParentComponent.getComponent());
    }

    @Nonnull
    public EditFindAction createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        editFindAction.init(resourceBundle);
        return editFindAction;
    }

    @Nonnull
    public EditFindAgainAction createEditFindAgainAction() {
        EditFindAgainAction editFindAgainAction = new EditFindAgainAction();
        editFindAgainAction.init(resourceBundle);
        return editFindAgainAction;
    }

    @Nonnull
    public EditReplaceAction createEditReplaceAction() {
        EditReplaceAction editReplaceAction = new EditReplaceAction();
        editReplaceAction.init(resourceBundle);
        return editReplaceAction;
    }

    @ParametersAreNonnullByDefault
    public class EditFindAction extends AbstractAction {

        public static final String ACTION_ID = "editFind";

        private DialogParentComponent dialogParentComponent;
        private TextPanel textPanel;

        public EditFindAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                    registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                        dialogParentComponent = instance;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog(dialogParentComponent, textPanel, FindDialogMode.FIND);
        }
    }

    @ParametersAreNonnullByDefault
    public class EditFindAgainAction extends AbstractAction {

        public static final String ACTION_ID = "editFindAgain";

        private DialogParentComponent dialogParentComponent;
        private TextPanel textPanel;

        public EditFindAgainAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                    registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                        dialogParentComponent = instance;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog(dialogParentComponent, textPanel, FindDialogMode.FIND);
        }
    }

    @ParametersAreNonnullByDefault
    public class EditReplaceAction extends AbstractAction {

        public static final String ACTION_ID = "editReplace";

        private DialogParentComponent dialogParentComponent;
        private TextPanel textPanel;

        public EditReplaceAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                    registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                        dialogParentComponent = instance;
                        setEnabled(textPanel != null && dialogParentComponent != null);
                    });
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFindDialog(dialogParentComponent, textPanel, FindDialogMode.REPLACE);
        }
    }

    public enum FindDialogMode {
        FIND,
        REPLACE
    }
}
