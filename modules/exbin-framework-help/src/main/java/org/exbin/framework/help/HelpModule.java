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
package org.exbin.framework.help;

import java.awt.Insets;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.exbin.framework.App;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.help.api.HelpOpeningHandler;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Framework help module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HelpModule implements HelpModuleApi {

    private ResourceBundle resourceBundle;
    private HelpOpeningHandler helpOpeningHandler = null;

    public HelpModule() {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(HelpModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    @Override
    public JButton createHelpButton() {
        // TODO Change button shape to rounded
        getResourceBundle();
        JButton helpButton = new JButton();
        helpButton.setMargin(new Insets(2, 2, 2, 2));
        helpButton.setEnabled(hasOpeningHandler());
        helpButton.setIcon(new ImageIcon(getClass().getResource(resourceBundle.getString("helpAction.smallIcon"))));
        helpButton.setToolTipText(resourceBundle.getString("helpAction.toolTipText"));
        return helpButton;
    }

    @Override
    public void openHelp(HelpLink helpLink) {
        if (helpOpeningHandler != null) {
            helpOpeningHandler.openHelpLink(helpLink);
        }
    }

    @Nonnull
    @Override
    public Optional<HelpOpeningHandler> getHelpOpeningHandler() {
        return Optional.ofNullable(helpOpeningHandler);
    }

    @Override
    public void setHelpOpeningHandler(@Nullable HelpOpeningHandler helpOpeningHandler) {
        this.helpOpeningHandler = helpOpeningHandler;
    }

    @Override
    public boolean hasOpeningHandler() {
        return helpOpeningHandler != null;
    }
}
