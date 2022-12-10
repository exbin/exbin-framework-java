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
package org.exbin.framework.service.catalog.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.base.XBCBase;
import org.exbin.xbup.core.catalog.base.XBCExtension;
import org.exbin.xbup.core.catalog.base.service.XBCService;

/**
 * List model for catalog extensions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogExtensionsListModel extends AbstractListModel<String> {

    private XBCatalog catalog;
    private final List<XBCService<? extends XBCBase>> extensions;

    public CatalogExtensionsListModel() {
        extensions = new ArrayList<>();
    }

    @Override
    public int getSize() {
        return extensions.size();
    }

    @Override
    public String getElementAt(int index) {
        return ((XBCExtension) extensions.get(index)).getExtensionName();
    }

    void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        extensions.clear();
        if (catalog != null) {
            for (XBCService<? extends XBCBase> service : catalog.getCatalogServices()) {
                if (service instanceof XBCExtension) {
                    extensions.add(service);
                }
            }

            fireContentsChanged(this, 0, extensions.size() - 1);
        }
    }
}
