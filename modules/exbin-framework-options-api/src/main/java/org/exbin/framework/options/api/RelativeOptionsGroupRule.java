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
package org.exbin.framework.options.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Options group relative position rule.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RelativeOptionsGroupRule implements OptionsGroupRule {

    private final NextToMode nextToMode;
    private final String groupId;

    public RelativeOptionsGroupRule(NextToMode nextToMode, String groupId) {
        this.nextToMode = nextToMode;
        this.groupId = groupId;
    }

    @Nonnull
    public NextToMode getNextToMode() {
        return nextToMode;
    }

    @Nonnull
    public String getGroupId() {
        return groupId;
    }

    /**
     * Enumeration of menu next to modes.
     */
    public enum NextToMode {

        BEFORE,
        AFTER
    }
}
