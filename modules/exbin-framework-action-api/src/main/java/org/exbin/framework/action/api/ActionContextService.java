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
import javax.swing.Action;

/**
 * Service for action context handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionContextService {

    /**
     * Registers component activation listener.
     *
     * @param listener listener
     */
    void registerListener(ComponentActivationListener listener);

    /**
     * Requests update of registered listeners.
     */
    void requestUpdate();

    /**
     * Requests update of registered listeners.
     *
     * @param action action
     */
    void requestUpdate(Action action);
}
