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
package org.exbin.framework.search.action;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Find/replace actions for binary search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        editFindAction.setup(resourceBundle);
        return editFindAction;
    }

    @Nonnull
    public Action createEditFindNextAction() {
        EditFindNextAction editFindNextAction = new EditFindNextAction();
        editFindNextAction.setup(resourceBundle);
        return editFindNextAction;
    }

    @Nonnull
    public Action createEditFindPreviousAction() {
        EditFindPreviousAction editFindPreviousAction = new EditFindPreviousAction();
        editFindPreviousAction.setup(resourceBundle);
        return editFindPreviousAction;
    }

    @Nonnull
    public Action createEditReplaceAction() {
        EditReplaceAction editReplaceAction = new EditReplaceAction();
        editReplaceAction.setup(resourceBundle);
        return editReplaceAction;
    }
}
