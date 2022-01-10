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
package org.exbin.framework.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.project.action.NewProjectAction;
import org.exbin.framework.project.action.OpenProjectAction;
import org.exbin.framework.project.action.SaveProjectAction;
import org.exbin.framework.project.api.ProjectCategory;
import org.exbin.framework.project.api.ProjectModuleApi;
import org.exbin.framework.project.api.ProjectType;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework project module.
 *
 * @version 0.2.2 2022/01/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ProjectModule implements ProjectModuleApi {

    private java.util.ResourceBundle resourceBundle = null;
    private XBApplication application;

    private static final List<ProjectCategory> projectCategories = new ArrayList<>();
    private static final List<ProjectType> projectTypes = new ArrayList<>();

    private NewProjectAction newProjectAction;
    private OpenProjectAction openProjectAction;
    private SaveProjectAction saveProjectAction;

    public ProjectModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(ProjectModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void registerProjectCategory(ProjectCategory projectCategory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerProjectType(ProjectType projectType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public Collection<ProjectCategory> getProjectCategories() {
        return projectCategories;
    }

    @Nonnull
    @Override
    public Collection<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    @Override
    public void registerMenuFileHandlingActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(PROJECT_MENU_ID, new MenuGroup(PROJECT_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        actionModule.registerMenuItem(PROJECT_MENU_ID, MODULE_ID, getNewProjectAction(), new MenuPosition(PROJECT_MENU_GROUP_ID));
        actionModule.registerMenuItem(PROJECT_MENU_ID, MODULE_ID, getOpenProjectAction(), new MenuPosition(PROJECT_MENU_GROUP_ID));
        actionModule.registerMenuItem(PROJECT_MENU_ID, MODULE_ID, getSaveProjectAction(), new MenuPosition(PROJECT_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public NewProjectAction getNewProjectAction() {
        if (newProjectAction == null) {
            ensureSetup();
            newProjectAction = new NewProjectAction();
            newProjectAction.setup(application, resourceBundle);
        }
        return newProjectAction;
    }

    @Nonnull
    @Override
    public OpenProjectAction getOpenProjectAction() {
        if (openProjectAction == null) {
            ensureSetup();
            openProjectAction = new OpenProjectAction();
            openProjectAction.setup(application, resourceBundle);
        }
        return openProjectAction;
    }

    @Nonnull
    @Override
    public SaveProjectAction getSaveProjectAction() {
        if (saveProjectAction == null) {
            ensureSetup();
            saveProjectAction = new SaveProjectAction();
            saveProjectAction.setup(application, resourceBundle);
        }
        return saveProjectAction;
    }
}
