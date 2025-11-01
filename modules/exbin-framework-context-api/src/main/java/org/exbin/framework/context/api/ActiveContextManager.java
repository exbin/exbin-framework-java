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
package org.exbin.framework.context.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for context state provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActiveContextManager extends ActiveContextProvider {

    /**
     * Changes active state.
     *
     * @param <T> state type
     * @param stateClass state class
     * @param activeState active state
     */
    <T> void changeActiveState(Class<T> stateClass, @Nullable T activeState);

    /**
     * Adds change listener.
     *
     * @param changeListener change listener
     */
    void addChangeListener(ActiveContextChangeListener changeListener);

    /**
     * Removes change listener.
     *
     * @param changeListener change listener
     */
    void removeChangeListener(ActiveContextChangeListener changeListener);
}
