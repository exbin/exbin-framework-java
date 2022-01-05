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
package org.exbin.framework.editor.xbup.viewer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Interface for document tab.
 *
 * @version 0.2.1 2020/09/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DocumentTab extends ClipboardActionsHandler {

    void setSelectedItem(@Nullable XBTBlock item);

    @Nonnull
    JComponent getComponent();

    void setActivationListener(ActivationListener listener);

    void setCatalog(XBACatalog catalog);

    void setApplication(XBApplication application);

    public void setPluginRepository(XBPluginRepository pluginRepository);

    public interface ActivationListener {

        void activated();
    }
}
