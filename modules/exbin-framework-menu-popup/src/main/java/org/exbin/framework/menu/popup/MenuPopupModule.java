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

import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextRegistrationProvider;
import org.exbin.framework.menu.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
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

    @Override
    public void registerDefaultClipboardPopupMenu() {
        DefaultPopupMenu.register();
    }

    @Override
    public void registerDefaultClipboardPopupMenuWithIcons() {
        DefaultPopupMenu.register();
        DefaultPopupMenu.getInstance().inheritClipboardActionsIcons();
    }

    @Override
    public void registerDefaultClipboardPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        DefaultPopupMenu.register(resourceBundle, resourceClass);
    }

    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        DefaultPopupMenu.getInstance().addClipboardEventDispatcher(dispatcher);
    }

    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        DefaultPopupMenu.getInstance().removeClipboardEventDispatcher(dispatcher);
    }

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

    @Nonnull
    @Override
    public JPopupMenu createComponentPopupMenu(String popupMenuId, ActionContextRegistrationProvider actionContextRegistrar) {
        return new JPopupMenu() {
            @Override
            public void show(@Nullable Component invoker, int x, int y) {
                if (invoker == null) {
                    return;
                }

                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }

                JPopupMenu popupMenu = UiUtils.createPopupMenu();
                MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                menuModule.buildMenu(popupMenu, popupMenuId, actionContextRegistrar.getRegistration());
                popupMenu.show(invoker, clickedX, clickedY);
            }
        };
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
