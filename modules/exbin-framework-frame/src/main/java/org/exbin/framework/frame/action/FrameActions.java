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
import org.exbin.framework.frame.XBApplicationFrame;
import org.exbin.framework.utils.ActionUtils;

/**
 * Basic frame actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameActions {

    public static final String VIEW_TOOL_BAR_ACTION_ID = "viewToolBarAction";
    public static final String VIEW_TOOL_BAR_CAPTIONS_ACTION_ID = "viewToolBarCaptionsAction";
    public static final String VIEW_STATUS_BAR_ACTION_ID = "viewStatusBarAction";

    private ResourceBundle resourceBundle;
    private XBApplicationFrame frame;

    private AbstractAction viewToolBarAction;
    private AbstractAction viewStatusBarAction;
    private AbstractAction viewToolBarCaptionsAction;

    public FrameActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setApplicationFrame(XBApplicationFrame frame) {
        this.frame = frame;
    }

    public void notifyFrameUpdated() {
        if (viewToolBarAction != null) {
            viewToolBarAction.putValue(Action.SELECTED_KEY, frame.isToolBarVisible());
        }
        if (viewToolBarCaptionsAction != null) {
            viewToolBarCaptionsAction.putValue(Action.SELECTED_KEY, frame.isToolBarCaptionsVisible());
        }
        if (viewStatusBarAction != null) {
            viewStatusBarAction.putValue(Action.SELECTED_KEY, frame.isStatusBarVisible());
        }
    }

    @Nonnull
    public Action getViewToolBarAction() {
        if (viewToolBarAction == null) {
            viewToolBarAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source instanceof JMenuItem) {
                        frame.setToolBarVisible(((JMenuItem) source).isSelected());
                    }
                }
            };
            ActionUtils.setupAction(viewToolBarAction, resourceBundle, VIEW_TOOL_BAR_ACTION_ID);
            viewToolBarAction.putValue(Action.SELECTED_KEY, true);
            viewToolBarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        }
        return viewToolBarAction;
    }

    @Nonnull
    public Action getViewToolBarCaptionsAction() {
        if (viewToolBarCaptionsAction == null) {
            viewToolBarCaptionsAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source instanceof JMenuItem) {
                        frame.setToolBarCaptionsVisible(((JMenuItem) source).isSelected());
                    }
                }
            };
            ActionUtils.setupAction(viewToolBarCaptionsAction, resourceBundle, VIEW_TOOL_BAR_CAPTIONS_ACTION_ID);
            viewToolBarCaptionsAction.putValue(Action.SELECTED_KEY, true);
            viewToolBarCaptionsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        }
        return viewToolBarCaptionsAction;
    }

    @Nonnull
    public Action getViewStatusBarAction() {
        if (viewStatusBarAction == null) {
            viewStatusBarAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source instanceof JMenuItem) {
                        frame.setStatusBarVisible(((JMenuItem) source).isSelected());
                    }
                }
            };
            ActionUtils.setupAction(viewStatusBarAction, resourceBundle, VIEW_STATUS_BAR_ACTION_ID);
            viewStatusBarAction.putValue(Action.SELECTED_KEY, true);
            viewStatusBarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        }
        return viewStatusBarAction;
    }
}
