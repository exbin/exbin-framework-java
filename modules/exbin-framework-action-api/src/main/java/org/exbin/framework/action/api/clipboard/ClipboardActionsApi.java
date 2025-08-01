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
package org.exbin.framework.action.api.clipboard;

import javax.annotation.Nonnull;
import javax.swing.Action;

/**
 * Interface for clipboard editing actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface ClipboardActionsApi {

    /**
     * Creates cut to clipboard action.
     *
     * @return action
     */
    @Nonnull
    Action createCutAction();

    /**
     * Creates copy to clipboard action.
     *
     * @return action
     */
    @Nonnull
    Action createCopyAction();

    /**
     * Creates paste from clipboard action.
     *
     * @return action
     */
    @Nonnull
    Action createPasteAction();

    /**
     * Creates delete selection action.
     *
     * @return action
     */
    @Nonnull
    Action createDeleteAction();

    /**
     * Creates select all action.
     *
     * @return action
     */
    @Nonnull
    Action createSelectAllAction();
}
