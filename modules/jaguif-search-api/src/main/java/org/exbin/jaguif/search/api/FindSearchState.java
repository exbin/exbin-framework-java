/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.search.api;

import org.exbin.jaguif.context.api.StateUpdateType;

/**
 * Interface for find search state.
 */
public interface FindSearchState {

    /**
     * Performs find action.
     */
    void performFind();

    /**
     * Performs find next action.
     */
    void performFindNext();

    /**
     * Performs find previous action.
     */
    void performFindPrevious();

    /**
     * Returns if find next is available.
     *
     * @return true if available
     */
    boolean isFindNextAvailable();

    /**
     * Returns if find previous is available.
     *
     * @return true if available
     */
    boolean isFindPreviousAvailable();

    public enum UpdateType implements StateUpdateType {
        FIND_AVAILABILITY
    }
}
