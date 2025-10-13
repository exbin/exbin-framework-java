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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for apply settings management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ApplySettingsManagement {

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
     * @param applySettings apply settings contribution
     * @param applySettingsRule apply settings rule
     */
    void registerApplySettingRule(ApplySettingsContribution applySettings, ApplySettingsContributionRule applySettingsRule);
}
