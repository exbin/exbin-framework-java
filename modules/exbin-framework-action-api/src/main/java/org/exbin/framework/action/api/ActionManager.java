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
 * Action manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionManager extends ActionContextService, ComponentActivationListener {

    /**
     * Registers given action.
     *
     * @param action action
     */
    void registerAction(Action action);

    /**
     * Initializes given action.
     *
     * @param action action
     */
    void initAction(Action action);
}
