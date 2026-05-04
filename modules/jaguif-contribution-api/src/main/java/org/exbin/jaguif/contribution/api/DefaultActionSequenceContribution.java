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
package org.exbin.jaguif.contribution.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Default sequence action item contribution.
 */
@ParametersAreNonnullByDefault
public class DefaultActionSequenceContribution implements ActionSequenceContribution {

    protected final String contributionId;
    protected final ActionCreator actionCreator;

    public DefaultActionSequenceContribution(String contributionId, ActionCreator actionCreator) {
        this.contributionId = contributionId;
        this.actionCreator = actionCreator;
    }

    @Nonnull
    @Override
    public Action createAction() {
        return actionCreator.createAction();
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return contributionId;
    }

    public interface ActionCreator {

        @Nonnull
        Action createAction();
    }
}
