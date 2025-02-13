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
package org.exbin.framework.ui.theme.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Look&feel provider with options handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface LafOptionsHandler {

    /**
     * Returns options component.
     *
     * @return component
     */
    @Nonnull
    JComponent createOptionsComponent();

    /**
     * Loads options from preferences.
     *
     * @param preferences preferences
     */
    void loadFromPreferences(OptionsStorage preferences);

    /**
     * Saves options to preferences.
     *
     * @param preferences preferences
     */
    void saveToPreferences(OptionsStorage preferences);
}
