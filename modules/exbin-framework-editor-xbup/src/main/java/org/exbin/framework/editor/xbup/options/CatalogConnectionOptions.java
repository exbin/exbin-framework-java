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
package org.exbin.framework.editor.xbup.options;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Catalog connection options.
 *
 * @version 0.2.1 2020/03/02
 * @author ExBin Project (http://exbin.org)
 */
public interface CatalogConnectionOptions {

    @Nonnull
    Optional<String> getCatalogUpdateUrl();

    @Nonnull
    Optional<String> getServiceConnectionUrl();

    boolean isCatalogUpdateAllowed();

    boolean isServiceConnectionAllowed();

    void setCatalogUpdateAllowed(boolean catalogUpdateAllowed);

    void setCatalogUpdateUrl(@Nullable String catalogUpdateUrl);

    void setServiceConnectionAllowed(boolean serviceConnectionAllowed);

    void setServiceConnectionUrl(@Nullable String serviceConnectionUrl);
}
