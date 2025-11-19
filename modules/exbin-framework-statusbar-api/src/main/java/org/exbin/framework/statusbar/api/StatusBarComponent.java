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
package org.exbin.framework.statusbar.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;

/**
 * Interface for status bar component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface StatusBarComponent {

    /**
     * Identifier value.
     * <p>
     * Value is of type {@link String}.
     */
    public static final String KEY_ID = "ID";
    /**
     * Tool tip value.
     * <p>
     * Value is of type {@link String}.
     */
    public static final String KEY_TOOLTIP = "ToolTip";
    /**
     * Context change value.
     * <p>
     * Value is of type {@link org.exbin.framework.context.api.ContextChange}.
     */
    public static final String KEY_CONTEXT_CHANGE = "ContextChange";

    /**
     * Creates instance of the component.
     *
     * @return component instance
     */
    @Nonnull
    JComponent createComponent();

    /**
     * Gets one of this object's properties using the associated key.
     *
     * @param key identifier
     * @return property value
     */
    @Nullable
    Object getValue(String key);

    /**
     * Sets one of this object's properties using the associated key.
     *
     * @param key identifier
     * @param value property value
     */
    void putValue(String key, @Nullable Object value);
}
