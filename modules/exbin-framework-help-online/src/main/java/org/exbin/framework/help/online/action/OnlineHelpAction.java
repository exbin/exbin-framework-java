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
package org.exbin.framework.help.online.action;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Online help action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OnlineHelpAction extends AbstractAction {

    public static final String ACTION_ID = "onlineHelpAction";

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OnlineHelpAction.class);
    private URL helpUrl = null;

    public OnlineHelpAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Optional<URL> getOnlineHelpUrl() {
        return Optional.ofNullable(helpUrl);
    }

    public void setOnlineHelpUrl(@Nullable URL helpUrl) {
        this.helpUrl = helpUrl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (helpUrl == null) {
            throw new IllegalStateException("Help URL was not set");
        }

        DesktopUtils.openDesktopURL(helpUrl.toExternalForm());
    }
}
