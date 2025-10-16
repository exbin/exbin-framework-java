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
package org.exbin.framework.options.settings.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsComponent;

/**
 * Options settings page record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsPageX {

    private final String pageId;
    private List<SettingsComponent<?>> components = new ArrayList<>();

    public SettingsPageX(String pageId) {
        this.pageId = pageId;
    }

    @Nonnull
    public String getPageId() {
        return pageId;
    }

    @Nonnull
    public List<SettingsComponent<?>> getComponents() {
        return components;
    }

    public void addComponent(SettingsComponent<?> component) {
        components.add(component);
    }
}
