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
package org.exbin.framework.deltahex.panel;

import java.awt.Color;
import java.util.Map;

/**
 * Hexadecimal editor color panel interface.
 *
 * @version 0.1.0 2016/06/23
 * @author ExBin Project (http://exbin.org)
 */
public interface HexColorPanelApi {

    /**
     * Returns current colors used in application frame.
     *
     * @return map of colors
     */
    public Map<HexColorType, Color> getCurrentTextColors();

    /**
     * Returns default colors used in application frame.
     *
     * @return map of colors
     */
    public Map<HexColorType, Color> getDefaultTextColors();

    /**
     * Sets current colors used in application frame.
     *
     * @param colors map of colors
     */
    public void setCurrentTextColors(Map<HexColorType, Color> colors);
}
