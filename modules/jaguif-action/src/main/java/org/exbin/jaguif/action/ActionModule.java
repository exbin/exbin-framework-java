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
package org.exbin.jaguif.action;

import org.exbin.jaguif.action.clipboard.ClipboardTextActions;
import org.exbin.jaguif.action.clipboard.ClipboardActions;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.utils.ClipboardUtils;
import org.exbin.jaguif.action.api.ActionManagement;
import org.exbin.jaguif.action.api.ActionContextRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.action.api.clipboard.ClipboardController;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.context.api.ContextChangeListener;

/**
 * Implementation of action module.
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
            clipboardActions.init(resourceBundle);
        }

        return clipboardActions;
    }

    @Nonnull
    @Override
    public ClipboardTextActions getClipboardTextActions() {
        if (clipboardTextActions == null) {
            clipboardTextActions = new ClipboardTextActions();
            ensureSetup();
            clipboardTextActions.init(resourceBundle);
        }

        return clipboardTextActions;
    }

    @Nonnull
    @Override
    public ActionManagement createActionManager(ActiveContextManagement contextManager) {
        return new ActionManager(contextManager);
    }

    @Nonnull
    @Override
    public ActionContextRegistration createActionContextRegistrar(ActionManagement actionManagement) {
        return new ActionContextRegistrar(actionManagement);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, String actionId) {
        initAction(action, bundle, action.getClass(), actionId);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId) {
        String resourceKeyPrefix = actionId + "Action";
        action.putValue(Action.NAME, bundle.getString(resourceKeyPrefix + ActionConsts.ACTION_NAME_POSTFIX));
        action.putValue(ActionConsts.ACTION_ID, resourceKeyPrefix);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(resourceKeyPrefix + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(resourceKeyPrefix + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(resourceKeyPrefix + ActionConsts.ACTION_SMALL_ICON_POSTFIX)) {
            String key = bundle.getString(resourceKeyPrefix + ActionConsts.ACTION_SMALL_ICON_POSTFIX);
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
        if (bundle.containsKey(resourceKeyPrefix + ActionConsts.ACTION_SMALL_LARGE_POSTFIX)) {
            String key = bundle.getString(resourceKeyPrefix + ActionConsts.ACTION_SMALL_LARGE_POSTFIX);
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
    public ImageIcon getClipboardActionIcon(String actionId) {
        try {
            return new javax.swing.ImageIcon(getClass().getResource(getResourceBundle().getString(actionId + "Action" + ActionConsts.ACTION_SMALL_ICON_POSTFIX)));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Icon loading failed for " + actionId, ex);
        }
    }

    @Override
    public void registerClipboardFlavorListener(ContextChangeListener listener, ActiveContextProvider provider) {
        ClipboardUtils.getClipboard().addFlavorListener(new FlavorListener() {

            @Override
            public void flavorsChanged(FlavorEvent fe) {
                ContextComponent contextComponent = provider.getActiveState(ContextComponent.class);
                if (contextComponent != null) {
                    listener.notifyStateUpdated(ContextComponent.class, contextComponent, ClipboardController.UpdateType.CLIPBOARD_FLAVOR);
                }
            }
        });
    }
}
