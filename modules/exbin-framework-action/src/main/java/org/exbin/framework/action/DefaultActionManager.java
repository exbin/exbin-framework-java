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
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionManager;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeListener;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.context.api.ApplicationContextManager;

/**
 * Action manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultActionManager implements ActionManager {

    protected final ApplicationContextManager contextManager;
    protected final Map<String, ActionRecord> actions = new HashMap<>();
    protected final Map<Class<?>, List<ActionContextChangeListener<?>>> actionContextChangeListeners = new HashMap<>();

    public DefaultActionManager(ApplicationContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
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
    @Override
    public void initAction(Action action) {
        ActionContextChange actionContextChange = (ActionContextChange) action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);
        if (actionContextChange != null) {
            String actionId = (String) action.getValue(ActionConsts.ACTION_ID);
            ActionRecord actionRecord = actions.get(actionId);
            actionContextChange.register(new ActivationManager(actionRecord));
            for (Map.Entry<Class<?>, ActionContextChangeListener<?>> entry : actionRecord.updateComponents.entrySet()) {
                Class<?> updateComponent = entry.getKey();
                Object updateValue = contextManager.getActiveState(updateComponent);
                ActionContextChangeListener listener = entry.getValue();
                listener.stateChanged(updateValue);
            }
        }
    }

    @Override
    public void registerActionContext(Action action) {
        // TODO throw new UnsupportedOperationException("Not supported yet.");
    }

/*    @SuppressWarnings("unchecked")
    @Override
    public <T> void updated(Class<T> componentClass, @Nullable T componentInstance) {
        super.updated(componentClass, componentInstance);
        List<ActionContextChangeListener<?>> componentListeners = actionContextChangeListeners.get(componentClass);
        if (componentListeners != null) {
            for (ActionContextChangeListener componentListener : componentListeners) {
                componentListener.stateChanged(componentInstance);
            }
        }
    } */

    @ParametersAreNonnullByDefault
    private class ActivationManager implements ActionContextChangeManager {

        private final ActionRecord actionRecord;

        public ActivationManager(ActionRecord actionRecord) {
            this.actionRecord = actionRecord;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> void updateActionsForComponent(Class<T> componentClass, @Nullable T componentInstance) {
            throw new UnsupportedOperationException("Not supported yet.");
//            activeComponentState.put(componentClass, componentInstance);
//            List<ActionContextChangeListener<?>> componentListeners = actionContextChangeListeners.get(componentClass);
//            if (componentListeners != null) {
//                for (ActionContextChangeListener componentListener : componentListeners) {
//                    componentListener.stateChanged(componentInstance);
//                }
//            }
        }

        @Override
        public <T> void registerListener(Class<T> componentClass, ActionContextChangeListener<T> listener) {
            actionRecord.updateComponents.put(componentClass, listener);

            List<ActionContextChangeListener<?>> componentListeners = actionContextChangeListeners.get(componentClass);
            if (componentListeners == null) {
                componentListeners = new ArrayList<>();
                actionContextChangeListeners.put(componentClass, componentListeners);
            }

            componentListeners.add(listener);
        }

        @Override
        public <T> void registerUpdateListener(Class<T> componentClass, ActionContextChangeListener<T> listener) {
            registerListener(componentClass, listener);
        }
    }

    protected static class ActionRecord {

        List<Action> actionInstances = new ArrayList<>();
        Map<Class<?>, ActionContextChangeListener<?>> updateComponents = new HashMap<>();
    }

/*
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
                listener.stateChanged(instance);
            }
        }
    }

    @Override
    public <T> void updated(Class<T> instanceClass, @Nullable T instance) {
        activeComponentState.put(instanceClass, instance);
        for (ComponentActivationListener listener : listeners) {
            listener.updated(instanceClass, instance);
        }
    } */
}
