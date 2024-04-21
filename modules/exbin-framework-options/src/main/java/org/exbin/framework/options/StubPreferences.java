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
package org.exbin.framework.options;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.exbin.framework.preferences.PreferencesWrapper;

/**
 * Stub preferences class.
 *
 * @author ExBin Project (https://exbin.org)
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
        protected void putSpi(@Nonnull String key, @Nullable String value) {
            spiValues.put(key, value);
        }

        @Nullable
        @Override
        protected String getSpi(@Nonnull String key) {
            return spiValues.get(key);
        }

        @Override
        protected void removeSpi(@Nonnull String key) {
            spiValues.remove(key);
        }

        @Override
        protected void removeNodeSpi() throws BackingStoreException {
            throw new IllegalStateException("Can't remove the root!");
        }

        @Nullable
        @Override
        protected String[] keysSpi() throws BackingStoreException {
            Set<String> keySet = spiValues.keySet();
            if (keySet == null) {
                return null;
            }

            return (String[]) keySet.toArray(new String[0]);
        }

        @Nonnull
        @Override
        protected String[] childrenNamesSpi() throws BackingStoreException {
            return new String[0];
        }

        @Nullable
        @Override
        protected AbstractPreferences childSpi(@Nonnull String name) {
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
