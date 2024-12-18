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
package org.exbin.framework.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuContributionRule;

/**
 * Menu definition.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuDefinition {

    private final String pluginId;
    private List<MenuContribution> contributions = new ArrayList<>();
    private Map<MenuContribution, List<MenuContributionRule>> rules = new HashMap<>();

    public MenuDefinition(String pluginId) {
        this.pluginId = pluginId;
    }

    @Nonnull
    public List<MenuContribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<MenuContribution> contributions) {
        this.contributions = contributions;
    }

    @Nonnull
    public Map<MenuContribution, List<MenuContributionRule>> getRules() {
        return rules;
    }

    public void setRules(Map<MenuContribution, List<MenuContributionRule>> rules) {
        this.rules = rules;
    }
}
