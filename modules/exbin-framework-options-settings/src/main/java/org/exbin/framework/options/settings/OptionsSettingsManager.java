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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.TreeContributionSequenceBuilder;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.ApplySettingsDependsOnRule;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.settings.api.SettingsOptionsBuilder;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.utils.ObjectUtils;

/**
 * Options settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsSettingsManager extends TreeContributionSequenceBuilder implements OptionsSettingsManagement {

    protected final Map<Class<? extends SettingsOptions>, SettingsOptionsBuilder> optionsSettings = new HashMap<>();

    protected final Map<Class<?>, List<ApplySettingsContribution>> applySettingsContributions = new HashMap<>();
    protected final Map<ApplySettingsContribution, List<ApplySettingsDependsOnRule>> applySettingsContributionRules = new HashMap<>();
    protected SettingsOptionsProvider settingsOptionsProvider;

    protected final ContributionDefinition definition = new ContributionDefinition();

    public OptionsSettingsManager() {
    }

    @Override
    public <T extends SettingsOptions> void registerOptionsSettings(Class<T> settingsClass, SettingsOptionsBuilder<T> builder) {
        optionsSettings.put(settingsClass, builder);
    }

    @Nonnull
    @Override
    public SettingsComponentContribution registerComponent(String contributionId, SettingsComponentProvider componentProvider) {
        SettingsComponentContribution contribution = new SettingsComponentContribution(contributionId, componentProvider);
        definition.addContribution(contribution);
        return contribution;
    }

    @Override
    public void registerPage(SettingsPageContribution contribution) {
        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerGroup(String groupId) {
        GroupSequenceContribution contribution = new GroupSequenceContribution(groupId);
        definition.addContribution(contribution);
        return contribution;
    }

    @Override
    public boolean groupExists(String groupId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerSettingsRule(SequenceContribution contribution, SequenceContributionRule rule) {
        definition.addRule(contribution, rule);
    }

    public void passSettingsPages(SettingsPageReceiver settingsPageReceiver) {
        SettingsPage settingsPage = new SettingsPage(OptionsSettingsModule.OPTIONS_PANEL_KEY);
        TreeContributionSequenceOutput output = new SettingsSequenceOutput(settingsPageReceiver, settingsPage, new ArrayList<>());
        List<SettingsPathItem> path = new ArrayList<>();
        path.add(new SettingsPathItem(OptionsSettingsModule.OPTIONS_PANEL_KEY, null));
        settingsPageReceiver.addSettingsPage(settingsPage, path);
        buildSequence(output, OptionsSettingsModule.OPTIONS_PANEL_KEY, definition);
        settingsPage.finish();
    }

    @Nonnull
    @Override
    public SettingsOptionsBuilder getOptionsSettingsBuilder(Class<? extends SettingsOptions> settingsClass) {
        return ObjectUtils.requireNonNull(optionsSettings.get(settingsClass), "Missing options settings builder: " + settingsClass.getCanonicalName());
    }

    @Override
    public void registerApplySetting(Class<?> instanceClass, ApplySettingsContribution applySetting) {
        List<ApplySettingsContribution> classApplySettings = applySettingsContributions.get(instanceClass);
        if (classApplySettings == null) {
            classApplySettings = new ArrayList<>();
            applySettingsContributions.put(instanceClass.getClass(), classApplySettings);
        }

        classApplySettings.add(applySetting);
    }

    @Override
    public void registerApplySettingRule(ApplySettingsContribution applySettings, ApplySettingsDependsOnRule applySettingsRule) {
        List<ApplySettingsDependsOnRule> rules = applySettingsContributionRules.get(applySettings);
        if (rules == null) {
            rules = new ArrayList<>();
            applySettingsContributionRules.put(applySettings, rules);
        }
        rules.add(applySettingsRule);
    }

    @Override
    public void applyOptions(Class<?> instanceClass, Object targetObject) {
        List<ApplySettingsContribution> classApplySettings = applySettingsContributions.get(instanceClass);
        if (classApplySettings == null) {
            return;
        }

        getSettingsOptionsProvider();
        for (ApplySettingsContribution applySettings : classApplySettings) {
            SettingsApplier settingsApplier = applySettings.getSettingsApplier();
            settingsApplier.applySettings(targetObject, settingsOptionsProvider);
        }
    }

    @Override
    public void applyAllOptions(ActiveContextManagement contextManager) {
        Collection<Class<?>> stateClasses = contextManager.getStateClasses();
        getSettingsOptionsProvider();
        for (Map.Entry<Class<?>, List<ApplySettingsContribution>> entry : applySettingsContributions.entrySet()) {
            Class<?> instanceClass = entry.getKey();
            if (!stateClasses.contains(instanceClass)) {
                continue;
            }

            Object activeState = contextManager.getActiveState(instanceClass);
            if (activeState != null) {
                List<ApplySettingsContribution> contributions = entry.getValue();
                for (ApplySettingsContribution contribution : contributions) {
                    SettingsApplier settingsApplier = contribution.getSettingsApplier();
                    settingsApplier.applySettings(activeState, settingsOptionsProvider);
                }
            }
        }
    }

    @Nonnull
    @Override
    public SettingsOptionsProvider getSettingsOptionsProvider() {
        if (settingsOptionsProvider == null) {
            settingsOptionsProvider = new SettingsOptionsProvider() {

                Map<Class<?>, SettingsOptions> settingsOptions = new HashMap<>();

                @Override
                @SuppressWarnings("unchecked")
                public <T extends SettingsOptions> T getSettingsOptions(Class<T> settingsClass) {
                    SettingsOptions instance = settingsOptions.get(settingsClass);
                    if (instance == null) {
                        SettingsOptionsBuilder builder = optionsSettings.get(settingsClass);
                        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                        instance = builder.createInstance(optionsModule.getAppOptions());
                    }

                    return (T) instance;
                }
            };
        }

        return settingsOptionsProvider;
    }
}
