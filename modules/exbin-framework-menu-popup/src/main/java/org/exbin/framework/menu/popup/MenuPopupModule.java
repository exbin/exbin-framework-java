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
package org.exbin.framework.menu.popup;

import java.awt.datatransfer.StringSelection;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.menu.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.UiUtils;

/**
 * Implementation of framework popup module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuPopupModule implements MenuPopupModuleApi {

    private java.util.ResourceBundle resourceBundle = null;

    public MenuPopupModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(MenuPopupModule.class);
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

    @Nonnull
    @Override
    public JPopupMenu createLinkPopupMenu(String targetURL) {
        JPopupMenu popupMenu = UiUtils.createPopupMenu();
        DefaultPopupMenu.getInstance().appendLinkMenu(popupMenu, new LinkActionsHandler() {
            @Override
            public void performCopyLink() {
                StringSelection stringSelection = new StringSelection(targetURL);
                ClipboardUtils.getClipboard().setContents(stringSelection, stringSelection);
            }

            @Override
            public void performOpenLink() {
                DesktopUtils.openDesktopURL(targetURL);
            }

            @Override
            public boolean isLinkSelected() {
                return true;
            }
        });
        return popupMenu;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
