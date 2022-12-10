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
package org.exbin.framework.action.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;

/**
 * Record for action as menu item contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DirectMenuContribution implements MenuContribution {

    private final JMenu menu;
    private final MenuPosition position;

    public DirectMenuContribution(JMenu menu, MenuPosition position) {
        this.menu = menu;
        this.position = position;
    }

    @Nonnull
    public JMenu getMenu() {
        return menu;
    }

    @Nonnull
    @Override
    public MenuPosition getMenuPosition() {
        return position;
    }
}
