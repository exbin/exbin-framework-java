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
package org.exbin.framework.window.api;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper for window with extra controls.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface WindowHandler {

    /**
     * Shows window.
     */
    void show();

    /**
     * Shows window with position related to given component.
     *
     * @param component component
     */
    void showCentered(@Nullable Component component);

    /**
     * Closes window.
     */
    void close();

    /**
     * Disposes of the window.
     */
    void dispose();

    /**
     * Returns window instance.
     *
     * @return window instance
     */
    @Nonnull
    Window getWindow();

    /**
     * Returns parent container.
     *
     * @return parent container
     */
    @Nonnull
    Container getParent();

    /**
     * Centers window position relative to component.
     *
     * @param component component
     */
    void center(@Nullable Component component);

    /**
     * Centers window position relative to screen.
     */
    void center();
}
