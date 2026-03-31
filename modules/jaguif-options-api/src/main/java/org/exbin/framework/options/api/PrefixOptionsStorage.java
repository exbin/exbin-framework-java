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
package org.exbin.framework.options.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Options storage with key prefix.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PrefixOptionsStorage implements OptionsStorage {

    protected final OptionsStorage optionsStorage;
    protected final String prefix;

    public PrefixOptionsStorage(OptionsStorage optionsStorage, String prefix) {
        this.optionsStorage = optionsStorage;
        this.prefix = prefix;
    }

    @Override
    public void flush() {
        optionsStorage.flush();
    }

    @Override
    public boolean exists(String key) {
        return optionsStorage.exists(prefix + key);
    }

    @Nonnull
    @Override
    public Optional<String> get(String key) {
        return optionsStorage.get(prefix + key);
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        return optionsStorage.get(prefix + key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return optionsStorage.getBoolean(prefix + key, def);
    }

    @Nonnull
    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return optionsStorage.getByteArray(prefix + key, def);
    }

    @Override
    public double getDouble(String key, double def) {
        return optionsStorage.getDouble(prefix + key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return optionsStorage.getFloat(prefix + key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return optionsStorage.getInt(prefix + key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return optionsStorage.getLong(prefix + key, def);
    }

    @Override
    public void put(String key, @Nullable String value) {
        optionsStorage.put(prefix + key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        optionsStorage.putBoolean(prefix + key, value);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        optionsStorage.putByteArray(prefix + key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        optionsStorage.putDouble(prefix + key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        optionsStorage.putFloat(prefix + key, value);
    }

    @Override
    public void putInt(String key, int value) {
        optionsStorage.putInt(prefix + key, value);
    }

    @Override
    public void putLong(String key, long value) {
        optionsStorage.putLong(prefix + key, value);
    }

    @Override
    public void remove(String key) {
        optionsStorage.remove(prefix + key);
    }

    @Override
    public void sync() {
        optionsStorage.sync();
    }
}
