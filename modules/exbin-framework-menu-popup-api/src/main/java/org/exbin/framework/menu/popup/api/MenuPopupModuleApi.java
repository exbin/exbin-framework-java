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
package org.exbin.framework.menu.popup.api;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework popup module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuPopupModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(MenuPopupModuleApi.class);

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     */
    void registerDefaultClipboardPopupMenu();

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    void registerDefaultClipboardPopupMenu(ResourceBundle resourceBundle, Class resourceClass);

    /**
     * Adds component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    /**
     * Removes component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    /**
     * Fills given popup menu with default clipboard actions.
     *
     * @param popupMenu popup menu
     * @param position target index position or -1 for adding at the end
     */
    void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position);

    /**
     * Creates component popup menu for link.
     *
     * @param targetURL target URL
     * @return popup menu
     */
    @Nonnull
    JPopupMenu createLinkPopupMenu(String targetURL);
}
