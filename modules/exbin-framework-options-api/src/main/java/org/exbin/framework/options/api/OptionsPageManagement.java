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
package org.exbin.framework.options.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for management of options pages.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsPageManagement {

    /**
     * Registers options group.
     *
     * @param optionsGroup options group
     */
    void registerGroup(OptionsGroup optionsGroup);

    /**
     * Registers options group.
     *
     * @param groupId group id
     * @param groupName group name
     */
    void registerGroup(String groupId, String groupName);

    /**
     * Registers options group rule.
     *
     * @param optionsGroup options group
     * @param groupRule options group rule
     */
    void registerGroupRule(OptionsGroup optionsGroup, OptionsGroupRule groupRule);

    /**
     * Registers options group rule.
     *
     * @param groupId options group id
     * @param groupRule options group rule
     */
    void registerGroupRule(String groupId, OptionsGroupRule groupRule);

    /**
     * Registers options panel to default path and name.
     *
     * @param page options page
     */
    void registerPage(OptionsPage<?> page);

    /**
     * Registers options page rule.
     *
     * @param page options page
     * @param pageRule options page rule
     */
    void registerPageRule(OptionsPage<?> page, OptionsPageRule pageRule);

    /**
     * Registers options page rule.
     *
     * @param pageId options page id
     * @param pageRule options page rule
     */
    void registerPageRule(String pageId, OptionsPageRule pageRule);
}
