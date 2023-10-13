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
package org.exbin.framework.action.api;

/**
 * Enumeration of menu position modes using build-in groups.
 *
 * @author ExBin Project (https://exbin.org)
 */
public enum PositionMode {

    /**
     * Top position.
     */
    TOP,
    /**
     * End of the top position.
     */
    TOP_LAST,
    /**
     * Default: Normal position in the middle section.
     */
    MIDDLE,
    /**
     * Normal position at the end of the middle section.
     */
    MIDDLE_LAST,
    /**
     * Bottom position.
     */
    BOTTOM,
    /**
     * End of the bottom position.
     */
    BOTTOM_LAST,
    /**
     * Hidden position, should be included manually.
     */
    CUSTOM
}
