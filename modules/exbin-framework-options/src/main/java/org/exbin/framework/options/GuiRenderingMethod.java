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
package org.exbin.framework.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Enumeration of rendering methods.
 *
 * @see
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html">JavaSE
 * 8 2D Technology</a>
 * @see Wayland + Vulkan:
 * <a href="https://openjdk.org/projects/wakefield/">Project WakeField</a>
 * @see MacOS Metal framework:
 * <a href="https://openjdk.org/projects/lanai/">Project Lanai</a>
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public enum GuiRenderingMethod {

    DEFAULT(""),
    DIRECT_DRAW("directdraw"),
    DDRAW_HWSCALE("hw_scale"),
    SOFTWARE("software"),
    OPEN_GL("opengl"),
    XRENDER("xrender"),
    METAL("metal");

    private final String propertyValue;

    private GuiRenderingMethod(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Nonnull
    public String getPropertyValue() {
        return propertyValue;
    }

    @Nonnull
    public static Optional<GuiRenderingMethod> fromPropertyValue(String propertyValue) {
        for (GuiRenderingMethod method : values()) {
            if (propertyValue.equals(method.getPropertyValue())) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    @Nonnull
    public static List<GuiRenderingMethod> getAvailableMethods() {
        List<GuiRenderingMethod> methods = new ArrayList<>();

        DesktopUtils.DesktopOs desktopOs = DesktopUtils.detectBasicOs();
        methods.add(DEFAULT);
        methods.add(SOFTWARE);
        switch (desktopOs) {
            case WINDOWS:
                methods.add(DIRECT_DRAW);
                methods.add(DDRAW_HWSCALE);
                methods.add(OPEN_GL);
                break;
            case MAC_OS:
                methods.add(OPEN_GL);
                methods.add(METAL);
                break;
            default:
                methods.add(OPEN_GL);
                methods.add(XRENDER);
                break;
        }

        return methods;
    }
}
