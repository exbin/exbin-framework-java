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
package org.exbin.framework.text.encoding;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.text.encoding.action.ManageEncodingsAction;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.UiUtils;

/**
 * Encodings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EncodingsManager {

    public static final String ENCODING_UTF8 = "UTF-8"; //NOI18N

    private final ResourceBundle resourceBundle;

    private CharsetListEncodingState listEncodingState = null;
    private CharsetEncodingState encodingState = null;
    private ActionListener encodingActionListener;
    private ButtonGroup encodingButtonGroup;
    private javax.swing.JMenu toolsEncodingMenu;
    private javax.swing.JRadioButtonMenuItem utfEncodingRadioButtonMenuItem;
    private ActionListener utfEncodingActionListener;

    private ManageEncodingsAction manageEncodingsAction;

    public EncodingsManager() {
        resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EncodingsManager.class);
    }

    public void init() {
        encodingButtonGroup = new ButtonGroup();

//        encodingActionListener = (ActionEvent e) -> {
//            textEncodingService.setSelectedEncoding(((JRadioButtonMenuItem) e.getSource()).getText());
//        };
        utfEncodingRadioButtonMenuItem = UiUtils.createRadioButtonMenuItem();
        utfEncodingRadioButtonMenuItem.setSelected(true);
        utfEncodingRadioButtonMenuItem.setText(resourceBundle.getString("defaultEncoding.text"));
        utfEncodingRadioButtonMenuItem.setToolTipText(MessageFormat.format(resourceBundle.getString("switchEncoding.toolTipText"), new Object[]{ENCODING_UTF8}));
//        utfEncodingActionListener = (java.awt.event.ActionEvent evt) -> textEncodingService.setSelectedEncoding(ENCODING_UTF8);
//        utfEncodingRadioButtonMenuItem.addActionListener(utfEncodingActionListener);

        encodingButtonGroup.add(utfEncodingRadioButtonMenuItem);
        manageEncodingsAction = new ManageEncodingsAction();
        manageEncodingsAction.setup(resourceBundle);

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        toolsEncodingMenu = UiUtils.createMenu();
        Action toolsEncodingAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        toolsEncodingAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextEncoding.class, (instance) -> {
                    listEncodingState = instance instanceof CharsetListEncodingState ? (CharsetListEncodingState) instance : null;
                    encodingState = instance instanceof CharsetEncodingState ? (CharsetEncodingState) instance : null;
                    rebuildEncodings();
                });
                registrar.registerStateChangeListener(ContextEncoding.class, (instance, changeType) -> {
                    if (CharsetListEncodingState.ChangeType.ENCODING_LIST.equals(changeType) || CharsetEncodingState.ChangeType.ENCODING.equals(changeType)) {
                        rebuildEncodings();
                    }
                });
            }
        });
        toolsEncodingMenu.setAction(toolsEncodingAction);
        toolsEncodingMenu.addSeparator();
        toolsEncodingMenu.add(actionModule.actionToMenuItem(manageEncodingsAction));
        toolsEncodingMenu.setText(resourceBundle.getString("toolsEncodingMenu.text"));
        toolsEncodingMenu.setToolTipText(resourceBundle.getString("toolsEncodingMenu.shortDescription"));
        EncodingsManager.this.rebuildEncodings();
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

        List<String> encodings = listEncodingState == null ? null : listEncodingState.getEncodings();
        if (encodings == null || encodings.isEmpty()) {
            toolsEncodingMenu.add(utfEncodingRadioButtonMenuItem, 0);
            if (encodingState != null) {
                encodingState.setEncoding(ENCODING_UTF8);
            }
            utfEncodingRadioButtonMenuItem.setSelected(true);
        } else {
            int selectedEncodingIndex = encodings.indexOf(encodingState.getEncoding());
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

    private void updateEncodingsSelection(int encodingIndex) {
        JMenuItem item = toolsEncodingMenu.getItem(encodingIndex);
        item.setSelected(true);
    }

    public void cycleNextEncoding() {
        if (listEncodingState == null) {
            return;
        }

        List<String> encodings = listEncodingState.getEncodings();
        if (encodings.isEmpty()) {
            return;
        }

        int encodingIndex = 0;
        int selectedEncodingIndex = encodings.indexOf(encodingState.getEncoding());
        if (selectedEncodingIndex < 0 || selectedEncodingIndex == encodings.size() - 1) {
            encodingState.setEncoding(encodings.get(0));
        } else {
            encodingIndex = selectedEncodingIndex + 1;
            encodingState.setEncoding(encodings.get(encodingIndex));
        }

        updateEncodingsSelection(encodingIndex);
    }

    public void cyclePreviousEncoding() {
        if (listEncodingState == null) {
            return;
        }

        List<String> encodings = listEncodingState.getEncodings();
        if (encodings.isEmpty()) {
            return;
        }

        int encodingIndex = 0;
        int selectedEncodingIndex = encodings.indexOf(encodingState.getEncoding());
        if (selectedEncodingIndex > 0) {
            encodingIndex = selectedEncodingIndex - 1;
            encodingState.setEncoding(encodings.get(encodingIndex));
        } else if (!encodings.isEmpty()) {
            encodingIndex = encodings.size() - 1;
            encodingState.setEncoding(encodings.get(encodingIndex));
        }

        updateEncodingsSelection(encodingIndex);
    }

    public void popupEncodingsMenu(MouseEvent mouseEvent) {
        JPopupMenu popupMenu = UiUtils.createPopupMenu();

        String selectedEncoding = encodingState.getEncoding();
        List<String> encodings = listEncodingState == null ? null : listEncodingState.getEncodings();
        if (encodings == null || encodings.isEmpty()) {
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
        popupMenu.addSeparator();
        popupMenu.add(actionModule.actionToMenuItem(manageEncodingsAction));

        popupMenu.show((Component) mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
    }
}
