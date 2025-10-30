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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ApplicationContextChangeListener;
import org.exbin.framework.context.api.ApplicationContextManager;

/**
 * Child application context manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ChildApplicationContextManager implements ApplicationContextManager {

    protected final ApplicationContextManager parentContextManager;
    protected final Map<Class<?>, Object> activeStates = new HashMap<>();
    protected final Set<Class<?>> childStates = new HashSet<>();
    protected final List<ApplicationContextChangeListener> changeListeners = new ArrayList<>();

    public ChildApplicationContextManager(ApplicationContextManager parentContextManager) {
        this.parentContextManager = parentContextManager;
        parentContextManager.addChangeListener(new ApplicationContextChangeListener() {
            @Override
            public <T> void activeStateChanged(Class<T> stateClass, T activeState) {
                if (childStates.contains(stateClass)) {
                    return;
                }

                notifyChanged(stateClass, activeState);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getActiveState(Class<T> stateClass) {
        if (childStates.contains(stateClass)) {
            return (T) activeStates.get(stateClass);
        }

        return parentContextManager.getActiveState(stateClass);
    }

    @Override
    public <T> void changeActiveState(Class<T> stateClass, T activeState) {
        if (!childStates.contains(stateClass)) {
            childStates.add(stateClass);
        }

        activeStates.put(stateClass, activeState);
        notifyChanged(stateClass, activeState);
    }

    @Override
    public void addChangeListener(ApplicationContextChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    @Override
    public void removeChangeListener(ApplicationContextChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    protected <T> void notifyChanged(Class<T> stateClass, T activeState) {
        for (ApplicationContextChangeListener changeListener : changeListeners) {
            changeListener.activeStateChanged(stateClass, activeState);
        }
    }
}
