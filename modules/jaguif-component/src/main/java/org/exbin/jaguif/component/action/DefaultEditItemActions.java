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
package org.exbin.jaguif.component.action;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.component.ComponentModule;
import org.exbin.jaguif.component.api.action.EditItemActions;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;

/**
 * Item edit default action set.
 */
@ParametersAreNonnullByDefault
public class DefaultEditItemActions implements EditItemActions {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ComponentModule.class);

    protected final EditItemMode mode;

    public DefaultEditItemActions() {
        this(EditItemMode.NORMAL);
    }

    public DefaultEditItemActions(EditItemMode mode) {
        this.mode = mode;
    }

    @Nonnull
    @Override
    public AddItemAction createAddItemAction() {
        AddItemAction addItemAction = new AddItemAction(mode);
        addItemAction.init(resourceBundle);
        return addItemAction;
    }

    @Nonnull
    @Override
    public EditItemAction createEditItemAction() {
        EditItemAction editItemAction = new EditItemAction(mode);
        editItemAction.init(resourceBundle);
        return editItemAction;
    }

    @Nonnull
    @Override
    public DeleteItemAction createDeleteItemAction() {
        DeleteItemAction deleteItemAction = new DeleteItemAction();
        deleteItemAction.init(resourceBundle);
        return deleteItemAction;
    }

    @Nonnull
    @Override
    public SequenceContribution createAddItemContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createAddItemAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return AddItemAction.ACTION_ID;
            }
        };
    }

    @Nonnull
    @Override
    public SequenceContribution createEditItemContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createEditItemAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return EditItemAction.ACTION_ID;
            }
        };
    }

    @Nonnull
    @Override
    public SequenceContribution createDeleteItemContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createDeleteItemAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return DeleteItemAction.ACTION_ID;
            }
        };
    }

    @Override
    public void registerToolBarContributions(ToolBarDefinitionManagement toolBarDefinition) {
        GroupSequenceContribution toolBarGroup = toolBarDefinition.registerToolBarGroup("editItem");
        SequenceContribution contribution = createAddItemContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
        contribution = createEditItemContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
        contribution = createDeleteItemContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
    }
}
