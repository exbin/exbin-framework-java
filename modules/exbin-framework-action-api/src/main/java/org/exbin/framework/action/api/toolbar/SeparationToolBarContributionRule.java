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
package org.exbin.framework.action.api.toolbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.exbin.framework.action.api.SeparationMode;

/**
 * Tool bar contribution rule for items separation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@Immutable
public class SeparationToolBarContributionRule implements ToolBarContributionRule {

    private final SeparationMode separationMode;

    public SeparationToolBarContributionRule(SeparationMode separationMode) {
        this.separationMode = separationMode;
    }

    @Nonnull
    public SeparationMode getSeparationMode() {
        return separationMode;
    }
}
