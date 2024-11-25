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
package org.exbin.framework.editor.text;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.text.gui.AddEncodingPanel;
import org.exbin.framework.editor.text.options.gui.TextEncodingPanel;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.handler.OptionsControlHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.editor.text.service.TextEncodingService;
import org.exbin.framework.editor.text.service.impl.TextEncodingServiceImpl;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Encodings handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EncodingsHandler {

    public static final String MANAGE_ENCODINGS_ACTION_ID = "manageEncodingsAction"; //NOI18N
    public static final String ENCODING_UTF8 = "UTF-8"; //NOI18N

    private final ResourceBundle resourceBundle;

    private ActionListener encodingActionListener;
    private ButtonGroup encodingButtonGroup;
    private javax.swing.JMenu toolsEncodingMenu;
    private javax.swing.JRadioButtonMenuItem utfEncodingRadioButtonMenuItem;
    private ActionListener utfEncodingActionListener;
    private Component parentComponent = null;

    private Action manageEncodingsAction;
    private TextEncodingPreferences preferences;

    private final TextEncodingService textEncodingService = new TextEncodingServiceImpl();

    public EncodingsHandler() {
        resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EncodingsHandler.class);
    }

    public void init() {
        encodingButtonGroup = new ButtonGroup();

        encodingActionListener = (ActionEvent e) -> {
            textEncodingService.setSelectedEncoding(((JRadioButtonMenuItem) e.getSource()).getText());
        };

        utfEncodingRadioButtonMenuItem = UiUtils.createRadioButtonMenuItem();
        utfEncodingRadioButtonMenuItem.setSelected(true);
        utfEncodingRadioButtonMenuItem.setText(resourceBundle.getString("defaultEncoding.text"));
        utfEncodingRadioButtonMenuItem.setToolTipText(MessageFormat.format(resourceBundle.getString("switchEncoding.toolTipText"), new Object[]{ENCODING_UTF8}));
        utfEncodingActionListener = (java.awt.event.ActionEvent evt) -> textEncodingService.setSelectedEncoding(ENCODING_UTF8);
        utfEncodingRadioButtonMenuItem.addActionListener(utfEncodingActionListener);

        encodingButtonGroup.add(utfEncodingRadioButtonMenuItem);
        manageEncodingsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                final TextEncodingPanel textEncodingPanel = new TextEncodingPanel();
                textEncodingPanel.setPreferredSize(new Dimension(536, 358));
                textEncodingPanel.setEncodingList(textEncodingService.getEncodings());
                final OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
                JPanel dialogPanel = windowModule.createDialogPanel(textEncodingPanel, optionsControlPanel);
                final WindowHandler dialog = windowModule.createDialog(dialogPanel, optionsControlPanel);
                windowModule.addHeaderPanel(dialog.getWindow(), textEncodingPanel.getClass(), textEncodingPanel.getResourceBundle());
                windowModule.setWindowTitle(dialog, textEncodingPanel.getResourceBundle());
                optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        textEncodingService.setEncodings(textEncodingPanel.getEncodingList());
                        rebuildEncodings();
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            preferences.setEncodings(textEncodingPanel.getEncodingList());
                        }
                    }

                    dialog.close();
                    dialog.dispose();
                });
                textEncodingPanel.setAddEncodingsOperation((List<String> usedEncodings, TextEncodingPanel.EncodingsUpdate encodingsUpdate) -> {
                    final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                    addEncodingPanel.setUsedEncodings(usedEncodings);
                    DefaultControlPanel encodingsControlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                    final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, encodingsControlPanel);
                    windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingPanel.getResourceBundle());
                    windowModule.setWindowTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());

                    encodingsControlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType == DefaultControlHandler.ControlActionType.OK) {
                            encodingsUpdate.update(addEncodingPanel.getEncodings());
                        }

                        addEncodingDialog.close();
                        addEncodingDialog.dispose();
                    });
                    addEncodingDialog.showCentered(addEncodingPanel);
                });
                dialog.showCentered(parentComponent);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(manageEncodingsAction, resourceBundle, MANAGE_ENCODINGS_ACTION_ID);
        manageEncodingsAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);

        toolsEncodingMenu = UiUtils.createMenu();
        toolsEncodingMenu.addSeparator();
        toolsEncodingMenu.add(actionModule.actionToMenuItem(manageEncodingsAction));
        toolsEncodingMenu.setText(resourceBundle.getString("toolsEncodingMenu.text"));
        toolsEncodingMenu.setToolTipText(resourceBundle.getString("toolsEncodingMenu.shortDescription"));
        EncodingsHandler.this.rebuildEncodings();
    }

    public void setTextEncodingStatus(TextEncodingStatusApi textEncodingStatus) {
        textEncodingService.setTextEncodingStatus(textEncodingStatus);
    }

    public void setEncodingChangeListener(TextEncodingService.EncodingChangeListener listener) {
        textEncodingService.setEncodingChangeListener(listener);
    }

    @Nonnull
    public String getSelectedEncoding() {
        return textEncodingService.getSelectedEncoding();
    }

    public void setSelectedEncoding(String encoding) {
        textEncodingService.setSelectedEncoding(encoding);
    }

    @Nonnull
    public TextEncodingService getTextEncodingService() {
        return textEncodingService;
    }

    @Nonnull
    public JMenu getToolsEncodingMenu() {
        return toolsEncodingMenu;
    }

    public void rebuildEncodings() {
        for (int i = toolsEncodingMenu.getItemCount() - 3; i >= 0; i--) {
            encodingButtonGroup.remove(toolsEncodingMenu.getItem(i));
            toolsEncodingMenu.remove(i);
        }

        List<String> encodings = textEncodingService.getEncodings();
        if (encodings.isEmpty()) {
            toolsEncodingMenu.add(utfEncodingRadioButtonMenuItem, 0);
            textEncodingService.setSelectedEncoding(ENCODING_UTF8);
            utfEncodingRadioButtonMenuItem.setSelected(true);
        } else {
            int selectedEncodingIndex = encodings.indexOf(textEncodingService.getSelectedEncoding());
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = UiUtils.createRadioButtonMenuItem();
                item.setText(encoding);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(MessageFormat.format(resourceBundle.getString("switchEncoding.toolTipText"), new Object[]{encoding}));
                toolsEncodingMenu.add(item, index);
                encodingButtonGroup.add(item);
                if (index == selectedEncodingIndex) {
                    item.setSelected(true);
                }
            }
        }
    }

    private void updateEncodingsSelection(int menuIndex) {
        if (menuIndex > 0) {
            menuIndex++;
        }
        JMenuItem item = toolsEncodingMenu.getItem(menuIndex);
        item.setSelected(true);
    }

    public void loadFromPreferences(TextEncodingPreferences preferences) {
        this.preferences = preferences;
        textEncodingService.loadFromPreferences(preferences);
        rebuildEncodings();
    }

    public void cycleEncodings() {
        int menuIndex = 0;
        List<String> encodings = textEncodingService.getEncodings();
        if (!encodings.isEmpty()) {
            int selectedEncodingIndex = encodings.indexOf(textEncodingService.getSelectedEncoding());
            if (selectedEncodingIndex < 0 || selectedEncodingIndex == encodings.size() - 1) {
                textEncodingService.setSelectedEncoding(encodings.get(0));
            } else {
                textEncodingService.setSelectedEncoding(encodings.get(selectedEncodingIndex + 1));
                menuIndex = selectedEncodingIndex;
            }
        }

        updateEncodingsSelection(menuIndex);
    }

    public void popupEncodingsMenu(MouseEvent mouseEvent) {
        JPopupMenu popupMenu = UiUtils.createPopupMenu();

        List<String> encodings = textEncodingService.getEncodings();
        String selectedEncoding = textEncodingService.getSelectedEncoding();
        if (encodings.isEmpty()) {
            JRadioButtonMenuItem utfEncoding = UiUtils.createRadioButtonMenuItem();
            utfEncoding.setText(resourceBundle.getString("defaultEncoding.text"));
            utfEncoding.setSelected(ENCODING_UTF8.equals(selectedEncoding));
            utfEncoding.setToolTipText(MessageFormat.format(resourceBundle.getString("switchEncoding.toolTipText"), new Object[]{ENCODING_UTF8}));
            utfEncoding.addActionListener(utfEncodingActionListener);
            popupMenu.add(utfEncoding);
        } else {
            int selectedEncodingIndex = encodings.indexOf(selectedEncoding);
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = UiUtils.createRadioButtonMenuItem();
                item.setText(encoding);
                item.setSelected(index == selectedEncodingIndex);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(MessageFormat.format(resourceBundle.getString("switchEncoding.toolTipText"), new Object[]{encoding}));
                popupMenu.add(item, index);
            }
        }

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        popupMenu.add(new JSeparator());
        popupMenu.add(actionModule.actionToMenuItem(manageEncodingsAction));

        popupMenu.show((Component) mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
    }

    public void setEncodings(List<String> encodings) {
        textEncodingService.setEncodings(encodings);
    }

    public void setParentComponent(Component component) {
        parentComponent = component;
    }
}
