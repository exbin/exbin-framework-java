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

import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;

/**
 * Settings page contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@Immutable
public class SettingsPageContribution implements SubSequenceContribution {

    private final String pageId;
    protected final ResourceBundle resourceBundle;

    public SettingsPageContribution(String pageId, @Nullable ResourceBundle resourceBundle) {
        this.pageId = pageId;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return pageId;
    }

    @Nullable
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Nullable
    public String getPageName() {
        if (resourceBundle == null) {
            return null;
        }

        return resourceBundle.getString("settingsPage." + pageId + ".name");
    }

    @Nonnull
    @Override
    public Optional<TreeContributionSequenceOutput> getSubOutput() {
        return Optional.of(new TreeContributionSequenceOutput() {
            @Override
            public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
                return true;
            }

            @Override
            public void add(SequenceContribution contribution) {
                System.out.println("SP" + (contribution instanceof SubSequenceContribution
                        ? "S " + pageId + ": " + ((SubSequenceContribution) contribution).getContributionId()
                        : "A " + pageId + ": " + ((ItemSequenceContribution) contribution).getContributionId()));
            }

            @Override
            public void addSeparator() {
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
        // return Optional.empty();
    }
}
