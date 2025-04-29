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
package org.exbin.framework.frame.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.frame.ApplicationFrame;

/**
 * Basic frame actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameActions {

    private ResourceBundle resourceBundle;

    public FrameActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public ViewToolBarAction createViewToolBarAction() {
        ViewToolBarAction viewToolBarAction = new ViewToolBarAction();
        viewToolBarAction.setup(resourceBundle);
        return viewToolBarAction;
    }

    @Nonnull
    public ViewToolBarCaptionsAction createViewToolBarCaptionsAction() {
        ViewToolBarCaptionsAction viewToolBarCaptionsAction = new ViewToolBarCaptionsAction();
        viewToolBarCaptionsAction.setup(resourceBundle);
        return viewToolBarCaptionsAction;
    }

    @Nonnull
    public ViewStatusBarAction createViewStatusBarAction() {
        ViewStatusBarAction viewStatusBarAction = new ViewStatusBarAction();
        viewStatusBarAction.setup(resourceBundle);
        return viewStatusBarAction;
    }

    @ParametersAreNonnullByDefault
    public class ViewToolBarAction extends AbstractAction {

        public static final String ACTION_ID = "viewToolBarAction";

        private ApplicationFrame frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(ApplicationFrame.class, (instance) -> {
                    frame = instance;
                    setEnabled(frame != null);
                    if (frame != null) {
                        putValue(Action.SELECTED_KEY, frame.isToolBarVisible());
                    }
                });
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setToolBarVisible(((JMenuItem) source).isSelected());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public class ViewToolBarCaptionsAction extends AbstractAction {

        public static final String ACTION_ID = "viewToolBarCaptionsAction";

        private ApplicationFrame frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(ApplicationFrame.class, (instance) -> {
                    frame = instance;
                    setEnabled(frame != null);
                    if (frame != null) {
                        putValue(Action.SELECTED_KEY, frame.isToolBarCaptionsVisible());
                    }
                });
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setToolBarCaptionsVisible(((JMenuItem) source).isSelected());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public class ViewStatusBarAction extends AbstractAction {

        public static final String ACTION_ID = "viewStatusBarAction";

        private ApplicationFrame frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(ApplicationFrame.class, (instance) -> {
                    frame = instance;
                    setEnabled(frame != null);
                    if (frame != null) {
                        putValue(Action.SELECTED_KEY, frame.isStatusBarVisible());
                    }
                });
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setStatusBarVisible(((JMenuItem) source).isSelected());
            }
        }
    }
}
