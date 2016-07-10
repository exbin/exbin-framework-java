/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.frame;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Status bar handler.
 *
 * @version 0.2.0 2016/07/10
 * @author ExBin Project (http://exbin.org)
 */
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
