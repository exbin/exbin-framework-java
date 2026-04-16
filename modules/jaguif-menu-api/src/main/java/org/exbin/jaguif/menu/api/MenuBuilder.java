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
package org.exbin.jaguif.menu.api;

import javax.annotation.Nonnull;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Menu builder.
 */
public interface MenuBuilder {

    /**
     * Creates new instance of menu.
     *
     * @return new instance of menu
     */
    @Nonnull
    JMenu createMenu();

    /**
     * Creates new instance of popup menu.
     *
     * @return new instance of popup menu
     */
    @Nonnull
    JPopupMenu createPopupMenu();

    /**
     * Creates new instance of menu item.
     *
     * @return new instance of menu item
     */
    @Nonnull
    JMenuItem createMenuItem();

    /**
     * Creates new instance of check box menu item.
     *
     * @return new instance of check box menu item
     */
    @Nonnull
    JMenuItem createCheckBoxMenuItem();

    /**
     * Creates new instance of radio button menu item.
     *
     * @return new instance of radio button menu item
     */
    @Nonnull
    JMenuItem createRadioButtonMenuItem();

    /**
     * Creates new instance of popup menu.
     *
     * @param showMethod show method
     * @return new instance of popup menu
     */
    @Nonnull
    JPopupMenu createPopupMenu(MenuShowMethod showMethod);
}
