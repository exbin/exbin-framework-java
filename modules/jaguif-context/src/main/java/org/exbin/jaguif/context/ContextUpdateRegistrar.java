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
package org.exbin.jaguif.context;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextStateChangeListener;
import org.exbin.jaguif.context.api.ContextUpdateManagement;

/**
 * Context registration.
 */
@ParametersAreNonnullByDefault
public class ContextUpdateRegistrar implements ContextRegistration {

    protected final String recordId;
    protected final ContextUpdateManagement updateManagement;
    protected final ActiveContextManagement contextManagement;

    public ContextUpdateRegistrar(String recordId, ContextUpdateManagement updateManagement, ActiveContextManagement contextManagement) {
        this.recordId = recordId;
        this.updateManagement = updateManagement;
        this.contextManagement = contextManagement;
    }

    @Override
    public void registerContextChange(ContextChange contextChange) {
        updateManagement.addContextItem(recordId, contextChange);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void finish() {
        for (Class<?> stateClass : contextManagement.getStateClasses()) {
            Object instance = contextManagement.getActiveState(stateClass);
            List<ContextStateChangeListener<?>> changeListeners = updateManagement.getChangeListeners(recordId, stateClass);
            for (ContextStateChangeListener changeListener : changeListeners) {
                changeListener.stateChanged(instance);
            }
        }
    }
}
