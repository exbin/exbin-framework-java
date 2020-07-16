/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.utils;

import java.awt.Image;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBApplicationModuleInfo;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.xbup.plugin.XBModule;
import org.exbin.xbup.plugin.XBModuleRecord;

/**
 * Some simple static methods usable for windows and dialogs.
 *
 * @version 0.2.0 2019/06/08
 * @author ExBin Project (http://exbin.org)
 */
public class TestApplication implements XBApplication {

    private final Map<String, XBApplicationModule> modules = new HashMap<>();

    public TestApplication() {
    }

    public void addModule(String moduleId, XBApplicationModule module) {
        modules.put(moduleId, module);
    }

    @Override
    public ResourceBundle getAppBundle() {
        return emptyBundle;
    }

    @Override
    public Preferences getAppPreferences() {
        return new Preferences() {
            @Override
            public void flush() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean exists(String key) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Optional<String> get(String key) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String get(String key, String def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean getBoolean(String key, boolean def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public byte[] getByteArray(String key, byte[] def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double getDouble(String key, double def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public float getFloat(String key, float def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getInt(String key, int def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getLong(String key, long def) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void put(String key, String value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putBoolean(String key, boolean value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putByteArray(String key, byte[] value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putDouble(String key, double value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putFloat(String key, float value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putInt(String key, int value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void putLong(String key, long value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void remove(String key) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void sync() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public void registerLanguagePlugin(Locale locale, ClassLoader classLoader) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Locale> getLanguageLocales() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public XBApplicationModuleRepository getModuleRepository() {
        return new XBApplicationModuleRepository() {
            private static final String MODULE_ID = "MODULE_ID";

            @Override
            public void addModulesFrom(URL moduleJarFileUrl) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addClassPathModules() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addModulesFromManifest(Class manifestClass) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public XBApplicationModuleInfo getModuleRecordById(String moduleId) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public XBApplicationModule getModuleById(String moduleId) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T extends XBModule> T getModuleByInterface(Class<T> interfaceClass) {
                try {
                    Field declaredField = interfaceClass.getDeclaredField(MODULE_ID);
                    if (declaredField != null) {
                        Object interfaceModuleId = declaredField.get(null);
                        if (interfaceModuleId instanceof String) {
                            XBApplicationModule module = modules.get((String) interfaceModuleId);
                            return (T) module;
                        }
                    }

                    return null;
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(TestApplication.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }

            @Override
            public List<XBModuleRecord> getModulesList() {
                return new ArrayList<>();
            }

            @Override
            public void initModules() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addModulesFrom(URI moduleJarFileUri) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void loadModulesFromPath(URI uri) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addModulesFromPath(URL url) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public Image getApplicationIcon() {
        return null;
    }
    ResourceBundle emptyBundle = new ResourceBundle() {

        @Override
        protected Object handleGetObject(String key) {
            return "";
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }
    };
}
