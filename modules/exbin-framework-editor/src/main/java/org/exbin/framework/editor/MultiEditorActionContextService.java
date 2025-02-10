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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.action.api.DefaultActionContextService;
import org.exbin.framework.file.api.FileHandler;

/**
 * Default multi editor component activation service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiEditorActionContextService extends DefaultActionContextService {

    protected final Set<Class<?>> passActiveComponentState = new HashSet<>();
    protected final Map<FileHandler, ComponentActivationListener> fileActivationListeners = new HashMap<>();

    private FileHandler activeFileHandler = null;

    public <T> void passUpdate(Class<T> instanceClass, @Nullable T instance) {
        passActiveComponentState.add(instanceClass);
        for (ComponentActivationListener listener : listeners) {
            listener.updated(instanceClass, instance);
        }
    }

    public void passRequestUpdate(@Nullable ActionContextService actionContextService) {
        // TODO optimize later
        for (Class<?> instanceClass : passActiveComponentState) {
            for (ComponentActivationListener listener : listeners) {
                listener.updated(instanceClass, null);
            }
        }
        if (actionContextService != null) {
            actionContextService.requestUpdate();
        }
    }

    public void setActiveFileHandler(@Nullable FileHandler activeFileHandler) {
        this.activeFileHandler = activeFileHandler;
    }

    @Nonnull
    public ComponentActivationListener getFileActivationListener(FileHandler fileHandler) {
        ComponentActivationListener listener = fileActivationListeners.get(fileHandler);
        if (listener == null) {
            listener = new ComponentActivationListener() {
                @Override
                public <T> void updated(Class<T> instanceClass, T instance) {
                    if (fileHandler == activeFileHandler) {
                        passUpdate(instanceClass, instance);
                    }
                }
            };
            fileActivationListeners.put(fileHandler, listener);
        }
        return listener;
    }
}
