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
package org.exbin.framework.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsGroupRule;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.api.OptionsPageRule;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.options.api.ParentOptionsGroupRule;
import org.exbin.framework.options.api.RelativeOptionsPageRule;
import org.exbin.framework.options.api.VisualOptionsPageParams;
import org.exbin.framework.options.api.VisualOptionsPageRule;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;

/**
 * Options page manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsPageManager implements OptionsPageManagement {

    private final List<String> groupsOrder = new ArrayList<>();
    private final Map<String, OptionsGroup> groups = new HashMap<>();
    private final Map<String, List<OptionsGroupRule>> groupRules = new HashMap<>();
    private final List<String> pagesOrder = new ArrayList<>();
    private final Map<String, OptionsPage<?>> pages = new HashMap<>();
    private final Map<String, List<OptionsPageRule>> pagesRules = new HashMap<>();

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
    public void registerPage(OptionsPage<?> optionsPage) {
        String pageId = optionsPage.getId();
        pages.put(pageId, optionsPage);
        pagesOrder.add(pageId);
    }

    @Override
    public void registerPageRule(OptionsPage<?> optionsPage, OptionsPageRule optionsPageRule) {
        String pageId = optionsPage.getId();
        registerPageRule(pageId, optionsPageRule);
    }

    @Override
    public void registerPageRule(String pageId, OptionsPageRule pageRule) {
        List<OptionsPageRule> rules = pagesRules.get(pageId);
        if (rules == null) {
            rules = new ArrayList<>();
            pagesRules.put(pageId, rules);
        }
        rules.add(pageRule);
    }

    public void passOptionsPages(OptionsPageReceiver optionsPageReceiver) {
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
                List<OptionsPageRule> pageRules = pagesRules.get(pageId);
                if (pageRules != null) {
                    for (OptionsPageRule pageRule : pageRules) {
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
            List<OptionsPageRule> pageRules = pagesRules.get(pageId);
            if (pageRules != null) {
                for (OptionsPageRule pageRule : pageRules) {
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

    private void processOptionsPage(OptionsPageReceiver optionsPageReceiver, String pageId) {
        OptionsPage<?> optionsPage = pages.get(pageId);
        List<OptionsPathItem> path = null;
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
    public List<OptionsPathItem> getGroupPath(String groupId) {
        List<OptionsPathItem> path = new ArrayList<>();
        while (groupId != null) {
            OptionsGroup group = groups.get(groupId);
            if (group == null) {
                throw new IllegalStateException("Missing group: " + groupId);
            }
            path.add(0, new OptionsPathItem(groupId, group.getName()));
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
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        OptionsStorage preferences = preferencesModule.getAppPreferences();
        for (OptionsPage<? extends OptionsData> optionsPage : pages.values()) {
            OptionsData options = optionsPage.createOptions();
            ((OptionsPage) optionsPage).loadFromPreferences(preferences, options);
        }
    }
}
