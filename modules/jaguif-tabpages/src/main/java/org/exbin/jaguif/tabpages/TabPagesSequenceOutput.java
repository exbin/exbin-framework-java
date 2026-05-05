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
package org.exbin.jaguif.tabpages;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.tabpages.api.ActionTabPagesContribution;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPages;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;

/**
 * Tab pages sequence output.
 */
@ParametersAreNonnullByDefault
public class TabPagesSequenceOutput implements ContributionSequenceOutput {

    protected final TabPages tabPages;
    protected final ContextRegistration contextRegistration;
    protected final Map<SequenceContribution, TabPagesComponent> tabPagesItems = new HashMap<>();

    public TabPagesSequenceOutput(TabPages tabPages, ContextRegistration contextRegistration) {
        this.tabPages = tabPages;
        this.contextRegistration = contextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionTabPagesContribution) {
            Action action = ((ActionTabPagesContribution) itemContribution).createAction();
            throw new IllegalStateException();
        } else if (itemContribution instanceof ComponentTabPagesContribution) {
            TabPagesComponent tabPagesComponent = ((ComponentTabPagesContribution) itemContribution).createComponent();
            tabPagesItems.put(itemContribution, tabPagesComponent);
            return true;
        }

        throw new IllegalStateException("Unsupported contribution type");
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionTabPagesContribution) {
            throw new IllegalStateException();
        } else if (itemContribution instanceof ComponentTabPagesContribution) {
            TabPagesComponent tabPagesComponent = tabPagesItems.get(itemContribution);
            tabPages.addPage(tabPagesComponent);
            TabPagesSequenceOutput.finishTabPagesItem(tabPagesComponent, contextRegistration);
        }
    }

    @Override
    public void addSeparator() {
        // TODO tabPages.addSeparator();
    }

    @Override
    public boolean isEmpty() {
        return tabPages.getPagesCount() == 0;
    }

    protected static void finishTabPagesItem(TabPagesComponent tabPagesComponent, ContextRegistration contextRegistration) {
        if (tabPagesComponent == null) {
            return;
        }

        Object contextChange = tabPagesComponent.getValue(TabPagesComponent.KEY_CONTEXT_CHANGE);

        if (contextChange instanceof ContextChange) {
            contextRegistration.registerContextChange((ContextChange) contextChange);
        }
    }
}
