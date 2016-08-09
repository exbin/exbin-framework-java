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
package org.exbin.framework.gui.menu.api;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Interface for popup event trigger dispatcher.
 *
 * @version 0.2.0 2016/08/09
 * @author ExBin Project (http://exbin.org)
 */
public interface ComponentPopupEventDispatcher {

    /**
     * Processes event for popup trigger actions.
     *
     * @param mouseEvent mouse event
     * @return true if event was processed
     */
    boolean dispatchMouseEvent(MouseEvent mouseEvent);

    /**
     * Processes event for popup trigger actions.
     *
     * @param keyEvent key event
     * @return true if event was processed
     */
    boolean dispatchKeyEvent(KeyEvent keyEvent);
}
