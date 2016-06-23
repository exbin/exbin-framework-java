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
