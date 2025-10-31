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
package org.exbin.framework.context;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.context.api.ApplicationContextManager;
import org.exbin.framework.context.api.ContextModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Implementation of context module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ContextModule implements ContextModuleApi {

    private ResourceBundle resourceBundle;

    private DefaultApplicationContextManager applicationContextManager;

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
    public ApplicationContextManager getMainContextManager() {
        if (applicationContextManager == null) {
            applicationContextManager = new DefaultApplicationContextManager();
        }
        return applicationContextManager;
    }

    @Nonnull
    @Override
    public ApplicationContextManager createContextManager() {
        return new DefaultApplicationContextManager();
    }

    @Nonnull
    @Override
    public ApplicationContextManager createChildContextManager(ApplicationContextManager parentContextManager) {
        return new ChildApplicationContextManager(parentContextManager);
    }
}
