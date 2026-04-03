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
package org.exbin.jaguif.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ActiveContextChangeListener;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.StateUpdateType;

/**
 * Child active context manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ChildActiveContextManager implements ActiveContextManagement {

    protected final ActiveContextManagement parentContextManager;
    protected final Map<Class<?>, Object> activeStates = new HashMap<>();
    protected final Set<Class<?>> childStates = new HashSet<>();
    protected final List<ActiveContextChangeListener> changeListeners = new ArrayList<>();

    public ChildActiveContextManager(ActiveContextManagement parentContextManager) {
        this.parentContextManager = parentContextManager;
        parentContextManager.addChangeListener(new ActiveContextChangeListener() {
            @Override
            public <T> void notifyStateChanged(@Nonnull Class<T> stateClass, @Nullable T activeState) {
                if (childStates.contains(stateClass)) {
                    return;
                }

                notifyStateChanged(stateClass, activeState);
            }

            @Override
            public <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType updateType) {
                if (childStates.contains(stateClass)) {
                    return;
                }

                notifyStateUpdated(stateClass, activeState, updateType);
            }
        });
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getActiveState(Class<T> stateClass) {
        if (childStates.contains(stateClass)) {
            return (T) activeStates.get(stateClass);
        }

        return parentContextManager.getActiveState(stateClass);
    }

    @Nonnull
    @Override
    public Collection<Class<?>> getStateClasses() {
        List<Class<?>> stateClasses = new ArrayList<>();
        stateClasses.addAll(activeStates.keySet());
        stateClasses.addAll(parentContextManager.getStateClasses());
        return stateClasses;
    }

    @Override
    public <T> void changeActiveState(Class<T> stateClass, @Nullable T activeState) {
        if (!childStates.contains(stateClass)) {
            childStates.add(stateClass);
        }

        activeStates.put(stateClass, activeState);
        notifyStateChanged(stateClass, activeState);
    }

    @Override
    public <T> void updateActiveState(Class<T> stateClass, T activeState, StateUpdateType updateType) {
        if (!childStates.contains(stateClass)) {
            childStates.add(stateClass);
        }

        notifyStateUpdated(stateClass, activeState, updateType);
    }

    @Override
    public <T> void updateActiveState(Class<T> stateClass, StateUpdateType updateType) {
        Object activeState = getActiveState(stateClass);
        if (activeState != null) {
            updateActiveState(stateClass, stateClass.cast(activeState), updateType);
        }
    }

    @Override
    public void addChangeListener(ActiveContextChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    @Override
    public void removeChangeListener(ActiveContextChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    protected <T> void notifyStateChanged(Class<T> stateClass, T activeState) {
        for (ActiveContextChangeListener changeListener : changeListeners) {
            changeListener.notifyStateChanged(stateClass, activeState);
        }
    }

    protected <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType updateType) {
        for (ActiveContextChangeListener changeListener : changeListeners) {
            changeListener.notifyStateUpdated(stateClass, activeState, updateType);
        }
    }
}
