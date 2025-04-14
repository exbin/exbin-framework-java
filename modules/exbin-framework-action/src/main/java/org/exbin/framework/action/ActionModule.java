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
package org.exbin.framework.action;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.utils.ClipboardActionsHandler;

/**
 * Implementation of action module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionModule implements ActionModuleApi {

    private ClipboardActions clipboardActions = null;
    private ClipboardTextActions clipboardTextActions = null;
    private ResourceBundle resourceBundle;

    public ActionModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ActionModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public ClipboardActions getClipboardActions() {
        if (clipboardActions == null) {
            clipboardActions = new ClipboardActions();
            ensureSetup();
            clipboardActions.setup(resourceBundle);
        }

        return clipboardActions;
    }

    @Nonnull
    @Override
    public ClipboardTextActions getClipboardTextActions() {
        if (clipboardTextActions == null) {
            clipboardTextActions = new ClipboardTextActions();
            ensureSetup();
            clipboardTextActions.setup(resourceBundle);
        }

        return clipboardTextActions;
    }

    @Nonnull
    @Override
    public ActionManager createActionManager() {
        return new DefaultActionManager();
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, String actionId) {
        initAction(action, bundle, action.getClass(), actionId);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ActionConsts.ACTION_NAME_POSTFIX));
        action.putValue(ActionConsts.ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX)) {
            String key = bundle.getString(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX);
            URL resourceUrl = resourceClass.getResource(key);
            if (resourceUrl != null) {
                try {
                    action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(resourceUrl));
                } catch (Exception ex) {
                    Logger.getLogger(ActionModule.class.getName()).log(Level.SEVERE, "Icon loading failed", ex);
                }
            } else {
                Logger.getLogger(ActionModule.class.getName()).log(Level.SEVERE, "Invalid action icon for key: {0}", key);
            }
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX)) {
            String key = bundle.getString(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX);
            URL resourceUrl = resourceClass.getResource(key);
            if (resourceUrl != null) {
                try {
                    action.putValue(Action.LARGE_ICON_KEY, new javax.swing.ImageIcon(resourceUrl));
                } catch (Exception ex) {
                    Logger.getLogger(ActionModule.class.getName()).log(Level.SEVERE, "Icon loading failed", ex);
                }
            } else {
                Logger.getLogger(ActionModule.class.getName()).log(Level.SEVERE, "Invalid action icon for key: {0}", key);
            }
        }
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action) {
        return actionToMenuItem(action, null);
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        return actionToMenuItemInt(action, buttonGroups);
    }

    @Nonnull
    private JMenuItem actionToMenuItemInt(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        JMenuItem menuItem;
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    menuItem = UiUtils.createCheckBoxMenuItem();
                    menuItem.setAction(action);
                    break;
                }
                case RADIO: {
                    menuItem = UiUtils.createRadioButtonMenuItem();
                    menuItem.setAction(action);
                    String radioGroup = (String) action.getValue(ActionConsts.ACTION_RADIO_GROUP);
                    if (buttonGroups != null) {
                        ButtonGroup buttonGroup = buttonGroups.get(radioGroup);
                        if (buttonGroup == null) {
                            buttonGroup = new ButtonGroup();
                            buttonGroups.put(radioGroup, buttonGroup);
                        }
                        buttonGroup.add(menuItem);
                    }
                    break;
                }
                default: {
                    menuItem = UiUtils.createMenuItem();
                    menuItem.setAction(action);
                }
            }
        } else {
            menuItem = UiUtils.createMenuItem();
            menuItem.setAction(action);
        }

        Object dialogMode = action.getValue(ActionConsts.ACTION_DIALOG_MODE);
        if (dialogMode instanceof Boolean && ((Boolean) dialogMode)) {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            menuItem.setText(languageModule.getActionWithDialogText(menuItem.getText()));
        }
        return menuItem;
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
//        getClipboardActions().setClipboardActionsHandler(clipboardHandler);
    }

    /*
    @Nonnull
    @Override
    public ClipboardActionsApi createClipboardActions(ClipboardActionsHandler clipboardActionsHandler) {
        ClipboardActions customClipboardActions = new ClipboardActions();
        customClipboardActions.setup(resourceBundle);
        customClipboardActions.setClipboardActionsHandler(clipboardActionsHandler);
        return customClipboardActions;
    }
     */
    public void registerClipboardFlavorListener(ActionContextChangeManager activationManager) {
        ClipboardUtils.getClipboard().addFlavorListener(new FlavorListener() {

            private final ClipboardFlavorState clipboardFlavorState = new ClipboardFlavorState();

            @Override
            public void flavorsChanged(FlavorEvent fe) {
                activationManager.updateActionsForComponent(ClipboardFlavorState.class, clipboardFlavorState);
            }
        });
    }
}
