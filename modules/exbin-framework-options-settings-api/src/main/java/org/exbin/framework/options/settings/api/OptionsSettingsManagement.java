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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ActiveContextManagement;
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
    <T extends SettingsOptions> void registerSettingsOptions(Class<T> settingsClass, SettingsOptionsBuilder<T> builder);

    /**
     * Registers inference options.
     *
     * @param <T> inference options type
     * @param inferenceClass inference options class
     * @param inference inference options instance
     */
    <T extends InferenceOptions> void registerInferenceOptions(Class<T> inferenceClass, T inference);

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
     * Returns options settings builder.
     *
     * @param settingsClass settings class
     * @return options settings builder
     */
    @Nonnull
    SettingsOptionsBuilder getSettingsOptionsBuilder(Class<? extends SettingsOptions> settingsClass);

    /**
     * Returns inference options instance if available.
     *
     * @param <T> inference options type
     * @param inferenceClass inference class
     * @return inference options instance
     */
    @Nonnull
    <T extends InferenceOptions> Optional<T> getInferenceOptions(Class<T> inferenceClass);

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

    /**
     * Registers apply settings listener.
     *
     * @param listener apply settings listener
     */
    void registerApplyListener(ApplySettingsListener listener);

    /**
     * Applies options for specific instance and value.
     *
     * @param instanceClass instance class
     * @param targetObject instance value
     * @param provider settings options provider
     */
    void applyOptions(Class<?> instanceClass, Object targetObject, SettingsOptionsProvider provider);

    /**
     * Applies all options.
     *
     * @param contextManager context manager
     * @param provider settings options provider
     */
    void applyAllOptions(ActiveContextManagement contextManager, SettingsOptionsProvider provider);

    /**
     * Returns settings options provider.
     *
     * @return settings options provider
     */
    @Nonnull
    SettingsOptionsProvider getSettingsOptionsProvider();
}
