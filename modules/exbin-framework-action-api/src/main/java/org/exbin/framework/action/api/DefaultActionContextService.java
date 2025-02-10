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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Service for action update.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultActionContextService implements ActionContextService, ComponentActivationListener {

    protected final List<ComponentActivationListener> listeners = new ArrayList<>();
    protected final Map<Class<?>, Object> activeComponentState = new HashMap<>();

    @Override
    public void registerListener(ComponentActivationListener listener) {
        listeners.add(listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestUpdate() {
        for (Map.Entry<Class<?>, Object> entry : activeComponentState.entrySet()) {
            Class key = entry.getKey();
            Object instance = entry.getValue();
            for (ComponentActivationListener listener : listeners) {
                listener.updated(key, instance);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestUpdate(Action action) {
        Object value = action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);
        Map<Class<?>, ActionContextChangeListener<?>> actionListeners = new HashMap<>();
        if (value instanceof ActionContextChange) {
            ((ActionContextChange) value).register(new ActionContextChangeManager() {
                @Override
                public <T> void registerListener(Class<T> componentClass, ActionContextChangeListener<T> listener) {
                    actionListeners.put(componentClass, listener);
                }

                @Override
                public <T> void registerUpdateListener(Class<T> componentClass, ActionContextChangeListener<T> listener) {
                    registerListener(componentClass, listener);
                }

                @Override
                public <T> void updateActionsForComponent(Class<T> componentClass, T componentInstance) {
                    updated(componentClass, componentInstance);
                }
            });
        }

        for (Map.Entry<Class<?>, Object> entry : activeComponentState.entrySet()) {
            Class key = entry.getKey();
            Object instance = entry.getValue();
            ActionContextChangeListener listener = actionListeners.get(key);
            if (listener != null) {
                listener.update(instance);
            }
        }
    }

    @Override
    public <T> void updated(Class<T> instanceClass, @Nullable T instance) {
        activeComponentState.put(instanceClass, instance);
        for (ComponentActivationListener listener : listeners) {
            listener.updated(instanceClass, instance);
        }
    }

    public void clearState() {
        activeComponentState.clear();
    }
}
