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
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;

/**
 * Options page manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsPageManager implements OptionsPageManagement {

    private final Map<String, OptionsGroup> optionsGroups = new HashMap<>();
    private final Map<String, List<OptionsGroupRule>> optionsGroupRules = new HashMap<>();
    private final Map<String, OptionsPage<?>> optionsPages = new HashMap<>();
    private final Map<String, List<OptionsPageRule>> optionsPagesRules = new HashMap<>();

    @Override
    public void registerGroup(OptionsGroup optionsGroup) {
        String groupId = optionsGroup.getGroupId();
        optionsGroups.put(groupId, optionsGroup);
    }

    @Override
    public void registerGroup(String groupId, String groupName) {
        optionsGroups.put(groupId, new BasicOptionsGroup(groupId, groupName));
    }

    @Override
    public void registerGroupRule(OptionsGroup optionsGroup, OptionsGroupRule groupRule) {
        String groupId = optionsGroup.getGroupId();
        registerGroupRule(groupId, groupRule);
    }

    @Override
    public void registerGroupRule(String groupId, OptionsGroupRule groupRule) {
        List<OptionsGroupRule> rules = optionsGroupRules.get(groupId);
        if (rules == null) {
            rules = new ArrayList<>();
            optionsGroupRules.put(groupId, rules);
        }
        rules.add(groupRule);
    }

    @Override
    public void registerPage(OptionsPage<?> optionsPage) {
        String pageId = optionsPage.getId();
        optionsPages.put(pageId, optionsPage);
    }

    @Override
    public void registerPageRule(OptionsPage<?> optionsPage, OptionsPageRule optionsPageRule) {
        String pageId = optionsPage.getId();
        registerPageRule(pageId, optionsPageRule);
    }

    @Override
    public void registerPageRule(String pageId, OptionsPageRule pageRule) {
        List<OptionsPageRule> rules = optionsPagesRules.get(pageId);
        if (rules == null) {
            rules = new ArrayList<>();
            optionsPagesRules.put(pageId, rules);
        }
        rules.add(pageRule);
    }

    public void passOptionsPages(OptionsPageReceiver optionsPageReceiver) {
        for (OptionsPage<?> optionsPage : optionsPages.values()) {
            List<OptionsPathItem> path = null;
            List<OptionsPageRule> rules = optionsPagesRules.get(optionsPage.getId());
            if (rules != null) {
                for (OptionsPageRule rule : rules) {
                    if (rule instanceof GroupOptionsPageRule) {
                        path = getGroupPath(((GroupOptionsPageRule) rule).getGroupId());
                        break;
                    }
                }
            }
            optionsPageReceiver.addOptionsPage(optionsPage, path);
        }
    }

    @Nonnull
    public List<OptionsPathItem> getGroupPath(String groupId) {
        List<OptionsPathItem> path = new ArrayList<>();
        while (groupId != null) {
            OptionsGroup group = optionsGroups.get(groupId);
            if (group == null) {
                throw new IllegalStateException("Missing group: " + groupId);
            }
            path.add(0, new OptionsPathItem(groupId, group.getName()));
            List<OptionsGroupRule> rules = optionsGroupRules.get(groupId);
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
        for (OptionsPage<? extends OptionsData> optionsPage : optionsPages.values()) {
            OptionsData options = optionsPage.createOptions();
            ((OptionsPage) optionsPage).loadFromPreferences(preferences, options);
        }
    }
}
