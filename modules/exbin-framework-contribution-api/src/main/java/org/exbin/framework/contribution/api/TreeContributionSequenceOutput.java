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
package org.exbin.framework.contribution.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;

/**
 * Interface for tree contribution sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface TreeContributionSequenceOutput extends Module {

    /**
     * Initializes next sequence item.
     *
     * @param itemContribution item contribution
     * @param definitionId definition id
     * @param subId sub contribution id
     * @return true if valid
     */
    boolean initItem(ItemSequenceContribution itemContribution, String definitionId, String subId);

    /**
     * Reports next sequence item.
     *
     * @param itemContribution item contribution
     */
    void add(ItemSequenceContribution itemContribution);

    /**
     * Initializes next sub sequence.
     *
     * @param itemContribution item contribution
     * @param definitionId definition id
     * @param subId sub contribution id
     * @return true if valid
     */
    boolean initItem(SubSequenceContribution itemContribution, String definitionId, String subId);

    /**
     * Reports next sub sequence.
     *
     * @param itemContribution item contribution
     */
    void add(SubSequenceContribution itemContribution);

    /**
     * Reports next separator.
     */
    void addSeparator();

    /**
     * Checks whether sequence is empty.
     *
     * @return true if empty
     */
    boolean isEmpty();
}
