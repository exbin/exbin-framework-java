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
package org.exbin.framework.frame;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.frame.api.ApplicationExitListener;
import org.exbin.framework.frame.api.ApplicationFrameHandler;

/**
 * Application exit handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ApplicationExitHandler {

    private final List<ApplicationExitListener> listeners = new ArrayList<>();

    public ApplicationExitHandler() {
    }

    public void addListener(ApplicationExitListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ApplicationExitListener listener) {
        listeners.remove(listener);
    }

    public void executeExit(ApplicationFrameHandler frameHandler) {
        for (ApplicationExitListener listener : listeners) {
            boolean canContinue = listener.processExit(frameHandler);
            if (!canContinue) {
                return;
            }
        }

        System.exit(0);
    }
    
    public boolean canExit(ApplicationFrameHandler frameHandler) {
        for (ApplicationExitListener listener : listeners) {
            boolean canContinue = listener.processExit(frameHandler);
            if (!canContinue) {
                return false;
            }
        }

        return true;
    }
}
