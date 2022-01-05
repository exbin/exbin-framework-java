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
package org.exbin.framework.options;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.exbin.framework.preferences.PreferencesWrapper;

/**
 * Stub preferences class.
 *
 * @version 0.2.0 2019/06/08
 * @author ExBin Project (http://exbin.org)
 */
public class StubPreferences extends PreferencesWrapper {

    public StubPreferences() {
        super(new StubPreferencesImpl());
    }

    private static final class StubPreferencesImpl extends AbstractPreferences {

        private final Map<String, String> spiValues;

        private StubPreferencesImpl() {
            super(null, "");
            spiValues = new HashMap<>();
        }

        @Override
        protected void putSpi(String key, String value) {
            spiValues.put(key, value);
        }

        @Override
        protected String getSpi(String key) {
            return spiValues.get(key);
        }

        @Override
        protected void removeSpi(String key) {
            spiValues.remove(key);
        }

        @Override
        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Can't remove the root!");
        }

        @Override
        protected String[] keysSpi() throws BackingStoreException {
            return (String[]) spiValues.keySet().toArray(new String[0]);
        }

        @Override
        protected String[] childrenNamesSpi() throws BackingStoreException {
            return new String[0];
        }

        @Override
        protected AbstractPreferences childSpi(String name) {
            return null;
        }

        @Override
        protected void syncSpi() throws BackingStoreException {
        }

        @Override
        protected void flushSpi() throws BackingStoreException {
        }
    }
}
