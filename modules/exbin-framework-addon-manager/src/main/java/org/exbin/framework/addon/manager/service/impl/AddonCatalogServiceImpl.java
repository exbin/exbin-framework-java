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
package org.exbin.framework.addon.manager.service.impl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogService;

/**
 * Addon catalog service implementation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonCatalogServiceImpl implements AddonCatalogService {

    @Nonnull
    @Override
    public AddonsListResult searchForAddons(String searchCondition) {
        return new AddonsListResult() {
            @Override
            public int itemsCount() {
                return 0;
            }

            @Nonnull
            @Override
            public AddonRecord getLazyItem(int index) {
                throw new IndexOutOfBoundsException();
            }
        };
    }
}
