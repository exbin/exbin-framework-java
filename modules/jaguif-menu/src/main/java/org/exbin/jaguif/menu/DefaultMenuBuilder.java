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
package org.exbin.jaguif.menu;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import org.exbin.jaguif.menu.api.MenuBuilder;
import org.exbin.jaguif.menu.api.MenuShowMethod;

/**
 * Default menu builder.
 */
@ParametersAreNonnullByDefault
public class DefaultMenuBuilder implements MenuBuilder {

    @Nonnull
    @Override
    public JMenu createMenu() {
        return new JMenu();
    }

    @Nonnull
    @Override
    public JPopupMenu createPopupMenu() {
        return new JPopupMenu();
    }

    @Nonnull
    @Override
    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    @Nonnull
    @Override
    public JMenuItem createCheckBoxMenuItem() {
        return new JCheckBoxMenuItem();
    }

    @Nonnull
    @Override
    public JMenuItem createRadioButtonMenuItem() {
        return new JRadioButtonMenuItem();
    }

    @Nonnull
    @Override
    public JPopupMenu createPopupMenu(MenuShowMethod showMethod) {
        return new JPopupMenu() {
            @Override
            public void show(@Nullable Component invoker, int x, int y) {
                showMethod.show(invoker, x, y);
            }
        };
    }
}
