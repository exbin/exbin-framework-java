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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.StateChangeType;

/**
 * Listener for action context state change.
 *
 * @author ExBin Project (https://exbin.org)
 * @param <T> instance type
 */
@ParametersAreNonnullByDefault
public interface ActionContextStateChangeListener<T> {

    /**
     * Notifies active state changed.
     *
     * @param instance class instance
     * @param changeType change type
     */
    void notifyStateChange(T instance, StateChangeType changeType);
}
