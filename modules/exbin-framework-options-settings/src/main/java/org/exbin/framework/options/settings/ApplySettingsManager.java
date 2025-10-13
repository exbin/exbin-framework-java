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
package org.exbin.framework.options.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.ApplySettingsContributionRule;
import org.exbin.framework.options.settings.api.ApplySettingsManagement;

/**
 * Options apply settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ApplySettingsManager implements ApplySettingsManagement {

    private final Map<Class<?>, List<ApplySettingsContribution>> contributions = new HashMap<>();
    private final Map<ApplySettingsContribution, List<ApplySettingsContributionRule>> contributionRules = new HashMap<>();

    @Override
    public void registerApplySetting(Class<?> instanceClass, ApplySettingsContribution applySetting) {
        List<ApplySettingsContribution> classApplySettings = contributions.get(instanceClass);
        if (classApplySettings == null) {
            classApplySettings = new ArrayList<>();
            contributions.put(instanceClass.getClass(), classApplySettings);
        }

        classApplySettings.add(applySetting);
    }

    @Override
    public void registerApplySettingRule(ApplySettingsContribution applySettings, ApplySettingsContributionRule applySettingsRule) {
        List<ApplySettingsContributionRule> rules = contributionRules.get(applySettings);
        if (rules == null) {
            rules = new ArrayList<>();
            contributionRules.put(applySettings, rules);
        }
        rules.add(applySettingsRule);
    }

    public void applyOptions(Class<?> instanceClass, Object targetObject, OptionsStorage optionsStorage) {
        List<ApplySettingsContribution> classApplySettings = contributions.get(instanceClass);
        if (classApplySettings == null) {
            return;
        }

        for (ApplySettingsContribution applySettings : classApplySettings) {
            applySettings.applyOptions(targetObject, optionsStorage);
        }
    }
}
