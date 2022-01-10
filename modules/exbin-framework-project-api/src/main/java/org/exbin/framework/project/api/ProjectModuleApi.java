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
package org.exbin.framework.project.api;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Interface for framework project module.
 *
 * @version 0.2.2 2022/01/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ProjectModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(ProjectModuleApi.class);
    public static String PROJECT_MENU_ID = FrameModuleApi.MAIN_MENU_ID + "/File";
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
