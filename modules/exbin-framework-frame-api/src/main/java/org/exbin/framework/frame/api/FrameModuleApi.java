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
package org.exbin.framework.frame.api;

import java.awt.Frame;
import java.awt.Image;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework frame module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FrameModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(FrameModuleApi.class);

    public static final String SETTINGS_PAGE_ID = "appearance";
    public static final String DEFAULT_STATUS_BAR_ID = "default";
    public static final String MAIN_STATUS_BAR_ID = "main";
    public static final String PROGRESS_STATUS_BAR_ID = "progress";
    public static final String BUSY_STATUS_BAR_ID = "busy";

    public static final String PREFERENCES_FRAME_RECTANGLE = "frameRectangle";

    /**
     * Returns frame handler.
     *
     * @return frame handler
     */
    @Nonnull
    ApplicationFrameHandler getFrameHandler();

    /**
     * Creates and initializes main menu and toolbar.
     */
    void init();

    /**
     * Returns frame instance.
     *
     * TODO: Support for multiple frames
     *
     * @return frame
     */
    @Nonnull
    Frame getFrame();

    /**
     * Returns exit action.
     *
     * @return exit action
     */
    @Nonnull
    Action getExitAction();

    /**
     * Registers exit action in default menu location.
     */
    void registerExitAction();

    /**
     * Adds exit listener.
     *
     * @param listener listener
     */
    void addExitListener(ApplicationExitListener listener);

    /**
     * Removes exit listener.
     *
     * @param listener listener
     */
    void removeExitListener(ApplicationExitListener listener);

    void registerBarsVisibilityActions();

    void registerToolBarVisibilityActions();

    void registerStatusBarVisibilityActions();

    /**
     * Registers new status bar with unique ID.
     *
     * @param moduleId module id
     * @param statusBarId statusbar id
     * @param panel panel
     */
    void registerStatusBar(String moduleId, String statusBarId, JPanel panel);

    /**
     * Switches to status bar with specific ID.
     *
     * @param statusBarId statusbar id
     */
    void switchStatusBar(String statusBarId);

    void loadFramePosition();

    void saveFramePosition();

    void switchFrameToFullscreen();

    void switchFrameToUndecorated();

    @Nonnull
    Optional<Image> getApplicationIcon();

    /**
     * Registers options panels.
     */
    void registerSettings();
}
