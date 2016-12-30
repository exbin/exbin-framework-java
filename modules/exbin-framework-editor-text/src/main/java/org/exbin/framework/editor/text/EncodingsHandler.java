/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.text;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.text.panel.AddEncodingPanel;
import org.exbin.framework.editor.text.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanelApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;

/**
 * Encodings handler.
 *
 * @version 0.2.0 2016/12/30
 * @author ExBin Project (http://exbin.org)
 */
public class EncodingsHandler implements TextEncodingPanelApi {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private TextEncodingStatusApi textEncodingStatus;
    private final ResourceBundle resourceBundle;

    private List<String> encodings = null;
    private ActionListener encodingActionListener;
    private ButtonGroup encodingButtonGroup;
    private javax.swing.JMenu toolsEncodingMenu;
    private javax.swing.JRadioButtonMenuItem utfEncodingRadioButtonMenuItem;
    private ActionListener utfEncodingActionListener;

    public static final String UTF_ENCODING_TEXT = "UTF-8 (default)";
    public static final String UTF_ENCODING_TOOLTIP = "Set encoding UTF-8";

    private Action manageEncodingsAction;

    public EncodingsHandler(XBApplication application, EditorProvider editorProvider, TextEncodingStatusApi textStatus) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.textEncodingStatus = textStatus;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorTextModule.class);
    }

    public void init() {
        encodings = new ArrayList<>();
        encodingButtonGroup = new ButtonGroup();

        encodingActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedEncoding(((JRadioButtonMenuItem) e.getSource()).getText());
            }
        };

        utfEncodingRadioButtonMenuItem = new JRadioButtonMenuItem();
        utfEncodingRadioButtonMenuItem.setSelected(true);
        utfEncodingRadioButtonMenuItem.setText(UTF_ENCODING_TEXT);
        utfEncodingRadioButtonMenuItem.setToolTipText(UTF_ENCODING_TOOLTIP);
        utfEncodingActionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSelectedEncoding(TextEncodingPanel.ENCODING_UTF8);
            }
        };
        utfEncodingRadioButtonMenuItem.addActionListener(utfEncodingActionListener);

        encodingButtonGroup.add(utfEncodingRadioButtonMenuItem);
        manageEncodingsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextEncodingPanel encodingsPanel = new TextEncodingPanel();
                encodingsPanel.setEncodingList(new ArrayList<>(encodings));
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(encodingsPanel, controlPanel);
                final JDialog dialog = frameModule.createDialog(dialogPanel);
                controlPanel.setHandler(new OptionsControlHandler() {
                    @Override
                    public void controlActionPerformed(OptionsControlHandler.ControlActionType actionType) {
                        if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                            encodings = encodingsPanel.getEncodingList();
                            encodingsRebuild();
                            if (actionType != OptionsControlHandler.ControlActionType.SAVE) {
                                encodingsPanel.saveToPreferences(application.getAppPreferences());
                            }
                        }
                        WindowUtils.closeWindow(dialog);
                    }
                });
                encodingsPanel.setAddEncodingsOperation(new TextEncodingPanel.AddEncodingsOperation() {
                    @Override
                    public List<String> run(List<String> usedEncodings) {
                        final List<String> result = new ArrayList<>();
                        final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                        addEncodingPanel.setUsedEncodings(usedEncodings);
                        DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                        JPanel dialogPanel = WindowUtils.createDialogPanel(addEncodingPanel, controlPanel);
                        final JDialog addEncodingDialog = frameModule.createDialog(dialogPanel);
                        controlPanel.setHandler(new DefaultControlHandler() {
                            @Override
                            public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                    result.addAll(addEncodingPanel.getEncodings());
                                }

                                WindowUtils.closeWindow(addEncodingDialog);
                            }
                        });
                        frameModule.setDialogTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
                        WindowUtils.assignGlobalKeyListener(addEncodingDialog, controlPanel.createOkCancelListener());
                        addEncodingDialog.setLocationRelativeTo(dialog);
                        addEncodingDialog.setVisible(true);
                        return result;
                    }
                });
                WindowUtils.addHeaderPanel(dialog, encodingsPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, encodingsPanel.getResourceBundle());
                WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
                dialog.setLocationRelativeTo(frameModule.getFrame());
                dialog.setVisible(true);
            }
        };
        ActionUtils.setupAction(manageEncodingsAction, resourceBundle, "manageEncodingsAction");
        manageEncodingsAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        manageEncodingsAction.putValue(Action.NAME, manageEncodingsAction.getValue(Action.NAME) + ActionUtils.DIALOG_MENUITEM_EXT);

        toolsEncodingMenu = new JMenu();
        toolsEncodingMenu.add(utfEncodingRadioButtonMenuItem);
        toolsEncodingMenu.addSeparator();
        toolsEncodingMenu.add(manageEncodingsAction);
        toolsEncodingMenu.setText(resourceBundle.getString("toolsEncodingMenu.text"));
        toolsEncodingMenu.setToolTipText(resourceBundle.getString("toolsEncodingMenu.shortDescription"));
    }

    @Override
    public List<String> getEncodings() {
        return encodings;
    }

    @Override
    public void setEncodings(List<String> encodings) {
        this.encodings = encodings;
    }

    @Override
    public String getSelectedEncoding() {
        return ((TextCharsetApi) editorProvider.getPanel()).getCharset().name();
    }

    @Override
    public void setSelectedEncoding(String encoding) {
        if (encoding != null) {
            ((TextCharsetApi) editorProvider.getPanel()).setCharset(Charset.forName(encoding));
            textEncodingStatus.setEncoding(encoding);
        }
    }

    public void setTextEncodingStatus(TextEncodingStatusApi textEncodingStatus) {
        this.textEncodingStatus = textEncodingStatus;
    }

    public JMenu getToolsEncodingMenu() {
        return toolsEncodingMenu;
    }

    public void encodingsRebuild() {
        String encodingToolTip = "Set encoding ";
        for (int i = toolsEncodingMenu.getItemCount() - 2; i > 1; i--) {
            toolsEncodingMenu.remove(i);
        }

        if (encodings.size() > 0) {
            int selectedEncoding = encodings.indexOf(getSelectedEncoding());
            if (selectedEncoding < 0) {
                setSelectedEncoding(TextEncodingPanel.ENCODING_UTF8);
                utfEncodingRadioButtonMenuItem.setSelected(true);
            }
            toolsEncodingMenu.add(new JSeparator(), 1);
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(encoding, false);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(encodingToolTip + encoding);
                toolsEncodingMenu.add(item, index + 2);
                encodingButtonGroup.add(item);
                if (index == selectedEncoding) {
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

    public void loadFromPreferences(Preferences preferences) {
        setSelectedEncoding(preferences.get(TextEncodingOptionsPanel.PREFERENCES_TEXT_ENCODING_DEFAULT, TextEncodingPanel.ENCODING_UTF8));
        encodings.clear();
        String value;
        int i = 0;
        do {
            value = preferences.get(TextEncodingPanel.PREFERENCES_TEXT_ENCODING_PREFIX + Integer.toString(i), null);
            if (value != null) {
                encodings.add(value);
                i++;
            }
        } while (value != null);
        encodingsRebuild();
    }

    public void cycleEncodings() {
        int menuIndex = 0;
        if (encodings.size() > 0) {
            int selectedEncoding = encodings.indexOf(getSelectedEncoding());
            if (selectedEncoding < 0) {
                setSelectedEncoding(encodings.get(0));
                menuIndex = 1;
            } else if (selectedEncoding < encodings.size() - 1) {
                setSelectedEncoding(encodings.get(selectedEncoding + 1));
                menuIndex = selectedEncoding + 2;
            } else {
                setSelectedEncoding(TextEncodingPanel.ENCODING_UTF8);
            }
        }

        updateEncodingsSelection(menuIndex);
    }

    public void popupEncodingsMenu(MouseEvent mouseEvent) {
        JPopupMenu popupMenu = new JPopupMenu();

        int selectedEncoding = encodings.indexOf(getSelectedEncoding());
        String encodingToolTip = "Set encoding ";
        JRadioButtonMenuItem utfEncoding = new JRadioButtonMenuItem("", false);
        utfEncoding.setText(UTF_ENCODING_TEXT);
        utfEncoding.setToolTipText(UTF_ENCODING_TOOLTIP);
        utfEncoding.addActionListener(utfEncodingActionListener);
        if (selectedEncoding < 0) {
            utfEncoding.setSelected(true);
        }
        popupMenu.add(utfEncoding);
        if (encodings.size() > 0) {

            popupMenu.add(new JSeparator(), 1);
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(encoding, false);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(encodingToolTip + encoding);
                popupMenu.add(item, index + 2);
                if (index == selectedEncoding) {
                    item.setSelected(true);
                }
            }
        }

        popupMenu.add(new JSeparator());
        popupMenu.add(manageEncodingsAction);

        popupMenu.show((Component) mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
    }
}
