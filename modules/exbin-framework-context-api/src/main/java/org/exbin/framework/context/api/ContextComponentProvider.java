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

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for content aware component provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ContextComponentProvider {

    /**
     * Returns component.
     *
     * @return component
     */
    @Nonnull
    Component getComponent();

    /**
     * Notifies activation of the component.
     *
     * @param contextManager context manager
     */
    void notifyActivated(ActiveContextManagement contextManager);

    /**
     * Notifies deactivation of the component.
     *
     * @param contextManager context manager
     */
    void notifyDeactivated(ActiveContextManagement contextManager);
}
