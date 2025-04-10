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
package org.exbin.framework.sidebar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Sidebar contribution rule for item relative position.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@Immutable
public class RelativeSideBarContributionRule implements SideBarContributionRule {

    private final NextToMode nextToMode;
    private final String contributionId;

    public RelativeSideBarContributionRule(NextToMode nextToMode, String contributionId) {
        this.nextToMode = nextToMode;
        this.contributionId = contributionId;
    }

    @Nonnull
    public NextToMode getNextToMode() {
        return nextToMode;
    }

    @Nonnull
    public String getContributionId() {
        return contributionId;
    }

    /**
     * Enumeration of menu next to modes.
     */
    public enum NextToMode {

        BEFORE,
        AFTER
    }
}
