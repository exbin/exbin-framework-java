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
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * Options settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsSetingsManager extends ContributionManager implements OptionsSettingsManagement {

    public static final String MAIN_OPTIONS_ID = "main";
    
    protected ContributionDefinition mainDefinition;

    public OptionsSetingsManager() {
        mainDefinition = new ContributionDefinition(OptionsSettingsModule.MODULE_ID);
        definitions.put(MAIN_OPTIONS_ID, mainDefinition);
    }

    @Nonnull
    @Override
    public SettingsComponentContribution registerComponent(SettingsComponentProvider<?> componentProvider) {
        SettingsComponentContribution contribution = new SettingsComponentContribution(componentProvider.toString(), componentProvider);
        mainDefinition.getContributions().add(contribution);
        return contribution;
    }

    @Nonnull
    @Override
    public SettingsPageContribution registerPage(String pageId) {
        SettingsPageContribution contribution = new SettingsPageContribution(pageId);
        mainDefinition.getContributions().add(contribution);
        return contribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerGroup(String groupId) {
        GroupSequenceContribution contribution = new GroupSequenceContribution(groupId);
        mainDefinition.getContributions().add(contribution);
        return contribution;
    }

    @Override
    public boolean menuGroupExists(String groupId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerSettingsRule(SequenceContribution contribution, SequenceContributionRule rule) {
        List<SequenceContributionRule> rules = mainDefinition.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            mainDefinition.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

//    private final List<String> groupsOrder = new ArrayList<>();
//    private final Map<String, OptionsGroup> groups = new HashMap<>();
//    private final Map<String, List<OptionsGroupRule>> groupRules = new HashMap<>();
//    private final List<String> pagesOrder = new ArrayList<>();
//    private final Map<String, SettingsPage<?>> pages = new HashMap<>();
//    private final Map<String, List<SettingsPageRule>> pagesRules = new HashMap<>();

    /*
    @Override
    public void registerGroup(OptionsGroup optionsGroup) {
        String groupId = optionsGroup.getGroupId();
        groups.put(groupId, optionsGroup);
        groupsOrder.add(groupId);
    }

    @Override
    public void registerGroup(String groupId, String groupName) {
        registerGroup(new BasicOptionsGroup(groupId, groupName));
    }

    @Override
    public void registerGroupRule(OptionsGroup optionsGroup, OptionsGroupRule groupRule) {
        String groupId = optionsGroup.getGroupId();
        registerGroupRule(groupId, groupRule);
    }

    @Override
    public void registerGroupRule(String groupId, OptionsGroupRule groupRule) {
        List<OptionsGroupRule> rules = groupRules.get(groupId);
        if (rules == null) {
            rules = new ArrayList<>();
            groupRules.put(groupId, rules);
        }
        rules.add(groupRule);
    }

    @Override
    public void registerPage(SettingsPage<?> settingsPage) {
        String pageId = settingsPage.getId();
        pages.put(pageId, settingsPage);
        pagesOrder.add(pageId);
    }

    @Override
    public void registerPageRule(SettingsPage<?> optionsPage, SettingsPageRule settingsPageRule) {
        String pageId = optionsPage.getId();
        registerPageRule(pageId, settingsPageRule);
    }

    @Override
    public void registerPageRule(String pageId, SettingsPageRule pageRule) {
        List<SettingsPageRule> rules = pagesRules.get(pageId);
        if (rules == null) {
            rules = new ArrayList<>();
            pagesRules.put(pageId, rules);
        }
        rules.add(pageRule);
    }

    public void passOptionsPages(SettingsPageReceiver optionsPageReceiver) {
        List<String> processedGroupOrder = new ArrayList<>();
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
        }
    }

    private void processOptionsPage(SettingsPageReceiver optionsPageReceiver, String pageId) {
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
}
