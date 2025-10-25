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
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Interface for management of options settings.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsSettingsManagement {

    /**
     * Registers settings options.
     *
     * @param <T> settings options type
     * @param settingsClass settings options class
     * @param builder settings options builder
     */
    <T extends SettingsOptions> void registerOptionsSettings(Class<T> settingsClass, SettingsOptionsBuilder<T> builder);

    /**
     * Registers settings component.
     *
     * @param contributionId contribution id
     * @param componentProvider component provider
     * @return contribution instance
     */
    @Nonnull
    SettingsComponentContribution registerComponent(String contributionId, SettingsComponentProvider componentProvider);

    /**
     * Registers settings page.
     *
     * @param pageContribution page contribution
     */
    void registerPage(SettingsPageContribution pageContribution);

    /**
     * Registers settings group.
     *
     * @param groupId group id
     * @return contribution instance
     */
    @Nonnull
    GroupSequenceContribution registerGroup(String groupId);

    /**
     * Returns true if given group exists.
     *
     * @param groupId group id
     * @return true if group exists
     */
    boolean groupExists(String groupId);

    /**
     * Registers settings component rule.
     *
     * @param contribution contribution
     * @param rule contribution rule
     */
    void registerSettingsRule(SequenceContribution contribution, SequenceContributionRule rule);

    /**
     * Registers apply settings method.
     *
     * @param instanceClass instance class
     * @param applySettingContribution apply settings method
     */
    void registerApplySetting(Class<?> instanceClass, ApplySettingsContribution applySettingContribution);

    /**
     * Registers apply settings rule.
     *
     * @param applySettingsContribution apply settings contribution
     * @param applySettingsRule apply settings rule
     */
    void registerApplySettingRule(ApplySettingsContribution applySettingsContribution, ApplySettingsDependsOnRule applySettingsRule);
}
