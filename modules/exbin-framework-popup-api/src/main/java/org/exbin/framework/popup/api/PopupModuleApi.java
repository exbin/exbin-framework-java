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
package org.exbin.framework.popup.api;

import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.utils.ComponentPopupEventDispatcher;

/**
 * Interface for framework popup module.
 *
 * @version 0.2.2 2022/05/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface PopupModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(PopupModuleApi.class);

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

    void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position);
}