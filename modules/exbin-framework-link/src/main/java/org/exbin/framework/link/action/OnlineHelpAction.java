/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.link.action;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.BareBonesBrowserLaunch;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Online help action.
 *
 * @version 0.2.0 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OnlineHelpAction extends AbstractAction {

    public static final String ACTION_ID = "onlineHelpAction";

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(OnlineHelpAction.class);
    private URL helpUrl = null;

    public OnlineHelpAction() {
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nullable
    public URL getOnlineHelpUrl() {
        return helpUrl;
    }

    public void setOnlineHelpUrl(@Nullable URL helpUrl) {
        this.helpUrl = helpUrl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BareBonesBrowserLaunch.openDesktopURL(helpUrl.toExternalForm());
    }
}
