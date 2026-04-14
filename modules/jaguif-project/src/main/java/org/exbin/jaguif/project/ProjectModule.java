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
package org.exbin.jaguif.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.project.action.NewProjectAction;
import org.exbin.jaguif.project.action.OpenProjectAction;
import org.exbin.jaguif.project.action.SaveProjectAction;
import org.exbin.jaguif.project.api.ProjectCategory;
import org.exbin.jaguif.project.api.ProjectModuleApi;
import org.exbin.jaguif.project.api.ProjectType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.project.contribution.NewProjectContribution;
import org.exbin.jaguif.project.contribution.OpenProjectContribution;
import org.exbin.jaguif.project.contribution.SaveProjectContribution;

/**
 * Implementation of framework project module.
 */
@ParametersAreNonnullByDefault
public class ProjectModule implements ProjectModuleApi {

    private java.util.ResourceBundle resourceBundle = null;

    private static final List<ProjectCategory> projectCategories = new ArrayList<>();
    private static final List<ProjectType> projectTypes = new ArrayList<>();

    public ProjectModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ProjectModule.class);
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
        String parentId = getParentId(projectCategory.getId());
        if (parentId != null) {
            if (!projectCategories.stream().anyMatch(category -> parentId.equals(category.getId()))) {
                throw new IllegalStateException("Missing parent category");
            }
        }
        if (!projectCategories.stream().anyMatch(category -> projectCategory.getId().equals(category.getId()))) {
            throw new IllegalStateException("Project category already registered");
        }

        projectCategories.add(projectCategory);
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
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(PROJECT_SUBMENU_ID, FrameModuleApi.MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(PROJECT_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = new NewProjectContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(PROJECT_MENU_GROUP_ID));
        contribution = new OpenProjectContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(PROJECT_MENU_GROUP_ID));
        contribution = new SaveProjectContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(PROJECT_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public NewProjectAction createNewProjectAction() {
        ensureSetup();
        NewProjectAction newProjectAction = new NewProjectAction();
        newProjectAction.init(resourceBundle);
        return newProjectAction;
    }

    @Nonnull
    @Override
    public OpenProjectAction createOpenProjectAction() {
        ensureSetup();
        OpenProjectAction openProjectAction = new OpenProjectAction();
        openProjectAction.init(resourceBundle);
        return openProjectAction;
    }

    @Nonnull
    @Override
    public SaveProjectAction createSaveProjectAction() {
        ensureSetup();
        SaveProjectAction saveProjectAction = new SaveProjectAction();
        saveProjectAction.init(resourceBundle);
        return saveProjectAction;
    }

    @Nullable
    public static String getParentId(String id) {
        int lastIndex = id.lastIndexOf("/");
        if (lastIndex > 0) {
            return id.substring(lastIndex + 1);
        }

        return null;
    }
}
