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
package org.exbin.framework.options.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * Settings sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsSequenceOutput implements TreeContributionSequenceOutput {

    protected SettingsPageReceiver settingsPageReceiver;
    protected List<SettingsPathItem> path;
    protected final Map<String, SettingsPage> childPage = new HashMap<>();
    protected final SettingsPage settingsPage;

    public SettingsSequenceOutput(SettingsPageReceiver settingsPageReceiver, SettingsPage settingsPage, List<SettingsPathItem> path) {
        this.settingsPageReceiver = settingsPageReceiver;
        this.settingsPage = settingsPage;
        this.path = path;
    }

    @Override
    public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
        return true;
    }

    @Override
    public void add(SequenceContribution contribution) {
        if (contribution instanceof SettingsComponentContribution) {
            SettingsComponentContribution componentContribution = (SettingsComponentContribution) contribution;
            SettingsComponent component = componentContribution.getSettingsComponentProvider().createComponent();
            settingsPage.addComponent(component);
        } else if (contribution instanceof SettingsPageContribution) {
            String subPageId = ((SettingsPageContribution) contribution).getContributionId();
            SettingsPage subPage = childPage.remove(subPageId);
            if (subPage != null) {
                subPage.finish();
            }
        }
    }

    @Override
    public void addSeparator() {
        // TODO
    }

    @Nonnull
    @Override
    public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
        SettingsPageContribution pageContribution = (SettingsPageContribution) subContribution;
        SettingsPage subPage = new SettingsPage(pageContribution.getContributionId());
        List<SettingsPathItem> subPath = new ArrayList<>();
        subPath.addAll(path);
        subPath.add(new SettingsPathItem(subContribution.getContributionId(), pageContribution.getPageName()));
        settingsPageReceiver.addSettingsPage(subPage, subPath);
        childPage.put(pageContribution.getContributionId(), subPage);
        return new SettingsSequenceOutput(settingsPageReceiver, subPage, subPath);
    }

    @Override
    public boolean isEmpty() {
        return settingsPage.getComponentsCount() == 0;
    }
}
