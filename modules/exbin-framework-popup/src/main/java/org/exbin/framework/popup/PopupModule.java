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
package org.exbin.framework.popup;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.popup.api.PopupModuleApi;
import org.exbin.framework.utils.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework popup module.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PopupModule implements PopupModuleApi {

    private java.util.ResourceBundle resourceBundle = null;
    private XBApplication application;

    public PopupModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(PopupModule.class);
        }

        return resourceBundle;
    }

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     */
    @Nonnull
    @Override
    public void registerDefaultClipboardPopupMenu() {
        DefaultPopupMenu.register();
    }

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    @Nonnull
    @Override
    public void registerDefaultClipboardPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        DefaultPopupMenu.register(resourceBundle, resourceClass);
    }

    @Nonnull
    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        DefaultPopupMenu.getInstance().addClipboardEventDispatcher(dispatcher);
    }

    @Nonnull
    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        DefaultPopupMenu.getInstance().removeClipboardEventDispatcher(dispatcher);
    }

    @Nonnull
    @Override
    public void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position) {
        DefaultPopupMenu.getInstance().fillDefaultEditPopupMenu(popupMenu, position);
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}