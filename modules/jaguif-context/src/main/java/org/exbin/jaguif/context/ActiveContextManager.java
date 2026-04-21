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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.context.api.ContextChangeListener;

/**
 * Default active context manager.
 */
@ParametersAreNonnullByDefault
public class ActiveContextManager implements ActiveContextManagement {

    protected final Map<Class<?>, Object> activeStates = new HashMap<>();
    protected final List<ContextChangeListener> changeListeners = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getActiveState(Class<T> stateClass) {
        return (T) activeStates.get(stateClass);
    }

    @Nonnull
    @Override
    public Collection<Class<?>> getStateClasses() {
        List<Class<?>> stateClasses = new ArrayList<>();
        stateClasses.addAll(activeStates.keySet());
        return stateClasses;
    }

    @Override
    public <T> void changeActiveState(Class<T> stateClass, @Nullable T activeState) {
        activeStates.put(stateClass, activeState);
        notifyStateChanged(stateClass, activeState);
    }

    @Override
    public <T> void updateActiveState(Class<T> stateClass, T activeState, StateUpdateType updateType) {
        Object currentState = getActiveState(stateClass);
        if (currentState == activeState) {
            ActiveContextManager.this.notifyStateUpdated(stateClass, activeState, updateType);
        }
    }

    @Override
    public void addChangeListener(ContextChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    @Override
    public void removeChangeListener(ContextChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    protected <T> void notifyStateChanged(Class<T> stateClass, T activeState) {
        for (ContextChangeListener changeListener : changeListeners) {
            changeListener.notifyStateChanged(stateClass, activeState);
        }
    }

    protected <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType updateType) {
        for (ContextChangeListener changeListener : changeListeners) {
            changeListener.notifyStateUpdated(stateClass, activeState, updateType);
        }
    }
}
