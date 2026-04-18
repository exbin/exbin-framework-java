/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.context.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Context update management.
 */
@ParametersAreNonnullByDefault
public interface ContextUpdateManagement {

    /**
     * Adds group with specific identifier.
     *
     * @param recordId record identifier
     */
    void addRecord(String recordId);

    /**
     * Removes group with specific identifier.
     *
     * @param recordId record identifier
     */
    void removeRecord(String recordId);

    /**
     * Adds context change monitoring item.
     *
     * @param recordId record identifier
     * @param contextChange context change monitor
     */
    void addContextItem(String recordId, ContextChange contextChange);

    /**
     * Notifies all items about context change.
     *
     * @param <T> monitored class type
     * @param contextClass context class
     * @param contextInstance instance
     */
    <T> void notifyStateChanged(Class<T> contextClass, @Nullable T contextInstance);

    /**
     * Notifies all items about context update.
     *
     * @param <T> monitored class type
     * @param contextClass context class
     * @param contextInstance instance
     * @param updateType update type
     */
    <T> void notifyStateUpdated(Class<T> contextClass, T contextInstance, StateUpdateType updateType);

    /**
     * Returns context change listeners.
     *
     * @param <T> monitored class type
     * @param recordId record identifier
     * @param contextClass context class
     * @return context change listeners
     */
    @Nonnull
    <T> List<ContextStateChangeListener<?>> getChangeListeners(String recordId, Class<T> contextClass);

    /**
     * Returns context update listeners.
     *
     * @param <T> monitored class type
     * @param recordId record identifier
     * @param contextClass context class
     * @return context update listeners
     */
    @Nonnull
    <T> List<ContextStateUpdateListener<?>> getUpdateListeners(String recordId, Class<T> contextClass);
}
