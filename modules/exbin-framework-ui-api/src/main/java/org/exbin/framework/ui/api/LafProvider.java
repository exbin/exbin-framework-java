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
package org.exbin.framework.ui.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Look&feel provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface LafProvider {

    /**
     * Returns look and feel string identifier.
     *
     * @return string identifier
     */
    @Nonnull
    String getLafId();

    /**
     * Returns look and feel default name.
     *
     * @return name
     */
    @Nonnull
    String getLafName();

    /**
     * Installs look and feel into UIManager.
     */
    void installLaf();

    /**
     * Applies specific look and feel.
     */
    void applyLaf();
}
