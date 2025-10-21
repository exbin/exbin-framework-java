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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.TreeContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.ApplySettingsDependsOnRule;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.settings.api.SettingsOptionsBuilder;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * Options settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsSetingsManager extends TreeContributionManager implements OptionsSettingsManagement {

    protected final Map<Class<?>, SettingsOptionsBuilder> optionsSettings = new HashMap<>();
    protected final Map<Class<?>, List<ApplySettingsContribution>> applySettingsContributions = new HashMap<>();
    protected final Map<ApplySettingsContribution, List<ApplySettingsDependsOnRule>> applySettingsContributionRules = new HashMap<>();

    protected final ContributionDefinition definition = new ContributionDefinition();

    public OptionsSetingsManager() {
    }

    @Override
    public <T extends SettingsOptions> void registerOptionsSettings(Class<T> settingsClass, SettingsOptionsBuilder<T> builder) {
        optionsSettings.put(settingsClass, builder);
    }

    @Nonnull
    @Override
    public SettingsComponentContribution registerComponent(String contributionId, SettingsComponentProvider<?> componentProvider) {
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
        TreeContributionSequenceOutput output = new TreeContributionSequenceOutput() {

            private List<SettingsPathItem> path = new ArrayList<>();
            private SettingsPage settingsPage = new SettingsPage("root");

            @Override
            public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
                // TODO
                return true;
            }

            @Override
            public void add(SequenceContribution contribution) {
                if (contribution instanceof SubSequenceContribution) {
                    SubSequenceContribution subContribution = (SubSequenceContribution) contribution;
                    System.out.println("SUB: " + subContribution.getContributionId());
                    if (subContribution instanceof SettingsPageContribution) {
                        SettingsPageContribution pageContribution = (SettingsPageContribution) subContribution;

                        if (settingsPage != null) {
                            settingsPage.finish();
                            settingsPageReceiver.addSettingsPage(settingsPage, path.isEmpty() ? null : path);
                        }

                        path = new ArrayList<>();
                        path.add(new SettingsPathItem(subContribution.getContributionId(), pageContribution.getPageName()));

    //                    SettingsComponentProvider settingsComponentProvider = ((SettingsComponentContribution) itemContribution).getSettingsComponentProvider();
                        settingsPage = new SettingsPage(pageContribution.getContributionId());
                    }
                    return;
                }

                System.out.println("ITEM: " + ((ItemSequenceContribution) contribution).getContributionId());
                if (contribution instanceof SettingsComponentContribution) {
                    SettingsComponentContribution componentContribution = (SettingsComponentContribution) contribution;
                    SettingsComponent component = componentContribution.getSettingsComponentProvider().createComponent();
                    if (settingsPage != null) {
                        settingsPage.addComponent(component);
                    }
                }
            }

            @Override
            public void addSeparator() {
                // TODO
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
        buildSequence(output, "settings", definition);
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

    public void applyOptions(Class<?> instanceClass, Object targetObject, OptionsStorage optionsStorage) {
        List<ApplySettingsContribution> classApplySettings = applySettingsContributions.get(instanceClass);
        if (classApplySettings == null) {
            return;
        }

        for (ApplySettingsContribution applySettings : classApplySettings) {
            SettingsApplier settingsApplier = applySettings.getSettingsApplier();
            // TODO
            settingsApplier.applySettings(targetObject, null);
        }
    }
}
