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
package org.exbin.framework.contribution.api;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Sequence contribution rule for item relative position.
 *
 * @author ExBin Project (https://exbin.org)
 */
@Immutable
public interface RelativeSequenceContributionRule extends SequenceContributionRule {

    /**
     * Returns next to mode.
     *
     * @return next to mode
     */
    @Nonnull
    public NextToMode getNextToMode();

    /**
     * Returns position relative contribution id.
     *
     * @return contribution id
     */
    @Nonnull
    public String getContributionId();

    /**
     * Enumeration of menu next to modes.
     */
    public enum NextToMode {

        BEFORE,
        AFTER
    }
}
