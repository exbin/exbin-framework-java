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
package org.exbin.framework.action.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for action context change registration.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionContextChangeManager {

    /**
     * Registers listener to call once when menu is created for component or
     * empty when no such component is active.
     *
     * @param <T> monitored class type
     * @param componentClass component class
     * @param listener listener
     */
    <T> void registerListener(Class<T> componentClass, ActionContextChangeListener<T> listener);

    /**
     * Registers listener to call each time when component is activated or empty
     * when deactivated.
     *
     * @param <T> monitored class type
     * @param componentClass component class
     * @param listener listener
     */
    <T> void registerUpdateListener(Class<T> componentClass, ActionContextChangeListener<T> listener);

    /**
     * Requests update for component class.
     *
     * @param <T> monitored class type
     * @param componentClass component class
     * @param componentInstance component instance
     */
    <T> void updateActionsForComponent(Class<T> componentClass, @Nullable T componentInstance);
}
