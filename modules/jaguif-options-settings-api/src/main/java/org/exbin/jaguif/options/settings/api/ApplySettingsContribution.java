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
package org.exbin.jaguif.options.settings.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Settings applier contribution.
 */
@ParametersAreNonnullByDefault
@Immutable
public class ApplySettingsContribution {

    private final String contributionId;
    private final SettingsApplier settingsApplier;

    public ApplySettingsContribution(String contributionId, SettingsApplier settingsApplier) {
        this.contributionId = contributionId;
        this.settingsApplier = settingsApplier;
    }

    @Nonnull
    public String getContributionId() {
        return contributionId;
    }

    @Nonnull
    public SettingsApplier getSettingsApplier() {
        return settingsApplier;
    }
}
