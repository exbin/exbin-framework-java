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
package org.exbin.framework.options.api.utils;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Empty options storage.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EmptyOptionsStorage implements OptionsStorage {

    @Override
    public void flush() {
    }

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Nonnull
    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        return def;
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return def;
    }

    @Nonnull
    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return def;
    }

    @Override
    public double getDouble(String key, double def) {
        return def;
    }

    @Override
    public float getFloat(String key, float def) {
        return def;
    }

    @Override
    public int getInt(String key, int def) {
        return def;
    }

    @Override
    public long getLong(String key, long def) {
        return def;
    }

    @Override
    public void put(String key, String value) {
    }

    @Override
    public void putBoolean(String key, boolean value) {
    }

    @Override
    public void putByteArray(String key, byte[] value) {
    }

    @Override
    public void putDouble(String key, double value) {
    }

    @Override
    public void putFloat(String key, float value) {
    }

    @Override
    public void putInt(String key, int value) {
    }

    @Override
    public void putLong(String key, long value) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public void sync() {
    }
}
