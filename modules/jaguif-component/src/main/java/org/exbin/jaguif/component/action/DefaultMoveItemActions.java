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
import org.exbin.jaguif.component.api.action.MoveItemActions;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;

/**
 * Item movement default action set.
 */
@ParametersAreNonnullByDefault
public class DefaultMoveItemActions implements MoveItemActions {

    public static final String GROUP_ID = "moveItem";
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ComponentModule.class);

    public DefaultMoveItemActions() {
    }

    @Nonnull
    @Override
    public MoveUpAction createMoveUpAction() {
        MoveUpAction moveUpAction = new MoveUpAction();
        moveUpAction.init(resourceBundle);
        return moveUpAction;
    }

    @Nonnull
    @Override
    public MoveDownAction createMoveDownAction() {
        MoveDownAction moveDownAction = new MoveDownAction();
        moveDownAction.init(resourceBundle);
        return moveDownAction;
    }

    @Nonnull
    @Override
    public MoveTopAction createMoveTopAction() {
        MoveTopAction moveTopAction = new MoveTopAction();
        moveTopAction.init(resourceBundle);
        return moveTopAction;
    }

    @Nonnull
    @Override
    public MoveBottomAction createMoveBottomAction() {
        MoveBottomAction moveBottomAction = new MoveBottomAction();
        moveBottomAction.init(resourceBundle);
        return moveBottomAction;
    }

    @Nonnull
    @Override
    public SequenceContribution createMoveUpContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createMoveUpAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return MoveUpAction.ACTION_ID;
            }
        };
    }

    @Nonnull
    @Override
    public SequenceContribution createMoveDownContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createMoveDownAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return MoveDownAction.ACTION_ID;
            }
        };
    }

    @Nonnull
    @Override
    public SequenceContribution createMoveTopContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createMoveTopAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return MoveTopAction.ACTION_ID;
            }
        };
    }

    @Nonnull
    @Override
    public SequenceContribution createMoveBottomContribution() {
        return new ActionSequenceContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return createMoveBottomAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return MoveBottomAction.ACTION_ID;
            }
        };
    }

    @Override
    public void registerToolBarContributions(ToolBarDefinitionManagement toolBarDefinition) {
        GroupSequenceContribution toolBarGroup = toolBarDefinition.registerToolBarGroup(GROUP_ID);
        SequenceContribution contribution = createMoveTopContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
        contribution = createMoveUpContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
        contribution = createMoveDownContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
        contribution = createMoveBottomContribution();
        toolBarDefinition.registerToolBarContribution(contribution);
        toolBarDefinition.registerToolBarRule(contribution, new GroupSequenceContributionRule(toolBarGroup));
    }
}
