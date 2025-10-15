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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.ApplySettingsDependsOnRule;
import org.exbin.framework.options.settings.api.DefaultSettingsPage;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.settings.api.SettingsOptionsBuilder;
import org.exbin.framework.options.settings.api.SettingsPage;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * Options settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsSetingsManager extends ContributionManager implements OptionsSettingsManagement {

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
    public SettingsComponentContribution registerComponent(SettingsComponentProvider<?> componentProvider) {
        SettingsComponentContribution contribution = new SettingsComponentContribution(componentProvider.toString(), componentProvider);
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
    public boolean menuGroupExists(String groupId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerSettingsRule(SequenceContribution contribution, SequenceContributionRule rule) {
        definition.addRule(contribution, rule);
    }

    public void passOptionsPages(SettingsPageReceiver optionsPageReceiver) {
        ContributionSequenceOutput output = new ContributionSequenceOutput() {
            @Override
            public boolean initItem(ItemSequenceContribution itemContribution) {
                // TODO
                return true;
            }

            @Override
            public void add(ItemSequenceContribution itemContribution) {
                List<SettingsPathItem> path = new ArrayList<>();
                if (itemContribution instanceof SettingsComponentContribution) {
                    // optionsPageReceiver.addSettingsPage(settingsPage, path);
                } else if (itemContribution instanceof SettingsPageContribution) {
                    SettingsPageContribution pageContribution = (SettingsPageContribution) itemContribution;

                    path.add(new SettingsPathItem(itemContribution.getContributionId(), pageContribution.getPageName()));

//                    SettingsComponentProvider settingsComponentProvider = ((SettingsComponentContribution) itemContribution).getSettingsComponentProvider();
                    SettingsPage<SettingsOptions> settingsPage = new DefaultSettingsPage<SettingsOptions>() {
                        @Override
                        public String getId() {
                            return pageContribution.getContributionId();
                        }

                        @Override
                        public SettingsComponent<SettingsOptions> createComponent() {
                            return new EmptySettingsComponent(); // settingsComponentProvider.createComponent();
                        }

                        @Override
                        public SettingsOptions createOptions() {
                            // TODO
                            return null;
                        }

                        @Override
                        public void loadFromOptions(OptionsStorage optionsStorage, SettingsOptions options) {
                            // TODO
                        }

                        @Override
                        public void saveToOptions(OptionsStorage optionsStorage, SettingsOptions options) {
                            // TODO
                        }

                        @Override
                        public ResourceBundle getResourceBundle() {
                            return null;
                        }
                    };
                    optionsPageReceiver.addSettingsPage(settingsPage, path);
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
        buildSequence(output, definition);
        /* List<String> processedGroupOrder = new ArrayList<>();
        for (String groupId : groupsOrder) {
            List<OptionsGroupRule> rules = groupRules.get(groupId);
            String parentGroupId = null;
            if (rules != null) {
                for (OptionsGroupRule rule : rules) {
                    if (rule instanceof ParentOptionsGroupRule) {
                        parentGroupId = ((ParentOptionsGroupRule) rule).getParentGroupId();
                    } else if (rule instanceof RelativeOptionsPageRule) {
                        // TODO
                    }
                }
            }
//            if (parentGroupId == null) {
            processedGroupOrder.add(groupId);
//            }
        }

        // TODO Tree order + relative reordering
//        List<String> path = new ArrayList<>();
//        for (String groupId : processedGroupOrder) {
//            path.clear();
//            path.add(groupId);
//            int index = processedGroupOrder.indexOf(groupId);
//            
//        }
        // Process pages in groups first
        for (String groupId : processedGroupOrder) {
            for (String pageId : pagesOrder) {
                List<SettingsPageRule> pageRules = pagesRules.get(pageId);
                if (pageRules != null) {
                    for (SettingsPageRule pageRule : pageRules) {
                        if (pageRule instanceof GroupOptionsPageRule) {
                            if (groupId.equals(((GroupOptionsPageRule) pageRule).getGroupId())) {
                                processOptionsPage(optionsPageReceiver, pageId);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (String pageId : pagesOrder) {
            boolean inGroup = false;
            List<SettingsPageRule> pageRules = pagesRules.get(pageId);
            if (pageRules != null) {
                for (SettingsPageRule pageRule : pageRules) {
                    if (pageRule instanceof GroupOptionsPageRule) {
                        inGroup = true;
                        break;
                    }
                }
            }
            if (!inGroup) {
                processOptionsPage(optionsPageReceiver, pageId);
            }
        } */
    }

    /* private void processOptionsPage(SettingsPageReceiver optionsPageReceiver, String pageId) {
        SettingsPage<?> optionsPage = pages.get(pageId);
        List<SettingsPathItem> path = null;
        VisualOptionsPageParams visualParams = null;
        List<OptionsPageRule> rules = pagesRules.get(pageId);
        if (rules != null) {
            for (OptionsPageRule rule : rules) {
                if (rule instanceof GroupOptionsPageRule) {
                    path = getGroupPath(((GroupOptionsPageRule) rule).getGroupId());
                } else if (rule instanceof VisualOptionsPageRule) {
                    visualParams = ((VisualOptionsPageRule) rule).getVisualParams();
                } else if (rule instanceof RelativeOptionsPageRule) {
                    // TODO
                }
            }
        }
        optionsPageReceiver.addOptionsPage(optionsPage, path, visualParams);
    }

    @Nonnull
    public List<SettingsPathItem> getGroupPath(String groupId) {
        List<SettingsPathItem> path = new ArrayList<>();
        while (groupId != null) {
            OptionsGroup group = groups.get(groupId);
            if (group == null) {
                throw new IllegalStateException("Missing group: " + groupId);
            }
            path.add(0, new SettingsPathItem(groupId, group.getName()));
            List<OptionsGroupRule> rules = groupRules.get(groupId);
            groupId = null;
            if (rules != null) {
                for (OptionsGroupRule rule : rules) {
                    if (rule instanceof ParentOptionsGroupRule) {
                        groupId = ((ParentOptionsGroupRule) rule).getParentGroupId();
                        break;
                    }
                }
            }
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    public void initialLoadFromPreferences() {
        // TODO use preferences instead of options for initial apply
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsStorage optionsStorage = optionsModule.getAppOptions();
        for (SettingsPage<? extends SettingsData> optionsPage : pages.values()) {
            SettingsData options = optionsPage.createOptions();
            ((SettingsPage) optionsPage).loadFromPreferences(optionsStorage, options);
        }
    } */

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
    
    private class EmptySettingsComponent extends JComponent implements SettingsComponent<SettingsOptions> {

        @Override
        public void loadFromOptions(SettingsOptions settingsData) {
            // TODO
        }

        @Override
        public void saveToOptions(SettingsOptions settingsData) {
            // TODO
        }

        @Override
        public void setSettingsModifiedListener(SettingsModifiedListener listener) {
            // TODO
        }

        @Override
        public ResourceBundle getResourceBundle() {
            return null;
        }
    }
}
