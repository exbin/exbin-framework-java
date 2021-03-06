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
package org.exbin.framework.gui.action.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tool bar position.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarPosition {

    private final PositionMode basicMode;
    private final String groupId;

    public ToolBarPosition(PositionMode basicMode) {
        this.basicMode = basicMode;
        groupId = null;
    }

    public ToolBarPosition(String groupId) {
        basicMode = null;
        this.groupId = groupId;
    }

    @Nullable
    public PositionMode getBasicMode() {
        return basicMode;
    }

    @Nullable
    public String getGroupId() {
        return groupId;
    }
}
