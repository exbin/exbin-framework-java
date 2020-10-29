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
package org.exbin.framework.gui.menu.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Menu group definition.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuGroup {

    private final String groupId;
    private final MenuPosition position;
    private final SeparationMode separationMode;

    public MenuGroup(String groupId, MenuPosition position) {
        this.groupId = groupId;
        this.position = position;
        separationMode = SeparationMode.NONE;
    }

    public MenuGroup(String groupId, MenuPosition position, SeparationMode separationMode) {
        this.groupId = groupId;
        this.position = position;
        this.separationMode = separationMode;
    }

    @Nonnull
    public String getGroupId() {
        return groupId;
    }

    @Nonnull
    public MenuPosition getPosition() {
        return position;
    }

    @Nonnull
    public SeparationMode getSeparationMode() {
        return separationMode;
    }
}
