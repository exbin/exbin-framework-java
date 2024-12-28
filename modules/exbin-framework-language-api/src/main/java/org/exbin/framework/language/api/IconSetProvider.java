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
package org.exbin.framework.language.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Icon set provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface IconSetProvider {

    /**
     * Returns icon set id.
     *
     * @return id
     */
    @Nonnull
    String getId();

    /**
     * Returns icon set name.
     *
     * @return name
     */
    @Nonnull
    String getName();

    /**
     * Returns icon resource for given key.
     *
     * @param key
     * @return resource path
     */
    @Nullable
    String getIconKey(String key);
}
