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
package org.exbin.framework.ui.theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Enumeration of rendering methods.
 * <p>
 * @see
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html">JavaSE
 * 8 2D Technology</a>
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public enum GuiFontAntialiasing {

    DEFAULT(""),
    OFF("off"),
    GASP("gasp"),
    LCD_AUTO("lcd"),
    LCD_HRGB("lcd_hrgb"),
    LCD_HBGR("lcd_hbgr"),
    LCD_VRGB("lcd_vrgb"),
    LCD_VBGR("lcd_vbgr");

    private final String propertyValue;

    private GuiFontAntialiasing(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Nonnull
    public String getPropertyValue() {
        return propertyValue;
    }

    @Nonnull
    public static Optional<GuiFontAntialiasing> fromPropertyValue(String propertyValue) {
        for (GuiFontAntialiasing method : values()) {
            if (propertyValue.equals(method.getPropertyValue())) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    @Nonnull
    public static List<GuiFontAntialiasing> getAvailable() {
        return new ArrayList<>(Arrays.asList(values()));
    }
}
