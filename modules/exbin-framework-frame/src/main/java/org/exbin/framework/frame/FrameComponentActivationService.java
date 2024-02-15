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
package org.exbin.framework.frame;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.ComponentActivationService;

/**
 * Component activation service. Currently used for direct passing.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameComponentActivationService implements ComponentActivationService, ComponentActivationListener {

    private final List<ComponentActivationListener> listeners = new ArrayList<>();
    private final Map<Class<?>, Object> activeComponentState = new HashMap<>();

    @Override
    public void registerListener(ComponentActivationListener listener) {
        listeners.add(listener);
    }

    @Override
    public <T> void updated(Class<T> instanceClass, @Nullable T instance) {
        activeComponentState.put(instanceClass, instance);
        for (ComponentActivationListener listener : listeners) {
            listener.updated(instanceClass, instance);
        }
    }

    @Override
    public void requestUpdate() {
        for (Map.Entry<Class<?>, Object> entry : activeComponentState.entrySet()) {
            Class<?> key = entry.getKey();
            Object instance = entry.getValue();
            for (ComponentActivationListener listener : listeners) {
                // TODO cast instance somehow
                listener.updated(key, null);
            }
        }
    }
}
