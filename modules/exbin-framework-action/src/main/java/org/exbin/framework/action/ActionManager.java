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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.ComponentActivationInstanceListener;

/**
 * Action manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManager {

    private final Map<Class<?>, List<ComponentActivationInstanceListener<?>>> activeComponentListeners = new HashMap<>();
    private final Map<Class<?>, Object> activeComponentState = new HashMap<>();

    public ActionManager() {
    }

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param actionId action identifier and bundle key prefix
     */
    public void initAction(Action action, ResourceBundle bundle, String actionId) {
        ActionManager.this.initAction(action, bundle, action.getClass(), actionId);
    }
    
    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param resourceClass resourceClass
     * @param actionId action identifier and bundle key prefix
     */
    public void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ActionConsts.ACTION_NAME_POSTFIX));
        action.putValue(ActionConsts.ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX)) {
            action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX))));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX)) {
            action.putValue(Action.LARGE_ICON_KEY, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX))));
        }
    }

    @Nonnull
    public JMenuItem actionToMenuItem(Action action) {
        return actionToMenuItem(action, null);
    }

    @Nonnull
    public JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        JMenuItem menuItem;
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    menuItem = new JCheckBoxMenuItem(action);
                    break;
                }
                case RADIO: {
                    menuItem = new JRadioButtonMenuItem(action);
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
                    menuItem = new JMenuItem(action);
                }
            }
        } else {
            menuItem = new JMenuItem(action);
        }

        Object dialogMode = action.getValue(ActionConsts.ACTION_DIALOG_MODE);
        if (dialogMode instanceof Boolean && ((Boolean) dialogMode)) {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            menuItem.setText(languageModule.getActionWithDialogText(menuItem.getText()));
        }

        return menuItem;
    }

    @SuppressWarnings("unchecked")
    public <T> void updateActionsForComponent(Class<T> componentClass, @Nullable T componentInstance) {
        activeComponentState.put(componentClass, componentInstance);
        List<ComponentActivationInstanceListener<?>> componentListeners = activeComponentListeners.get(componentClass);
        if (componentListeners != null) {
            for (ComponentActivationInstanceListener componentListener : componentListeners) {
                componentListener.update(componentInstance);
            }
        }
    }
}
