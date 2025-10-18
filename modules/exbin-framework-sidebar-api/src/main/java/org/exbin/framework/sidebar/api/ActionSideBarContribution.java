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
package org.exbin.framework.sidebar.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.contribution.api.ItemSequenceContribution;

/**
 * Action side bar item contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionSideBarContribution implements ItemSequenceContribution {

    protected final Action action;
    protected  JComponent component;

    public ActionSideBarContribution(Action action) {
        this.action = action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return (String) action.getValue(ActionConsts.ACTION_ID);
    }

    @Nonnull
    public Action getAction() {
        return action;
    }

    @Nullable
    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }
}
