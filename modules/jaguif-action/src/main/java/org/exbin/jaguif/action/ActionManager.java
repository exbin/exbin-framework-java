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
package org.exbin.jaguif.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionManagement;
import org.exbin.jaguif.context.api.ActiveContextChangeListener;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChangeListener;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.context.api.ContextStateUpdateListener;

/**
 * Default action manager.
 */
@ParametersAreNonnullByDefault
public class ActionManager implements ActionManagement {

    protected final ActiveContextManagement contextManager;
    protected final Map<String, ActionRecord> actions = new HashMap<>();
    protected final Map<Class<?>, List<ContextChangeListener<?>>> actionContextChangeListeners = new HashMap<>();
    protected final Map<Class<?>, List<ContextStateUpdateListener<?>>> actionContextStateUpdateListeners = new HashMap<>();

    public ActionManager(ActiveContextManagement contextManager) {
        this.contextManager = contextManager;
        contextManager.addChangeListener(new ActiveContextChangeListener() {
            @Override
            public <T> void notifyStateChanged(Class<T> stateClass, T activeState) {
                ActionManager.this.activeStateChanged(stateClass, activeState);
            }

            @Override
            public <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType stateUpdateType) {
                ActionManager.this.notifyStateUpdate(stateClass, activeState, stateUpdateType);
            }
        });
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
        if (actionContextChange == null) {
            return;
        }

        String actionId = (String) action.getValue(ActionConsts.ACTION_ID);
        ActionRecord actionRecord = actions.get(actionId);
        actionContextChange.register(new DefaultActionContextChangeRegistrar(actionRecord));
        for (Map.Entry<Class<?>, ContextChangeListener<?>> entry : actionRecord.contextChangeListeners.entrySet()) {
            Class<?> stateClass = entry.getKey();
            Object activeState = contextManager.getActiveState(stateClass);
            ContextChangeListener listener = entry.getValue();
            listener.stateChanged(activeState);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void activeStateChanged(Class<T> stateClass, @Nullable T contextInstance) {
        // TODO: Convert to thread
        List<ContextChangeListener<?>> contextListeners = actionContextChangeListeners.get(stateClass);
        if (contextListeners == null) {
            return;
        }

        for (ContextChangeListener contextListener : contextListeners) {
            contextListener.stateChanged(contextInstance);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void notifyStateUpdate(Class<T> stateClass, @Nullable T contextInstance, StateUpdateType updateType) {
        // TODO: Convert to thread
        List<ContextStateUpdateListener<?>> updateListeners = actionContextStateUpdateListeners.get(stateClass);
        if (updateListeners == null) {
            return;
        }

        for (ContextStateUpdateListener contextListener : updateListeners) {
            contextListener.notifyStateUpdated(contextInstance, updateType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestUpdateForAction(Action action) {
        String actionId = (String) action.getValue(ActionConsts.ACTION_ID);
        if (actionId != null) {
            ActionRecord actionRecord = actions.get(actionId);
            if (actionRecord == null) {
                return;
            }

            // TODO Restrict to specific action listeners
            for (Class<?> stateClass : contextManager.getStateClasses()) {
                Object instance = contextManager.getActiveState(stateClass);
                ContextChangeListener listener = actionRecord.contextChangeListeners.get(stateClass);
                if (listener != null) {
                    listener.stateChanged(instance);
                }
            }
        } else {
            // TODO Temporary workaround for non-registered actions or action without action ID
            ActionContextChange actionContextChange = (ActionContextChange) action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);
            if (actionContextChange == null) {
                return;
            }
            
            ActionRecord actionRecord = new ActionRecord();
            ContextChangeRegistration registrar = new DefaultActionContextChangeRegistrar(actionRecord);
            actionContextChange.register(registrar);
            
            for (Class<?> stateClass : contextManager.getStateClasses()) {
                Object instance = contextManager.getActiveState(stateClass);
                ContextChangeListener listener = actionRecord.contextChangeListeners.get(stateClass);
                if (listener != null) {
                    listener.stateChanged(instance);
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private class DefaultActionContextChangeRegistrar implements ContextChangeRegistration {

        private final ActionRecord actionRecord;

        public DefaultActionContextChangeRegistrar(ActionRecord actionRecord) {
            this.actionRecord = actionRecord;
        }

        @Override
        public <T> void registerChangeListener(Class<T> contextClass, ContextChangeListener<T> listener) {
            actionRecord.contextChangeListeners.put(contextClass, listener);

            List<ContextChangeListener<?>> contextChangeListeners = actionContextChangeListeners.get(contextClass);
            if (contextChangeListeners == null) {
                contextChangeListeners = new ArrayList<>();
                actionContextChangeListeners.put(contextClass, contextChangeListeners);
            }

            contextChangeListeners.add(listener);
        }

        @Override
        public <T> void registerStateUpdateListener(Class<T> contextClass, ContextStateUpdateListener<T> listener) {
            actionRecord.contextStateUpdateListeners.put(contextClass, listener);

            List<ContextStateUpdateListener<?>> contextStateUpdateListener = actionContextStateUpdateListeners.get(contextClass);
            if (contextStateUpdateListener == null) {
                contextStateUpdateListener = new ArrayList<>();
                actionContextStateUpdateListeners.put(contextClass, contextStateUpdateListener);
            }

            contextStateUpdateListener.add(listener);
        }
    }

    protected static class ActionRecord {

        List<Action> actionInstances = new ArrayList<>();
        Map<Class<?>, ContextChangeListener<?>> contextChangeListeners = new HashMap<>();
        Map<Class<?>, ContextStateUpdateListener<?>> contextStateUpdateListeners = new HashMap<>();
    }
}
