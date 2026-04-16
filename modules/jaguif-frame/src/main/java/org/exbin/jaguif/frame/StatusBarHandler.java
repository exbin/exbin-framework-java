/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.frame;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;

/**
 * Status bar handler.
 */
@ParametersAreNonnullByDefault
public class StatusBarHandler {

    private final ApplicationFrame frame;

    private final Map<String, JComponent> statusBars = new HashMap<>();

    // Map of status bar to module connections
    private final Map<String, String> statusBarModules = new HashMap<>();

    public StatusBarHandler(ApplicationFrame frame) {
        this.frame = frame;
    }

    public void registerStatusBar(String moduleId, String statusBarId, JComponent component) {
        statusBars.put(statusBarId, component);
        statusBarModules.put(moduleId, statusBarId);
    }

    public void switchStatusBar(String statusBarId) {
        JComponent component = statusBars.get(statusBarId);
        frame.switchStatusBar(component);
    }
}
