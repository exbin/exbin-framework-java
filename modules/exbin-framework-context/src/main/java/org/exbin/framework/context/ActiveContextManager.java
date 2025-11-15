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
package org.exbin.framework.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ActiveContextChangeListener;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.context.api.StateChangeType;

/**
 * Default active context manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActiveContextManager implements ActiveContextManagement {

    protected final Map<Class<?>, Object> activeStates = new HashMap<>();
    protected final List<ActiveContextChangeListener> changeListeners = new ArrayList<>();

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
        notifyChanged(stateClass, activeState);
    }

    @Override
    public <T> void notifyStateChange(Class<T> stateClass, T activeState, StateChangeType changeType) {
        activeStates.put(stateClass, activeState);
        notifyStateChanged(stateClass, activeState, changeType);
    }

    @Override
    public <T> void notifyActiveStateChange(Class<T> stateClass, StateChangeType changeType) {
        Object activeState = getActiveState(stateClass);
        if (activeState != null) {
            notifyStateChange(stateClass, stateClass.cast(activeState), changeType);
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

    protected <T> void notifyChanged(Class<T> stateClass, T activeState) {
        for (ActiveContextChangeListener changeListener : changeListeners) {
            changeListener.activeStateChanged(stateClass, activeState);
        }
    }

    protected <T> void notifyStateChanged(Class<T> stateClass, T activeState, StateChangeType changeType) {
        for (ActiveContextChangeListener changeListener : changeListeners) {
            changeListener.notifyStateChange(stateClass, activeState, changeType);
        }
    }
}
