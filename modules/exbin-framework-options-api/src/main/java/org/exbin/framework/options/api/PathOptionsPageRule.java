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
package org.exbin.framework.options.api;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Options page tree path rule.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PathOptionsPageRule implements OptionsPageRule {

    private final List<OptionsPathItem> path;

    public PathOptionsPageRule(String parentPath) {
        path = new ArrayList<>();
        String[] pathParts = parentPath.split("/");
        for (String pathPart : pathParts) {
            if (!pathPart.isEmpty()) {
                path.add(new OptionsPathItem(pathPart, null));
            }
        }
//        if (optionsPage instanceof ComponentResourceProvider) {
//            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPage).getResourceBundle();
//            String optionsDefaultName = componentResourceBundle.getString("options.name");
//            String optionsDefaultCaption = componentResourceBundle.getString("options.caption");
//            optionsPath.add(new OptionsPathItem(optionsDefaultName, optionsDefaultCaption));
//        }
    }

    public PathOptionsPageRule(List<OptionsPathItem> path) {
        this.path = path;
    }

    @Nonnull
    public List<OptionsPathItem> getPath() {
        return path;
    }
}
