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
package org.exbin.framework.popup;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for link handler for visual component / context menu.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface PositionLinkActionsHandler {

    /**
     * Performs copy link on given relative position to clipboard operation.
     *
     * @param x component x position
     * @param y component y position
     */
    void performCopyLink(int x, int y);

    /**
     * Opens link on given relative position using default browser.
     *
     * @param x component x position
     * @param y component y position
     */
    void performOpenLink(int x, int y);

    /**
     * Returns if true if link is selected on given relative position.
     *
     * @param x component x position
     * @param y component y position
     * @return true if link is selected
     */
    boolean isLinkSelected(int x, int y);
}