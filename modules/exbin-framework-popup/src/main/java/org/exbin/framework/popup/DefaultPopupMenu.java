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
package org.exbin.framework.popup;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.framework.popup.handler.EditorPanePopupHandler;
import org.exbin.framework.popup.handler.ListPopupHandler;
import org.exbin.framework.popup.handler.TablePopupHandler;
import org.exbin.framework.popup.handler.TextComponentPopupHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Utilities for default menu generation.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultPopupMenu {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(DefaultPopupMenu.class);

    public static final String POPUP_COPY_ACTION_ID = "popupCopyAction";
    public static final String POPUP_COPY_TEXT_ACTION_ID = "popupCopyTextAction";
    public static final String POPUP_COPY_LINK_ACTION_ID = "popupCopyLinkAction";
    public static final String POPUP_COPY_IMAGE_ACTION_ID = "popupCopyImageAction";
    public static final String POPUP_CUT_ACTION_ID = "popupCutAction";
    public static final String POPUP_PASTE_ACTION_ID = "popupPasteAction";
    public static final String POPUP_DELETE_ACTION_ID = "popupDeleteAction";
    public static final String POPUP_SELECT_ALL_ACTION_ID = "popupSelectAllAction";
    public static final String POPUP_OPEN_LINK_ACTION_ID = "popupOpenLinkAction";
    public static final String POPUP_COPY_TEXT_ACTION_NAME = "copy-text";
    public static final String POPUP_COPY_LINK_ACTION_NAME = "copy-link";
    public static final String POPUP_COPY_IMAGE_ACTION_NAME = "copy-image";
    public static final String POPUP_OPEN_LINK_ACTION_NAME = "open-link";

    private ActionMap defaultTextActionMap;
    private JPopupMenu defaultPopupMenu;
    private JPopupMenu defaultEditPopupMenu;
    private JPopupMenu defaultLinkPopupMenu;
    private DefaultPopupClipboardAction defaultCutAction;
    private DefaultPopupClipboardAction defaultCopyAction;
    private DefaultPopupClipboardAction defaultPasteAction;
    private DefaultPopupClipboardAction defaultDeleteAction;
    private DefaultPopupClipboardAction defaultSelectAllAction;
    private DefaultPopupClipboardAction[] defaultTextActions;
    private Action copyLinkAction;
    private Action openLinkAction;

    private final List<ComponentPopupEventDispatcher> clipboardEventDispatchers = new ArrayList<>();

    private static DefaultPopupMenu instance = null;

    private DefaultPopupMenu() {
    }

    @Nonnull
    public static synchronized DefaultPopupMenu getInstance() {
        if (instance == null) {
            instance = new DefaultPopupMenu();
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu();
        defaultPopupMenu.registerToEventQueue();
    }

    /**
     * Registers default popup menu to AWT.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    public static void register(ResourceBundle resourceBundle, Class resourceClass) {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu(resourceBundle, resourceClass);
        defaultPopupMenu.registerToEventQueue();
    }

    private void registerToEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new PopupEventQueue());
    }

    private void initDefaultPopupMenu() {
        initDefaultPopupMenu(resourceBundle, this.getClass());
    }

    private void initDefaultPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        defaultTextActionMap = new ActionMap();
        defaultCutAction = new DefaultPopupClipboardAction(DefaultEditorKit.cutAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCut();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCutAction, resourceBundle, resourceClass, POPUP_CUT_ACTION_ID);
        defaultCutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        defaultCutAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), defaultCutAction);

        defaultCopyAction = new DefaultPopupClipboardAction(DefaultEditorKit.copyAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCopy();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCopyAction, resourceBundle, resourceClass, POPUP_COPY_ACTION_ID);
        defaultCopyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        defaultCopyAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), defaultCopyAction);

        defaultPasteAction = new DefaultPopupClipboardAction(DefaultEditorKit.pasteAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performPaste();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable());
            }
        };
        ActionUtils.setupAction(defaultPasteAction, resourceBundle, resourceClass, POPUP_PASTE_ACTION_ID);
        defaultPasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        defaultPasteAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), defaultPasteAction);

        defaultDeleteAction = new DefaultPopupClipboardAction(DefaultEditorKit.deleteNextCharAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performDelete();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.canDelete() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultDeleteAction, resourceBundle, resourceClass, POPUP_DELETE_ACTION_ID);
        defaultDeleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        defaultDeleteAction.setEnabled(false);
        defaultTextActionMap.put("delete", defaultDeleteAction);

        defaultSelectAllAction = new DefaultPopupClipboardAction(DefaultEditorKit.selectAllAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performSelectAll();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.canSelectAll());
            }
        };
        ActionUtils.setupAction(defaultSelectAllAction, resourceBundle, resourceClass, POPUP_SELECT_ALL_ACTION_ID);
        defaultSelectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        defaultTextActionMap.put("selectAll", defaultSelectAllAction);

        DefaultPopupClipboardAction[] actions = {defaultCutAction, defaultCopyAction, defaultPasteAction, defaultDeleteAction, defaultSelectAllAction};
        defaultTextActions = actions;

        copyLinkAction = new DefaultPopupLinkAction(POPUP_COPY_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkHandler.performCopyLink();
            }
        };
        ActionUtils.setupAction(copyLinkAction, resourceBundle, resourceClass, POPUP_COPY_LINK_ACTION_ID);
        openLinkAction = new DefaultPopupLinkAction(POPUP_OPEN_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkHandler.performOpenLink();
            }
        };
        ActionUtils.setupAction(openLinkAction, resourceBundle, resourceClass, POPUP_OPEN_LINK_ACTION_ID);

        buildDefaultPopupMenu();
        buildDefaultEditPopupMenu();
    }

    private void buildDefaultPopupMenu() {
        defaultPopupMenu = new JPopupMenu();

        defaultPopupMenu.setName("defaultPopupMenu"); // NOI18N
        fillDefaultPopupMenu(defaultPopupMenu, -1);
    }

    private void buildDefaultEditPopupMenu() {
        defaultEditPopupMenu = new JPopupMenu();

        defaultEditPopupMenu.setName("defaultEditPopupMenu"); // NOI18N
        fillDefaultEditPopupMenu(defaultEditPopupMenu, -1);
    }

    public void fillDefaultPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicEditPopupCopyMenuItem"); // NOI18N
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicEditPopupSelectAllMenuItem"); // NOI18N

        if (position >= 0) {
            popupMenu.insert(basicPopupCopyMenuItem, position);
            popupMenu.insert(new JPopupMenu.Separator(), position + 1);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 2);
        } else {
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    public void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCutMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupPasteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupDeleteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCutMenuItem.setAction(defaultCutAction);
        basicPopupCutMenuItem.setName("basicPopupCutMenuItem");
        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicPopupCopyMenuItem");
        basicPopupPasteMenuItem.setAction(defaultPasteAction);
        basicPopupPasteMenuItem.setName("basicPopupPasteMenuItem");
        basicPopupDeleteMenuItem.setAction(defaultDeleteAction);
        basicPopupDeleteMenuItem.setName("basicPopupDeleteMenuItem");
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicPopupSelectAllMenuItem");

        if (position >= 0) {
            popupMenu.insert(basicPopupCutMenuItem, position);
            popupMenu.insert(basicPopupCopyMenuItem, position + 1);
            popupMenu.insert(basicPopupPasteMenuItem, position + 2);
            popupMenu.insert(basicPopupDeleteMenuItem, position + 3);
            popupMenu.insert(new JPopupMenu.Separator(), position + 4);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 5);
        } else {
            popupMenu.add(basicPopupCutMenuItem);
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.add(basicPopupPasteMenuItem);
            popupMenu.add(basicPopupDeleteMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    public void addClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.add(dispatcher);
    }

    public void removeClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.remove(dispatcher);
    }

    @ParametersAreNonnullByDefault
    public class PopupEventQueue extends EventQueue {

        @Override
        protected void dispatchEvent(AWTEvent event) {
            super.dispatchEvent(event);

            if (event.getID() == MouseEvent.MOUSE_RELEASED || event.getID() == MouseEvent.MOUSE_PRESSED) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.isPopupTrigger()) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return;
                    }

                    for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
                        if (dispatcher.dispatchMouseEvent(mouseEvent)) {
                            return;
                        }
                    }

                    Component component = getSource(mouseEvent);
                    if (component instanceof JViewport) {
                        component = ((JViewport) component).getView();
                    }

                    if (component instanceof JEditorPane) {
                        activateMousePopup(mouseEvent, component, new EditorPanePopupHandler((JEditorPane) component));
                    } else if (component instanceof JTextComponent) {
                        activateMousePopup(mouseEvent, component, new TextComponentPopupHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        activateMousePopup(mouseEvent, component, new ListPopupHandler((JList) component));
                    } else if (component instanceof JTable) {
                        activateMousePopup(mouseEvent, component, new TablePopupHandler((JTable) component));
                    }
                }
            } else if (event.getID() == KeyEvent.KEY_PRESSED) {
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return;
                    }

                    for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
                        if (dispatcher.dispatchKeyEvent(keyEvent)) {
                            return;
                        }
                    }

                    Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                    if (component instanceof JTextComponent) {
                        Point point;
                        try {
                            Rectangle relativeRect = ((JTextComponent) component).modelToView(((JTextComponent) component).getCaretPosition());
                            point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                        } catch (BadLocationException ex) {
                            point = null;
                        }
                        activateKeyPopup(component, point, new TextComponentPopupHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        Point point = null;
                        int selectedIndex = ((JList) component).getSelectedIndex();
                        if (selectedIndex >= 0) {
                            Rectangle cellBounds = ((JList) component).getCellBounds(selectedIndex, selectedIndex);
                            point = new Point(component.getWidth() / 2, cellBounds.y);
                        }
                        activateKeyPopup(component, point, new ListPopupHandler((JList) component));
                    } else if (component instanceof JTable) {
                        Point point = null;
                        int selectedRow = ((JTable) component).getSelectedRow();
                        if (selectedRow >= 0) {
                            int selectedColumn = ((JTable) component).getSelectedColumn();
                            if (selectedColumn < -1) {
                                selectedColumn = 0;
                            }
                            Rectangle cellBounds = ((JTable) component).getCellRect(selectedRow, selectedColumn, false);
                            point = new Point(cellBounds.x, cellBounds.y);
                        }
                        activateKeyPopup(component, point, new TablePopupHandler((JTable) component));
                    }
                }
            }
        }

        private void activateMousePopup(MouseEvent mouseEvent, Component component, ClipboardActionsHandler clipboardHandler) {
            for (Object action : defaultTextActions) {
                ((DefaultPopupClipboardAction) action).setClipboardHandler(clipboardHandler);
            }

            Point point = mouseEvent.getLocationOnScreen();
            Point locationOnScreen = component.getLocationOnScreen();
            point.translate(-locationOnScreen.x, -locationOnScreen.y);

            showPopupMenu(component, point, clipboardHandler);
        }

        private void activateKeyPopup(Component component, Point point, ClipboardActionsHandler clipboardHandler) {
            for (Object action : defaultTextActions) {
                ((DefaultPopupClipboardAction) action).setClipboardHandler(clipboardHandler);
            }

            if (point == null) {
                if (component.getParent() instanceof ScrollPane) {
                    // TODO
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                } else {
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                }
            }

            showPopupMenu(component, point, clipboardHandler);
        }

        private void showPopupMenu(Component component, Point point, ClipboardActionsHandler handler) {
            if (handler instanceof LinkActionsHandler) {
                // TODO
            }

            boolean editable = handler.isEditable();
            if (editable) {
                defaultEditPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            } else {
                defaultPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            }
        }

        @Nullable
        private Component getSource(MouseEvent e) {
            return SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Clipboard action for default popup menu.
     */
    @ParametersAreNonnullByDefault
    private static abstract class DefaultPopupClipboardAction extends AbstractAction {

        protected ClipboardActionsHandler clipboardHandler;

        public DefaultPopupClipboardAction(String name) {
            super(name);
        }

        public void setClipboardHandler(ClipboardActionsHandler clipboardHandler) {
            this.clipboardHandler = clipboardHandler;
            postTextComponentInitialize();
        }

        protected abstract void postTextComponentInitialize();
    }

    /**
     * Link action for default popup menu.
     */
    @ParametersAreNonnullByDefault
    private static abstract class DefaultPopupLinkAction extends AbstractAction {

        protected LinkActionsHandler linkHandler;

        public DefaultPopupLinkAction(String name) {
            super(name);
        }

        public void setLinkHandler(LinkActionsHandler linkHandler) {
            this.linkHandler = linkHandler;
        }
    }
}