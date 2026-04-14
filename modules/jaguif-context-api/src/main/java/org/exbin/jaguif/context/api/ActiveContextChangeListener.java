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
package org.exbin.jaguif.context.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for context state change listener.
 */
@ParametersAreNonnullByDefault
public interface ActiveContextChangeListener {

    /**
     * Sends active state changed notification.
     *
     * @param <T> state type
     * @param stateClass state class
     * @param activeState active state
     */
    <T> void notifyStateChanged(Class<T> stateClass, @Nullable T activeState);

    /**
     * Sends active state specific update notification.
     *
     * @param <T> state type
     * @param stateClass state class
     * @param activeState active state
     * @param updateType update type
     */
    <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType updateType);
}
