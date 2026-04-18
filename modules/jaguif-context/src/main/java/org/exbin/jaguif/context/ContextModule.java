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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChangeListener;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.context.api.StateUpdateType;

/**
 * Implementation of context module.
 */
@ParametersAreNonnullByDefault
public class ContextModule implements ContextModuleApi {

    private ResourceBundle resourceBundle;

    private ActiveContextManager applicationContextManager;

    public ContextModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ContextModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public ActiveContextManagement getMainContextManager() {
        if (applicationContextManager == null) {
            applicationContextManager = new ActiveContextManager();
        }
        return applicationContextManager;
    }

    @Nonnull
    @Override
    public ActiveContextManagement createContextManager() {
        return new ActiveContextManager();
    }

    @Nonnull
    @Override
    public ContextRegistration createContextRegistrator() {
        return new ContextRegistrar(getMainContextManager());
    }

    @Override
    public ContextRegistration createContextRegistrator(ActiveContextManagement contextManager) {
        return new ContextRegistrar(contextManager);
    }

    @Nonnull
    @Override
    public ContextRegistration createContextRegistrator(String recordId, ContextUpdateManagement contextUpdateManagement, ActiveContextManagement contextManager) {
        return new ContextUpdateRegistrar(recordId, contextUpdateManagement, contextManager);
    }

    @Nonnull
    @Override
    public ContextUpdateManagement createContextUpdateManagement() {
        return new ContextUpdateManager();
    }

    @Nonnull
    @Override
    public ContextUpdateManagement createContextUpdateManagement(ActiveContextManagement contextManagement) {
        ContextUpdateManager contextUpdateManager = new ContextUpdateManager();
        contextManagement.addChangeListener(new ContextChangeListener() {
            @Override
            public <T> void notifyStateChanged(Class<T> stateClass, T activeState) {
                contextUpdateManager.notifyStateChanged(stateClass, activeState);
            }

            @Override
            public <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType updateType) {
                contextUpdateManager.notifyStateUpdated(stateClass, activeState, updateType);
            }
        });
        return contextUpdateManager;
    }

    @Nonnull
    @Override
    public ActiveContextManagement createChildContextManager(ActiveContextManagement parentContextManager) {
        return new ChildActiveContextManager(parentContextManager);
    }
}
