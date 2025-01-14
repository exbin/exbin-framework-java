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
package org.exbin.framework.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.LauncherModule;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleProvider;

/**
 * Some simple static methods usable for testing windows and dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TestApplication {

    private static final String MODULE_ID = "MODULE_ID";
    private static final String MODULE_FILE = "module.xml";

    private final Map<String, Module> modules = new HashMap<>();

    TestApplication() {
    }

    public void launch(Runnable runnable) {
        attachModuleProvider();
        App.launch(runnable);
    }

    public void launch(String launcherModuleId, String[] args) {
        attachModuleProvider();
        App.launch(launcherModuleId, args);
    }

    private void attachModuleProvider() {
        App.setModuleProvider(new ModuleProvider() {
            @Nonnull
            @Override
            public Class getManifestClass() {
                return TestApplication.this.getClass();
            }

            @Override
            public void launch(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void launch(String launcherModuleId, String[] args) {
                try {
                    Class<?> launcherClass = Class.forName(launcherModuleId);
                    Constructor<?> constructor = launcherClass.getConstructor();
                    LauncherModule launcherModule = (LauncherModule) constructor.newInstance();
                    launcherModule.launch(args);
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                    Logger.getLogger(TestApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends Module> T getModule(Class<T> interfaceClass) {
                try {
                    Field declaredField = interfaceClass.getDeclaredField(MODULE_ID);
                    if (declaredField != null) {
                        Object interfaceModuleId = declaredField.get(null);
                        if (interfaceModuleId instanceof String) {
                            Module module = modules.get((String) interfaceModuleId);
                            if (module != null) {
                                return (T) module;
                            }

//                            if ("org.exbin.framework.language.LanguageModule".equals(interfaceModuleId)) {
//                                return (T) languageModule;
//                            }
                            throw new IllegalStateException("Module not included in test application: " + interfaceModuleId);
//                            
//                            XBApplicationModule module = modules.get((String) interfaceModuleId);
//                            return (T) module;
                        }
                    }
                } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                    Logger.getLogger(TestApplication.class.getName()).log(Level.SEVERE, null, ex);
                }

                throw new IllegalStateException("Module not included in test application");
            }
        });
    }

    public static void run(Runnable runnable) {
        new TestApplication().launch(runnable);
    }

    public void addModule(String moduleId, Module module) {
        modules.put(moduleId, module);
    }
}
