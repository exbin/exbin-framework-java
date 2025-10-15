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
package org.exbin.framework.options.settings.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Settings page contribution rule.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@Immutable
public class SettingsPageContributionRule implements SequenceContributionRule {

    private final String pageId;

    public SettingsPageContributionRule(String pageId) {
        this.pageId = pageId;
    }

    public SettingsPageContributionRule(SettingsPageContribution pageContribution) {
        this(pageContribution.getContributionId());
    }

    @Nonnull
    public String getPageId() {
        return pageId;
    }
}
