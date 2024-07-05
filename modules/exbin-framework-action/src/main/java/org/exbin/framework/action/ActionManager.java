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
package org.exbin.framework.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ComponentActivationInstanceListener;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.action.api.ComponentActivationService;

/**
 * Action manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManager implements ComponentActivationService, ComponentActivationListener {

    protected final Map<String, ActionRecord> actions = new HashMap<>();
    protected final List<ComponentActivationListener> listeners = new ArrayList<>();
    protected final Map<Class<?>, List<ComponentActivationInstanceListener<?>>> activeComponentListeners = new HashMap<>();
    protected final Map<Class<?>, Object> activeComponentState = new HashMap<>();

    public ActionManager() {
    }

    @Override
    public void registerListener(ComponentActivationListener listener) {
        listeners.add(listener);
    }

    public void registerAction(Action action) {
        String actionId = (String) action.getValue(ActionConsts.ACTION_ID);
        ActionRecord actionRecord = actions.get(actionId);
        if (actionRecord == null) {
            actionRecord = new ActionRecord();
            actions.put(actionId, actionRecord);
        }
        actionRecord.actionInstances.add(action);
    }

    @SuppressWarnings("unchecked")
    public void initAction(Action action) {
        ActionActiveComponent actionActiveComponent = (ActionActiveComponent) action.getValue(ActionConsts.ACTION_ACTIVE_COMPONENT);
        if (actionActiveComponent != null) {
            String actionId = (String) action.getValue(ActionConsts.ACTION_ID);
            ActionRecord actionRecord = actions.get(actionId);
            actionActiveComponent.register(new ActivationManager(actionRecord));
            for (Map.Entry<Class<?>, ComponentActivationInstanceListener<?>> entry : actionRecord.updateComponents.entrySet()) {
                Class<?> updateComponent = entry.getKey();
                Object updateValue = activeComponentState.get(updateComponent);
                ComponentActivationInstanceListener listener = entry.getValue();
                listener.update(updateValue);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void requestUpdate() {
        for (Map.Entry<Class<?>, Object> entry : activeComponentState.entrySet()) {
            Class key = entry.getKey();
            Object instance = entry.getValue();
            for (ComponentActivationListener listener : listeners) {
                listener.updated(key, instance);
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

    @SuppressWarnings("unchecked")
    public <T> void updateActionsForComponent(Class<T> componentClass, @Nullable T componentInstance) {
        activeComponentState.put(componentClass, componentInstance);
        List<ComponentActivationInstanceListener<?>> componentListeners = activeComponentListeners.get(componentClass);
        if (componentListeners != null) {
            for (ComponentActivationInstanceListener componentListener : componentListeners) {
                componentListener.update(componentInstance);
            }
        }
    }

    public void clearState() {
        activeComponentState.clear();
    }

    private class ActivationManager implements ComponentActivationManager {

        private final ActionRecord actionRecord;

        public ActivationManager(ActionRecord actionRecord) {
            this.actionRecord = actionRecord;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> void updateActionsForComponent(Class<T> componentClass, @Nullable T componentInstance) {
            activeComponentState.put(componentClass, componentInstance);
            List<ComponentActivationInstanceListener<?>> componentListeners = activeComponentListeners.get(componentClass);
            if (componentListeners != null) {
                for (ComponentActivationInstanceListener componentListener : componentListeners) {
                    componentListener.update(componentInstance);
                }
            }
        }

        @Override
        public <T> void registerListener(Class<T> componentClass, ComponentActivationInstanceListener<T> listener) {
            actionRecord.updateComponents.put(componentClass, listener);

            List<ComponentActivationInstanceListener<?>> componentListeners = activeComponentListeners.get(componentClass);
            if (componentListeners == null) {
                componentListeners = new ArrayList<>();
                activeComponentListeners.put(componentClass, componentListeners);
            }

            componentListeners.add(listener);
        }

        @Override
        public <T> void registerUpdateListener(Class<T> componentClass, ComponentActivationInstanceListener<T> listener) {
            registerListener(componentClass, listener);
        }
    }

    protected static class ActionRecord {

        List<Action> actionInstances = new ArrayList<>();
        Map<Class<?>, ComponentActivationInstanceListener<?>> updateComponents = new HashMap<>();
    }
}
