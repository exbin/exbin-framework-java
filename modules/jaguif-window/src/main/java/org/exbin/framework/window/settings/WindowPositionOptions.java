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
package org.exbin.framework.window.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.utils.WindowPosition;

/**
 * Window position options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowPositionOptions implements SettingsOptions {

    public static final String KEY_SCREEN_INDEX = "screenIndex";
    public static final String KEY_SCREEN_WIDTH = "screenWidth";
    public static final String KEY_SCREEN_HEIGHT = "screenHeight";
    public static final String KEY_POSITION_X = "positionX";
    public static final String KEY_POSITION_Y = "positionY";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_MAXIMIZED = "maximized";

    protected final OptionsStorage storage;

    public WindowPositionOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public void setWindowPosition(WindowPosition windowPosition) {
        storage.putInt(KEY_SCREEN_INDEX, windowPosition.getScreenIndex());
        storage.putInt(KEY_SCREEN_WIDTH, windowPosition.getScreenWidth());
        storage.putInt(KEY_SCREEN_HEIGHT, windowPosition.getScreenHeight());
        storage.putInt(KEY_POSITION_X, windowPosition.getRelativeX());
        storage.putInt(KEY_POSITION_Y, windowPosition.getRelativeY());
        storage.putInt(KEY_WIDTH, windowPosition.getWidth());
        storage.putInt(KEY_HEIGHT, windowPosition.getHeight());
        storage.putBoolean(KEY_MAXIMIZED, windowPosition.isMaximized());
    }

    @Nonnull
    public WindowPosition getWindowPosition() {
        WindowPosition windowPosition = new WindowPosition();
        getWindowPosition(windowPosition);
        return windowPosition;
    }

    public void getWindowPosition(WindowPosition windowPosition) {
        windowPosition.setScreenIndex(storage.getInt(KEY_SCREEN_INDEX, 0));
        windowPosition.setScreenWidth(storage.getInt(KEY_SCREEN_WIDTH, 0));
        windowPosition.setScreenHeight(storage.getInt(KEY_SCREEN_HEIGHT, 0));
        windowPosition.setRelativeX(storage.getInt(KEY_POSITION_X, 0));
        windowPosition.setRelativeY(storage.getInt(KEY_POSITION_Y, 0));
        windowPosition.setWidth(storage.getInt(KEY_WIDTH, 0));
        windowPosition.setHeight(storage.getInt(KEY_HEIGHT, 0));
        windowPosition.setMaximized(storage.getBoolean(KEY_MAXIMIZED, false));
    }

    public boolean preferencesFramePositionExists() {
        return storage.exists(KEY_SCREEN_INDEX);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        WindowPositionOptions with = (WindowPositionOptions) options;
        with.setWindowPosition(getWindowPosition());
    }
}
