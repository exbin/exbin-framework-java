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
package org.exbin.jaguif.operation.undo.contribution;

import javax.annotation.Nonnull;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.operation.undo.action.RedoAction;
import org.exbin.jaguif.operation.undo.api.OperationUndoModuleApi;

/**
 * Redo contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class RedoContribution implements ActionSequenceContribution {

    public static final String CONTRIBUTION_ID = "editRedo";

    @Nonnull
    @Override
    public Action createAction() {
        RedoAction action = new RedoAction();
        OperationUndoModuleApi operationUndoModule = App.getModule(OperationUndoModuleApi.class);
        action.init(operationUndoModule.getResourceBundle());
        return action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return CONTRIBUTION_ID;
    }
}
