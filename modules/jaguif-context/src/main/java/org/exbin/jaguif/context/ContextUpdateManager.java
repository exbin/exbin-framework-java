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

/**
 * Context update manager.
 */
@ParametersAreNonnullByDefault
public class ContextUpdateManager implements ContextUpdateManagement {

    // TODO protected final ContextMessagingService messagingService = new ContextMessagingService();
    protected final Map<String, ContextUpdateRecord> records = new HashMap<>();

    @Override
    public void addRecord(String recordId) {
        records.put(recordId, new ContextUpdateRecord());
    }

    @Override
    public void removeRecord(String recordId) {
        records.remove(recordId);
    }

    @Override
    public void addContextItem(String recordId, ContextChange contextChange) {
        ContextUpdateRecord record = records.get(recordId);
        contextChange.register(record);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void notifyStateChanged(Class<T> stateClass, @Nullable T contextInstance) {
        // TODO: Convert to use service / thread
        for (ContextUpdateRecord record : records.values()) {
            List<ContextStateChangeListener<?>> changeListeners = record.getChangeListeners(stateClass);
            if (changeListeners == null) {
                continue;
            }

            for (ContextStateChangeListener changeListener : changeListeners) {
                changeListener.stateChanged(contextInstance);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void notifyStateUpdated(Class<T> stateClass, T contextInstance, StateUpdateType updateType) {
        // TODO: Convert to use service / thread
        for (ContextUpdateRecord record : records.values()) {
            List<ContextStateUpdateListener<?>> changeListeners = record.getUpdateListeners(stateClass);
            if (changeListeners == null) {
                continue;
            }

            for (ContextStateUpdateListener updateListener : changeListeners) {
                updateListener.notifyStateUpdated(contextInstance, updateType);
            }
        }
    }

    @Nonnull
    @Override
    public <T> List<ContextStateChangeListener<?>> getChangeListeners(String recordId, Class<T> contextClass) {
        List<ContextStateChangeListener<?>> listeners = null;
        ContextUpdateRecord record = records.get(recordId);
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
    public <T> List<ContextStateUpdateListener<?>> getUpdateListeners(String recordId, Class<T> contextClass) {
        List<ContextStateUpdateListener<?>> listeners = null;
        ContextUpdateRecord record = records.get(recordId);
        if (record != null) {
            listeners = record.getUpdateListeners(contextClass);
        }
            
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        
        return listeners;
    }
}
