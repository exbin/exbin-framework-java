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
package org.exbin.framework.statusbar;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.statusbar.api.StatusBarDefinitionManagement;
import org.exbin.framework.statusbar.api.StatusBarManagement;
import org.exbin.framework.statusbar.api.StatusBarModuleApi;

/**
 * Support for status bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusBarModule implements StatusBarModuleApi {

    @Override
    public StatusBarDefinitionManagement getMainStatusBarManager(String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StatusBarDefinitionManagement getStatusBarManager(String statusBarId, String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerStatusBar(String statusBarId, String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StatusBarManagement createStatusBarManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildStatusBar(JToolBar targetStatusBar, String statusBarId, ActionContextRegistration actionContextRegistration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Action> getStatusBarManagedActions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerFrameStatusBar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
