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
package org.exbin.framework.utils;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ActionMap;
import javax.swing.text.JTextComponent;

/**
 * Some simple static methods usable for actions, menus and toolbars.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionUtils {

    private ActionUtils() {
    }

    /**
     * Returns platform specific down mask filter.
     *
     * @return down mask for meta keys
     */
    @SuppressWarnings("deprecation")
    public static int getMetaMask() {
        try {
            switch (java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
                case java.awt.Event.CTRL_MASK:
                    return KeyEvent.CTRL_DOWN_MASK;
                case java.awt.Event.META_MASK:
                    return KeyEvent.META_DOWN_MASK;
                case java.awt.Event.SHIFT_MASK:
                    return KeyEvent.SHIFT_DOWN_MASK;
                case java.awt.Event.ALT_MASK:
                    return KeyEvent.ALT_DOWN_MASK;
                default:
                    return KeyEvent.CTRL_DOWN_MASK;
            }
        } catch (java.awt.HeadlessException ex) {
            return KeyEvent.CTRL_DOWN_MASK;
        }
    }

    /**
     * This method was lifted from JTextComponent.java.
     *
     * @return KeyEvent modifier mask
     */
    private static int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiersEx();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }
        return modifiers;
    }

    /**
     * Invokes action of given name on text component.
     *
     * @param textComponent component
     * @param actionName action name
     */
    public static void invokeTextAction(JTextComponent textComponent, String actionName) {
        ActionMap textActionMap = textComponent.getActionMap().getParent();
        long eventTime = EventQueue.getMostRecentEventTime();
        int eventMods = getCurrentEventModifiers();
        ActionEvent actionEvent = new ActionEvent(textComponent, ActionEvent.ACTION_PERFORMED, actionName, eventTime, eventMods);
        textActionMap.get(actionName).actionPerformed(actionEvent);
    }
}
