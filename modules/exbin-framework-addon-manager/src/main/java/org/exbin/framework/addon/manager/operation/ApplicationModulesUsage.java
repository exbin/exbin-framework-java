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
package org.exbin.framework.addon.manager.operation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Application modules usage.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ApplicationModulesUsage {

    /**
     * Returns true if module with given id is used by application.
     *
     * @param moduleId module id
     * @return true if used
     */
    boolean hasModule(String moduleId);

    /**
     * Returns true if library with given file name is used by application.
     *
     * @param libraryFileName library file name
     * @return true if used
     */
    boolean hasLibrary(String libraryFileName);
}
