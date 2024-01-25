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
package org.exbin.framework.project.api;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Interface for framework project module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ProjectModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ProjectModuleApi.class);
    public static String PROJECT_MENU_ID = WindowModuleApi.MAIN_MENU_ID + "/File";
    public static final String PROJECT_MENU_GROUP_ID = MODULE_ID + ".projectMenuGroup";

    void registerProjectCategory(ProjectCategory projectCategory);

    void registerProjectType(ProjectType projectType);

    @Nonnull
    Collection<ProjectCategory> getProjectCategories();

    @Nonnull
    Collection<ProjectType> getProjectTypes();

    void registerMenuFileHandlingActions();

    @Nonnull
    AbstractAction getNewProjectAction();

    @Nonnull
    AbstractAction getOpenProjectAction();

    @Nonnull
    AbstractAction getSaveProjectAction();
}
