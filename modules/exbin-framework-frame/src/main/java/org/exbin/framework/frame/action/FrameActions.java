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
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.frame.ApplicationFrame;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.ContextFrame;
import org.exbin.framework.context.api.ContextChangeRegistration;

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
    public static class ViewToolBarAction extends AbstractAction {

        public static final String ACTION_ID = "viewToolBarAction";

        protected ApplicationFrameHandler frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
                registrar.registerUpdateListener(ContextFrame.class, (instance) -> {
                    updateByContext(instance);
                });
                registrar.registerStateChangeListener(ContextFrame.class, (instance, message) -> {
                    if (ContextFrame.ChangeType.BARS_LAYOUT_CHANGE.equals(message)) {
                        updateByContext(instance);
                    }
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setToolBarVisible(((JMenuItem) source).isSelected());
            }
        }

        public void updateByContext(ContextFrame context) {
            this.frame = context instanceof ApplicationFrameHandler ? (ApplicationFrame) context : null;
            setEnabled(frame != null);
            if (frame != null) {
                putValue(Action.SELECTED_KEY, frame.isToolBarVisible());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class ViewToolBarCaptionsAction extends AbstractAction {

        public static final String ACTION_ID = "viewToolBarCaptionsAction";

        protected ApplicationFrameHandler frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
                registrar.registerUpdateListener(ContextFrame.class, (instance) -> {
                    updateByContext(instance);
                });
                registrar.registerStateChangeListener(ContextFrame.class, (instance, message) -> {
                    if (ContextFrame.ChangeType.BARS_LAYOUT_CHANGE.equals(message)) {
                        updateByContext(instance);
                    }
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setToolBarCaptionsVisible(((JMenuItem) source).isSelected());
            }
        }

        public void updateByContext(ContextFrame context) {
            this.frame = context instanceof ApplicationFrameHandler ? (ApplicationFrame) context : null;
            setEnabled(frame != null);
            if (frame != null) {
                putValue(Action.SELECTED_KEY, frame.isToolBarCaptionsVisible());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class ViewStatusBarAction extends AbstractAction {

        public static final String ACTION_ID = "viewStatusBarAction";

        protected ApplicationFrameHandler frame;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.SELECTED_KEY, true);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
                registrar.registerUpdateListener(ContextFrame.class, (instance) -> {
                    updateByContext(instance);
                });
                registrar.registerStateChangeListener(ContextFrame.class, (instance, message) -> {
                    if (ContextFrame.ChangeType.BARS_LAYOUT_CHANGE.equals(message)) {
                        updateByContext(instance);
                    }
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JMenuItem) {
                frame.setStatusBarVisible(((JMenuItem) source).isSelected());
            }
        }

        public void updateByContext(ContextFrame context) {
            this.frame = context instanceof ApplicationFrameHandler ? (ApplicationFrame) context : null;
            setEnabled(frame != null);
            if (frame != null) {
                putValue(Action.SELECTED_KEY, frame.isStatusBarVisible());
            }
        }
    }
}
