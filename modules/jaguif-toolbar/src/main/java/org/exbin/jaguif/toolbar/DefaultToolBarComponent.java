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
package org.exbin.jaguif.toolbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.exbin.jaguif.toolbar.api.ToolBarComponent;

/**
 * Default toolbar definition manager.
 */
@ParametersAreNonnullByDefault
@Immutable
public class DefaultToolBarComponent implements ToolBarComponent {

    private final JComponent component;
    private final Action action;

    public DefaultToolBarComponent(JComponent component, Action action) {
        this.component = component;
        this.action = action;
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }

    @Nonnull
    @Override
    public Action getAction() {
        return action;
    }
}
