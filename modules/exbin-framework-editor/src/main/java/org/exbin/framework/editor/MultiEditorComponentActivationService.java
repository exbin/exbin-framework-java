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
package org.exbin.framework.editor;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.api.DefaultComponentActivationService;

/**
 * Default multi editor component activation service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiEditorComponentActivationService extends DefaultComponentActivationService {

    protected final Set<Class<?>> passActiveComponentState = new HashSet<>();

    public <T> void passUpdate(Class<T> instanceClass, @Nullable T instance) {
        passActiveComponentState.add(instanceClass);
        for (ComponentActivationListener listener : listeners) {
            listener.updated(instanceClass, instance);
        }
    }

    public void passRequestUpdate(@Nullable ComponentActivationService componentActivationService) {
        // TODO optimize later
        for (Class<?> instanceClass : passActiveComponentState) {
            for (ComponentActivationListener listener : listeners) {
                listener.updated(instanceClass, null);
            }
        }
        if (componentActivationService != null) {
            componentActivationService.requestUpdate();
        }
    }
}
