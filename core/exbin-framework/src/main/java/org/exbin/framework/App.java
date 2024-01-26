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
package org.exbin.framework;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public final class App {

    private static final String MODULE_ID = "MODULE_ID";
    private static ModuleProvider moduleProvider = null;

    private App() {
        // No instance
    }
    
    public static void launch(Runnable runnable) {
        if (moduleProvider == null) {
            throw new IllegalStateException("Module provider not initialized");
        }

        moduleProvider.launch(runnable);
    }

    @Nonnull
    public static <T extends Module> T getModule(Class<T> interfaceClass) {
        if (moduleProvider != null) {
            return moduleProvider.getModule(interfaceClass);
        }
        
        throw new IllegalStateException("Module provider not initialized");
//        try {
//            Field declaredField = interfaceClass.getDeclaredField(MODULE_ID);
//            if (declaredField != null) {
//                Object moduleId = declaredField.get(null);
//                if (moduleId instanceof String) {
//                    @SuppressWarnings("unchecked")
//                    T module = (T) getModuleById((String) moduleId);
//                    return module;
//                }
//            }
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//            Logger.getLogger(XBDefaultModuleRepository.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//
//        throw new IllegalArgumentException("Module for class " + interfaceClass.getCanonicalName() + " was not found.");
    }
    
    public static void setModuleProvider(ModuleProvider moduleProvider) {
        if (App.moduleProvider != null) {
            throw new IllegalStateException("Module provider already initialized");
        }
        
        App.moduleProvider = moduleProvider;
    }
}
