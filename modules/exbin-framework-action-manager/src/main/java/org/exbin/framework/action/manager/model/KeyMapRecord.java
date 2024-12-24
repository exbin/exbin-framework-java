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
package org.exbin.framework.action.manager.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * Key map record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class KeyMapRecord {

    private final String name;
    private final String type;
    private final ImageIcon icon;
    private final KeyStroke shortcut;

    public KeyMapRecord(String name, @Nullable ImageIcon icon, String type, @Nullable KeyStroke shortcut) {
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.shortcut = shortcut;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public ImageIcon getIcon() {
        return icon;
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nullable
    public KeyStroke getShortcut() {
        return shortcut;
    }
}
