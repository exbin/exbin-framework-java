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
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeListener;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextStateUpdateListener;
import org.exbin.jaguif.context.api.ContextValues;

/**
 * Context registration.
 */
@ParametersAreNonnullByDefault
public class ContextRegistrar implements ContextRegistration, ContextChangeRegistration {

    public static final String KEY_CONTEXT_CHANGE = "ContextChange";
    protected final List<ContextValues> contextItems = new ArrayList<>();
    protected final Map<Class<?>, ContextChangeListener<?>> contextChangeListeners = new HashMap<>();
    protected final Map<Class<?>, ContextStateUpdateListener<?>> contextStateUpdateListeners = new HashMap<>();
    protected ActiveContextManagement contextManagement;

    public ContextRegistrar(ActiveContextManagement contextManagement) {
        this.contextManagement = contextManagement;
    }

    @Override
    public void registerItemContext(ContextValues contextItem) {
        contextItems.add(contextItem);
        ContextChange contextChange = (ContextChange) contextItem.getValue(KEY_CONTEXT_CHANGE);
        if (contextChange == null) {
            return;
        }

        contextChange.register(this);
    }

    @Override
    public void finish() {
        Collection<Class<?>> stateClasses = contextManagement.getStateClasses();
        for (ContextChangeListener<?> listener : contextChangeListeners.values()) {
            for (Class<?> stateClass : stateClasses) {
                // TODO listener.stateChanged(contextManagement.getActiveState(stateClass));
            }
        }
    }

    @Override
    public <T> void registerChangeListener(Class<T> contextClass, ContextChangeListener<T> listener) {
        contextChangeListeners.put(contextClass, listener);
    }

    @Override
    public <T> void registerStateUpdateListener(Class<T> contextClass, ContextStateUpdateListener<T> listener) {
        contextStateUpdateListeners.put(contextClass, listener);
    }
}
