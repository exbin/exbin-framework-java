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
package org.exbin.framework.addon.manager;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsPanel;

/**
 * Addons manager tab.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsManagerTab implements AddonManagerTab {

    private AddonsPanel addonsPanel = new AddonsPanel();
    private Set<String> toInstall = new HashSet<>();

    public AddonsManagerTab() {
        init();
    }
    
    private void init() {
        
    }

    @Nonnull
    @Override
    public String getTitle() {
        return addonsPanel.getResourceBundle().getString("addonsTab.title");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        return addonsPanel;
    }
    
    public void setController(AddonsPanel.Controller controller) {
        addonsPanel.setController(controller);
    }

    @Nonnull
    public Set<String> getToInstall() {
        return toInstall;
    }

    public int getToInstallCount() {
        return toInstall.size();
    }
}
