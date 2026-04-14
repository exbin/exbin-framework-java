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
package org.exbin.jaguif.options.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.contribution.ContributionDefinition;
import org.exbin.jaguif.contribution.TreeContributionSequenceBuilder;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.contribution.api.TreeContributionSequenceOutput;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.ApplySettingsDependsOnRule;
import org.exbin.jaguif.options.settings.api.ApplySettingsListener;
import org.exbin.jaguif.options.settings.api.InferenceOptions;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;
import org.exbin.jaguif.options.settings.api.SettingsOptions;
import org.exbin.jaguif.options.settings.api.SettingsOptionsBuilder;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.utils.ObjectUtils;

/**
 * Options settings manager.
 */
@ParametersAreNonnullByDefault
public class OptionsSettingsManager extends TreeContributionSequenceBuilder implements OptionsSettingsManagement {

    protected final Map<Class<? extends SettingsOptions>, SettingsOptionsBuilder> settingsOptions = new HashMap<>();
    protected final Map<Class<? extends InferenceOptions>, InferenceOptions> inferenceOptions = new HashMap<>();

    protected final Map<Class<? extends SettingsOptions>, List<ApplySettingsContribution>> applySettingsContributions = new HashMap<>();
    protected final Map<Class<?>, List<ApplySettingsContribution>> applyContextSettingsContributions = new HashMap<>();
    protected final Map<ApplySettingsContribution, List<ApplySettingsDependsOnRule>> applySettingsContributionRules = new HashMap<>();
    protected final List<ApplySettingsListener> applySettingsListeners = new ArrayList<>();
    protected SettingsOptionsProvider settingsOptionsProvider;

    protected final ContributionDefinition definition = new ContributionDefinition();

    public OptionsSettingsManager() {
    }

    @Override
    public <T extends SettingsOptions> void registerSettingsOptions(Class<T> settingsClass, SettingsOptionsBuilder<T> builder) {
        settingsOptions.put(settingsClass, builder);
    }

    @Override
    public <T extends InferenceOptions> void registerInferenceOptions(Class<T> inferenceClass, T inference) {
        inferenceOptions.put(inferenceClass, inference);
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
        List<SequenceContribution> contributions = definition.getContributions();
        for (SequenceContribution contribution : contributions) {
            if (contribution instanceof GroupSequenceContribution) {
                if (((GroupSequenceContribution) contribution).getGroupId().equals(groupId)) {
                    return true;
                }
            }
        }

        return false;
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
    public SettingsOptionsBuilder getSettingsOptionsBuilder(Class<? extends SettingsOptions> settingsClass) {
        return ObjectUtils.requireNonNull(settingsOptions.get(settingsClass), "Missing options settings builder: " + settingsClass.getCanonicalName());
    }

    @Nonnull
    @Override
    public <T extends InferenceOptions> Optional<T> getInferenceOptions(Class<T> inferenceClass) {
        return Optional.ofNullable(inferenceClass.cast(inferenceOptions.get(inferenceClass)));
    }

    @Override
    public void registerApplySetting(Class<? extends SettingsOptions> settingsClass, ApplySettingsContribution applySettingContribution) {
        List<ApplySettingsContribution> contributions = applySettingsContributions.get(settingsClass);
        if (contributions == null) {
            contributions = new ArrayList<>();
            applySettingsContributions.put(settingsClass, contributions);
        }

        contributions.add(applySettingContribution);
    }

    @Override
    public void registerApplyContextSetting(Class<?> contextTypeClass, ApplySettingsContribution applySettingContribution) {
        List<ApplySettingsContribution> contributions = applyContextSettingsContributions.get(contextTypeClass);
        if (contributions == null) {
            contributions = new ArrayList<>();
            applyContextSettingsContributions.put(contextTypeClass, contributions);
        }

        contributions.add(applySettingContribution);
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
    public void registerApplyListener(ApplySettingsListener listener) {
        applySettingsListeners.add(listener);
    }

    @Nonnull
    @Override
    public Collection<Class<? extends SettingsOptions>> getOptionsClasses() {
        return settingsOptions.keySet();
    }

    @Override
    public void applyContextOptions(Class<?> contextTypeClass, Object contextInstance, SettingsOptionsProvider settingsProvider) {
        List<ApplySettingsContribution> contribution = applyContextSettingsContributions.get(contextTypeClass);
        if (contribution == null) {
            return;
        }

        // TODO Rework for context provider parameter?
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextProvider = frameModule.getFrameHandler().getContextManager();
        contextProvider.changeActiveState((Class) contextTypeClass, contextInstance);

        for (ApplySettingsContribution applySettings : contribution) {
            SettingsApplier settingsApplier = applySettings.getSettingsApplier();
            settingsApplier.applySettings(contextProvider, settingsProvider);
        }
    }

    @Override
    public void applyOptions(Class<? extends SettingsOptions> optionsClass, SettingsOptionsProvider settingsProvider) {
        List<ApplySettingsContribution> contribution = applySettingsContributions.get(optionsClass);
        if (contribution == null) {
            return;
        }

        // TODO Rework for context provider parameter?
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextProvider = frameModule.getFrameHandler().getContextManager();

        for (ApplySettingsContribution applySettings : contribution) {
            SettingsApplier settingsApplier = applySettings.getSettingsApplier();
            settingsApplier.applySettings(contextProvider, settingsProvider);
        }
    }

    @Override
    public void applyAllOptions(ActiveContextManagement contextManager, SettingsOptionsProvider provider) {
        for (ApplySettingsListener listener : applySettingsListeners) {
            listener.applySettings(contextManager, provider);
        }
    }

    @Nonnull
    @Override
    public SettingsOptionsProvider getSettingsOptionsProvider() {
        if (settingsOptionsProvider == null) {
            settingsOptionsProvider = new SettingsOptionsProvider() {

                Map<Class<?>, SettingsOptions> settingsOptionsCache = new HashMap<>();

                @Nonnull
                @Override
                @SuppressWarnings("unchecked")
                public <T extends SettingsOptions> T getSettingsOptions(Class<T> settingsClass) {
                    SettingsOptions instance = settingsOptionsCache.get(settingsClass);
                    if (instance == null) {
                        SettingsOptionsBuilder builder = settingsOptions.get(settingsClass);
                        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                        instance = builder.createInstance(optionsModule.getAppOptions());
                        settingsOptionsCache.put(settingsClass, instance);
                    }

                    return (T) instance;
                }

                @Nonnull
                @Override
                public <T extends InferenceOptions> Optional<T> getInferenceOptions(Class<T> inferenceClass) {
                    return Optional.ofNullable(inferenceClass.cast(inferenceOptions.get(inferenceClass)));
                }
            };
        }

        return settingsOptionsProvider;
    }
}
