/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.frame;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;

/**
 * Status bar handler.
 *
 * @version 0.2.0 2016/07/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusBarHandler {

    private final XBApplicationFrame frame;

    private final Map<String, JPanel> statusBars = new HashMap<>();

    // Map of status bar to module connections
    private final Map<String, String> statusBarModules = new HashMap<>();

    public StatusBarHandler(XBApplicationFrame frame) {
        this.frame = frame;
    }

    public void registerStatusBar(String moduleId, String statusBarId, JPanel panel) {
        statusBars.put(statusBarId, panel);
        statusBarModules.put(moduleId, statusBarId);
    }

    public void switchStatusBar(String statusBarId) {
        JPanel panel = statusBars.get(statusBarId);
        frame.switchStatusBar(panel);
    }
}
