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
package org.exbin.framework.options.settings.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Default options storage.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultOptionsStorage implements OptionsStorage {

    private final Map<String, Object> values = new HashMap<>();

    @Override
    public void flush() {
    }

    @Override
    public boolean exists(String key) {
        return values.containsKey(key);
    }

    @Nonnull
    @Override
    public Optional<String> get(String key) {
        Object value = values.get(key);
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(valueAsString(value));
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        Object value = values.getOrDefault(key, def);
        return valueAsString(value);
    }

    @Nonnull
    private static String valueAsString(@Nonnull Object value) {
        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return (Boolean) values.getOrDefault(key, def);
    }

    @Nonnull
    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return (byte[]) values.getOrDefault(key, def);
    }

    @Override
    public double getDouble(String key, double def) {
        return (Double) values.getOrDefault(key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return (Float) values.getOrDefault(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return (Integer) values.getOrDefault(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return (Long) values.getOrDefault(key, def);
    }

    @Override
    public void put(String key, @Nullable String value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
    }

    @Override
    public void putBoolean(String key, boolean value) {
        values.put(key, value);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        values.put(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        values.put(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        values.put(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        values.put(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        values.put(key, value);
    }

    @Override
    public void remove(String key) {
        values.remove(key);
    }

    @Override
    public void sync() {
    }
}
