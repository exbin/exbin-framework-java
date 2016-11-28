/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.options;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Stub preferences class.
 *
 * @version 0.2.0 2016/11/28
 * @author ExBin Project (http://exbin.org)
 */
public class StubPreferences extends AbstractPreferences {

    private final Map<String, String> spiValues;

    public StubPreferences() {
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
};
