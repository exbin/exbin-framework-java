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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextStateChangeListener;
import org.exbin.jaguif.context.api.ContextStateUpdateListener;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.context.service.ContextMessagingService;

/**
 * Context update manager.
 */
@ParametersAreNonnullByDefault
public class ContextUpdateManager implements ContextUpdateManagement {

    public static final String DEFAULT_GROUP = "";
    protected final ContextMessagingService messagingService = new ContextMessagingService();
    protected final Map<String, ContextUpdateRecord> records = new HashMap<>();

    public ContextUpdateManager() {
        records.put(DEFAULT_GROUP, new ContextUpdateRecord());
    }

    @Override
    public void addGroup(String groupId) {
        records.put(groupId, new ContextUpdateRecord());
    }

    @Override
    public void removeGroup(String groupId) {
        records.remove(groupId);
    }

    @Override
    public void addContextItem(ContextChange contextChange) {
        addContextItem(DEFAULT_GROUP, contextChange);
    }

    @Override
    public void addContextItem(String groupId, ContextChange contextChange) {
        ContextUpdateRecord record = records.get(groupId);
        contextChange.register(record);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void notifyStateChanged(Class<T> stateClass, @Nullable T contextInstance) {
        LinkedList<ContextStateChangeListener> listeners = new LinkedList<>();
        for (ContextUpdateRecord record : records.values()) {
            List<ContextStateChangeListener<?>> changeListeners = record.getChangeListeners(stateClass);
            if (changeListeners == null) {
                continue;
            }

            for (ContextStateChangeListener changeListener : changeListeners) {
                listeners.add(changeListener);
            }
        }

        messagingService.notifyStateChanged(listeners, contextInstance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void notifyStateUpdated(Class<T> stateClass, T contextInstance, StateUpdateType updateType) {
        LinkedList<ContextStateUpdateListener> listeners = new LinkedList<>();
        for (ContextUpdateRecord record : records.values()) {
            List<ContextStateUpdateListener<?>> updateListeners = record.getUpdateListeners(stateClass);
            if (updateListeners == null) {
                continue;
            }

            for (ContextStateUpdateListener updateListener : updateListeners) {
                listeners.add(updateListener);
            }
        }

        messagingService.notifyStateUpdated(listeners, contextInstance, updateType);
    }

    @Nonnull
    @Override
    public <T> List<ContextStateChangeListener<?>> getChangeListeners(String groupId, Class<T> contextClass) {
        List<ContextStateChangeListener<?>> listeners = null;
        ContextUpdateRecord record = records.get(groupId);
        if (record != null) {
            listeners = record.getChangeListeners(contextClass);
        }

        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        return listeners;
    }

    @Nonnull
    @Override
    public <T> List<ContextStateUpdateListener<?>> getUpdateListeners(String groupId, Class<T> contextClass) {
        List<ContextStateUpdateListener<?>> listeners = null;
        ContextUpdateRecord record = records.get(groupId);
        if (record != null) {
            listeners = record.getUpdateListeners(contextClass);
        }

        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        return listeners;
    }
}
