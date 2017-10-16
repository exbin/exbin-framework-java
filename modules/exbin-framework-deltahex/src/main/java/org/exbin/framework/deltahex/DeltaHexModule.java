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
package org.exbin.framework.deltahex;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.exbin.deltahex.SelectionChangedListener;
import org.exbin.deltahex.SelectionRange;
import org.exbin.deltahex.delta.SegmentsRepository;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.deltahex.panel.HexAppearanceOptionsPanel;
import org.exbin.framework.deltahex.panel.HexColorOptionsPanel;
import org.exbin.framework.deltahex.panel.HexColorPanelApi;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.panel.AddEncodingPanel;
import org.exbin.framework.editor.text.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanelApi;
import org.exbin.framework.editor.text.panel.TextFontOptionsPanel;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.editor.text.panel.TextFontPanelApi;
import org.exbin.framework.gui.docking.api.GuiDockingModuleApi;
import org.exbin.framework.gui.file.api.FileHandlingActionsApi;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.ClipboardActions;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.gui.menu.api.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.menu.api.ComponentPopupEventDispatcher;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.NextToMode;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.menu.api.ToolBarGroup;
import org.exbin.framework.gui.menu.api.ToolBarPosition;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.deltahex.panel.HexAppearanceOptionsPanelApi;

/**
 * Hexadecimal editor module.
 *
 * @version 0.2.0 2017/10/16
 * @author ExBin Project (http://exbin.org)
 */
public class DeltaHexModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(DeltaHexModule.class);
    public static final String HEX_POPUP_MENU_ID = MODULE_ID + ".hexPopupMenu";
    public static final String CODE_AREA_POPUP_MENU_ID = MODULE_ID + ".codeAreaPopupMenu";
    public static final String VIEW_MODE_SUBMENU_ID = MODULE_ID + ".viewModeSubMenu";
    public static final String CODE_TYPE_SUBMENU_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String POSITION_CODE_TYPE_SUBMENU_ID = MODULE_ID + ".positionCodeTypeSubMenu";
    public static final String HEX_CHARACTERS_CASE_SUBMENU_ID = MODULE_ID + ".hexCharactersCaseSubMenu";

    private static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    private static final String VIEW_NONPRINTABLES_MENU_GROUP_ID = MODULE_ID + ".viewNonprintablesMenuGroup";
    private static final String VIEW_VALUES_PANEL_MENU_GROUP_ID = MODULE_ID + ".viewValuesPanelMenuGroup";
    private static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    public static final String HEX_STATUS_BAR_ID = "hexStatusBar";

    public static final String PREFERENCES_MEMORY_MODE = "memoryMode";

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private HexEditorProvider editorProvider;
    private HexStatusPanel hexStatusPanel;
    private HexColorOptionsPanel hexColorOptionsPanel;
    private TextEncodingOptionsPanel textEncodingOptionsPanel;
    private TextFontOptionsPanel textFontOptionsPanel;
    private HexAppearanceOptionsPanel hexAppearanceOptionsPanel;

    private FindReplaceHandler findReplaceHandler;
    private ViewNonprintablesHandler viewNonprintablesHandler;
    private ViewValuesPanelHandler viewValuesPanelHandler;
    private ToolsOptionsHandler toolsOptionsHandler;
    private LineWrappingHandler wordWrappingHandler;
    private EncodingsHandler encodingsHandler;
    private GoToPositionHandler goToLineHandler;
    private PropertiesHandler propertiesHandler;
    private PrintHandler printHandler;
    private ViewModeHandler viewModeHandler;
    private CodeTypeHandler codeTypeHandler;
    private PositionCodeTypeHandler positionCodeTypeHandler;
    private HexCharactersCaseHandler hexCharactersCaseHandler;
    private ClipboardCodeHandler clipboardCodeHandler;
    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;

    public DeltaHexModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
        }

        return resourceBundle;
    }

    public HexEditorProvider getEditorProvider() {
        if (editorProvider == null) {
            String deltaModeString = application.getAppPreferences().get(PREFERENCES_MEMORY_MODE, HexStatusApi.MemoryMode.DELTA_MODE.getPreferencesValue());
            HexStatusApi.MemoryMode memoryMode = HexStatusApi.MemoryMode.findByPreferencesValue(deltaModeString);
            HexPanel panel = new HexPanel();
            panel.setSegmentsRepository(new SegmentsRepository());
            panel.setDeltaMemoryMode(memoryMode == HexStatusApi.MemoryMode.DELTA_MODE);
            editorProvider = panel;

            panel.setPopupMenu(createPopupMenu(panel.getId()));
            panel.setApplication(application);
            panel.setCodeAreaPopupMenuHandler(getCodeAreaPopupMenuHandler());
            panel.setGoToLineAction(getGoToLineHandler().getGoToLineAction());
            panel.setCopyAsCode(getClipboardCodeHandler().getCopyAsCodeAction());
            panel.setPasteFromCode(getClipboardCodeHandler().getPasteFromCodeAction());
            panel.setEncodingStatusHandler(new EncodingStatusHandler() {
                @Override
                public void cycleEncodings() {
                    encodingsHandler.cycleEncodings();
                }

                @Override
                public void popupEncodingsMenu(MouseEvent mouseEvent) {
                    encodingsHandler.popupEncodingsMenu(mouseEvent);
                }
            });
            panel.setReleaseFileMethod(new HexPanel.ReleaseFileMethod() {
                @Override
                public boolean execute() {
                    GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
                    FileHandlingActionsApi fileHandlingActions = fileModule.getFileHandlingActions();
                    return fileHandlingActions.releaseFile();
                }
            });
        }

        return editorProvider;
    }

    public HexEditorProvider getMultiEditorProvider() {
        if (editorProvider == null) {
            GuiDockingModuleApi dockingModule = application.getModuleRepository().getModuleByInterface(GuiDockingModuleApi.class);
            editorProvider = new HexEditorHandler();
            ((HexEditorHandler) editorProvider).setHexPanelInit(new HexEditorHandler.HexPanelInit() {
                @Override
                public void init(HexPanel panel) {
                    panel.setPopupMenu(createPopupMenu(panel.getId()));
//                    panel.setCodeAreaPopupMenuHandler(getCodeAreaPopupMenuHandler());
                    panel.setGoToLineAction(getGoToLineHandler().getGoToLineAction());
                    panel.setCopyAsCode(getClipboardCodeHandler().getCopyAsCodeAction());
                    panel.setPasteFromCode(getClipboardCodeHandler().getPasteFromCodeAction());
                    panel.setEncodingStatusHandler(new EncodingStatusHandler() {
                        @Override
                        public void cycleEncodings() {
                            encodingsHandler.cycleEncodings();
                        }

                        @Override
                        public void popupEncodingsMenu(MouseEvent mouseEvent) {
                            encodingsHandler.popupEncodingsMenu(mouseEvent);
                        }
                    });
                }
            });
            ((HexEditorHandler) editorProvider).setEditorViewHandling(dockingModule.getEditorViewHandling());
            ((HexEditorHandler) editorProvider).setSegmentsRepository(new SegmentsRepository());
            ((HexEditorHandler) editorProvider).init();
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            FileHandlingActionsApi fileHandlingActions = fileModule.getFileHandlingActions();
            fileHandlingActions.setFileHandler(editorProvider);
        }

        return editorProvider;
    }

    public void registerStatusBar() {
        hexStatusPanel = new HexStatusPanel();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, HEX_STATUS_BAR_ID, hexStatusPanel);
        frameModule.switchStatusBar(HEX_STATUS_BAR_ID);
        getEditorProvider().registerHexStatus(hexStatusPanel);
        getEditorProvider().registerEncodingStatus(hexStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(hexStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.encodingsRebuild();

        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        HexColorPanelApi textColorPanelFrame = new HexColorPanelApi() {
            @Override
            public Map<HexColorType, Color> getCurrentTextColors() {
                return getEditorProvider().getCurrentColors();
            }

            @Override
            public Map<HexColorType, Color> getDefaultTextColors() {
                return getEditorProvider().getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Map<HexColorType, Color> colors) {
                getEditorProvider().setCurrentColors(colors);
            }
        };

        hexColorOptionsPanel = new HexColorOptionsPanel();
        hexColorOptionsPanel.setPanelApi(textColorPanelFrame);
        optionsModule.addOptionsPanel(hexColorOptionsPanel);

        HexAppearanceOptionsPanelApi appearanceOptionsPanelApi;
        appearanceOptionsPanelApi = new HexAppearanceOptionsPanelApi() {
            @Override
            public boolean getWordWrapMode() {
                return getEditorProvider().isWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                getEditorProvider().setWordWrapMode(mode);

                wordWrappingHandler.getViewLineWrapAction().putValue(Action.SELECTED_KEY, mode);
            }

            @Override
            public void setShowValuesPanel(boolean showValuesPanel) {
                boolean valuesPanelVisible = getEditorProvider().isValuesPanelVisible();
                if (valuesPanelVisible != showValuesPanel) {
                    if (showValuesPanel) {
                        getEditorProvider().showValuesPanel();
                    } else {
                        getEditorProvider().hideValuesPanel();
                    }
                }

                viewValuesPanelHandler.getViewValuesPanelAction().putValue(Action.SELECTED_KEY, showValuesPanel);
            }
        };

        hexAppearanceOptionsPanel = new HexAppearanceOptionsPanel(appearanceOptionsPanelApi);
        optionsModule.extendAppearanceOptionsPanel(hexAppearanceOptionsPanel);

        TextEncodingPanelApi textEncodingPanelApi = new TextEncodingPanelApi() {
            @Override
            public List<String> getEncodings() {
                return getEncodingsHandler().getEncodings();
            }

            @Override
            public String getSelectedEncoding() {
                return getEditorProvider().getCharset().name();
            }

            @Override
            public void setEncodings(List<String> encodings) {
                getEncodingsHandler().setEncodings(encodings);
                getEncodingsHandler().encodingsRebuild();
            }

            @Override
            public void setSelectedEncoding(String encoding) {
                if (encoding != null) {
                    getEditorProvider().setCharset(Charset.forName(encoding));
                }
            }
        };
        textEncodingOptionsPanel = new TextEncodingOptionsPanel(textEncodingPanelApi);
        textEncodingOptionsPanel.setAddEncodingsOperation(new TextEncodingPanel.AddEncodingsOperation() {
            @Override
            public List<String> run(List<String> usedEncodings) {
                final List<String> result = new ArrayList<>();
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
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
                addEncodingDialog.setLocationRelativeTo(addEncodingDialog.getParent());
                addEncodingDialog.setVisible(true);
                return result;
            }
        });
        optionsModule.addOptionsPanel(textEncodingOptionsPanel);

        TextFontPanelApi textFontPanelApi = new TextFontPanelApi() {
            @Override
            public Font getCurrentFont() {
                return ((TextFontApi) getEditorProvider()).getCurrentFont();
            }

            @Override
            public Font getDefaultFont() {
                return ((TextFontApi) getEditorProvider()).getDefaultFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                ((TextFontApi) getEditorProvider()).setCurrentFont(font);
            }
        };
        textFontOptionsPanel = new TextFontOptionsPanel(textFontPanelApi);
        textFontOptionsPanel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
            @Override
            public Font changeFont(Font currentFont) {
                final Result result = new Result();
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextFontPanel fontPanel = new TextFontPanel();
                fontPanel.setStoredFont(currentFont);
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                final JDialog dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog, fontPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                controlPanel.setHandler(new OptionsControlHandler() {
                    @Override
                    public void controlActionPerformed(OptionsControlHandler.ControlActionType actionType) {
                        if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                            if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                                fontPanel.saveToPreferences(application.getAppPreferences());
                            }
                            result.font = fontPanel.getStoredFont();
                        }

                        WindowUtils.closeWindow(dialog);
                    }
                });
                WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.setVisible(true);

                return result.font;
            }

            class Result {

                Font font;
            }
        });
        optionsModule.addOptionsPanel(textFontOptionsPanel);
    }

    public void registerWordWrapping() {
        getWordWrappingHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, wordWrappingHandler.getViewLineWrapAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        getGoToLineHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, goToLineHandler.getGoToLineAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public HexStatusPanel getTextStatusPanel() {
        return hexStatusPanel;
    }

    private FindReplaceHandler getFindReplaceHandler() {
        if (findReplaceHandler == null) {
            findReplaceHandler = new FindReplaceHandler(application, getEditorProvider());
            findReplaceHandler.init();
        }

        return findReplaceHandler;
    }

    private ViewNonprintablesHandler getViewNonprintablesHandler() {
        if (viewNonprintablesHandler == null) {
            viewNonprintablesHandler = new ViewNonprintablesHandler(application, getEditorProvider());
            viewNonprintablesHandler.init();
        }

        return viewNonprintablesHandler;
    }

    private ViewValuesPanelHandler getViewValuesPanelHandler() {
        if (viewValuesPanelHandler == null) {
            viewValuesPanelHandler = new ViewValuesPanelHandler(application, getEditorProvider());
            viewValuesPanelHandler.init();
        }

        return viewValuesPanelHandler;
    }

    private ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    private LineWrappingHandler getWordWrappingHandler() {
        if (wordWrappingHandler == null) {
            wordWrappingHandler = new LineWrappingHandler(application, getEditorProvider());
            wordWrappingHandler.init();
        }

        return wordWrappingHandler;
    }

    private GoToPositionHandler getGoToLineHandler() {
        if (goToLineHandler == null) {
            goToLineHandler = new GoToPositionHandler(application, getEditorProvider());
            goToLineHandler.init();
        }

        return goToLineHandler;
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    private EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler(application, getEditorProvider(), getTextStatusPanel());
            encodingsHandler.init();
        }

        return encodingsHandler;
    }

    private PrintHandler getPrintHandler() {
        if (printHandler == null) {
            printHandler = new PrintHandler(application, getEditorProvider());
            printHandler.init();
        }

        return printHandler;
    }

    private ViewModeHandler getViewModeHandler() {
        if (viewModeHandler == null) {
            getResourceBundle();
            viewModeHandler = new ViewModeHandler(application, getEditorProvider());
            viewModeHandler.init();
        }

        return viewModeHandler;
    }

    private CodeTypeHandler getCodeTypeHandler() {
        if (codeTypeHandler == null) {
            getResourceBundle();
            codeTypeHandler = new CodeTypeHandler(application, getEditorProvider());
            codeTypeHandler.init();
        }

        return codeTypeHandler;
    }

    private PositionCodeTypeHandler getPositionCodeTypeHandler() {
        if (positionCodeTypeHandler == null) {
            getResourceBundle();
            positionCodeTypeHandler = new PositionCodeTypeHandler(application, getEditorProvider());
            positionCodeTypeHandler.init();
        }

        return positionCodeTypeHandler;
    }

    private HexCharactersCaseHandler getHexCharactersCaseHandler() {
        if (hexCharactersCaseHandler == null) {
            getResourceBundle();
            hexCharactersCaseHandler = new HexCharactersCaseHandler(application, getEditorProvider());
            hexCharactersCaseHandler.init();
        }

        return hexCharactersCaseHandler;
    }

    private ClipboardCodeHandler getClipboardCodeHandler() {
        if (clipboardCodeHandler == null) {
            getResourceBundle();
            clipboardCodeHandler = new ClipboardCodeHandler(application, getEditorProvider());
            clipboardCodeHandler.init();
        }

        return clipboardCodeHandler;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditFindAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditFindAgainAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditReplaceAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        menuModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, findReplaceHandler.getEditFindAction(), new ToolBarPosition(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerViewNonprintablesMenuActions() {
        getViewNonprintablesHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_NONPRINTABLES_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewNonprintablesHandler.getViewNonprintablesAction(), new MenuPosition(VIEW_NONPRINTABLES_MENU_GROUP_ID));
    }

    public void registerViewValuesPanelMenuActions() {
        getViewValuesPanelHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_VALUES_PANEL_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewValuesPanelHandler.getViewValuesPanelAction(), new MenuPosition(VIEW_VALUES_PANEL_MENU_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        getToolsOptionsHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetFontAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetColorAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, clipboardCodeHandler.getCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getCopyAction().getValue(Action.NAME)));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, clipboardCodeHandler.getPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getPasteAction().getValue(Action.NAME)));
    }

    public void registerPropertiesMenu() {
        getPropertiesHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, propertiesHandler.getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        getPrintHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, printHandler.getPrintAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerViewModeMenu() {
        getViewModeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, VIEW_MODE_SUBMENU_ID, resourceBundle.getString("viewModeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(VIEW_MODE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getDualModeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getCodeMatrixModeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getTextPreviewModeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerCodeTypeMenu() {
        getCodeTypeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, CODE_TYPE_SUBMENU_ID, resourceBundle.getString("codeTypeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(CODE_TYPE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getBinaryCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerPositionCodeTypeMenu() {
        getPositionCodeTypeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_SUBMENU_ID, resourceBundle.getString("positionCodeTypeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerHexCharactersCaseHandlerMenu() {
        getHexCharactersCaseHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, HEX_CHARACTERS_CASE_SUBMENU_ID, resourceBundle.getString("hexCharsCaseSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseHandler.getUpperHexCharsAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseHandler.getLowerHexCharsAction(), new MenuPosition(PositionMode.TOP));
    }

    private JPopupMenu createPopupMenu(int postfix) {
        getClipboardCodeHandler();
        String popupMenuId = HEX_POPUP_MENU_ID + "." + postfix;
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenu(popupMenuId, MODULE_ID);
        menuModule.registerClipboardMenuItems(popupMenuId, MODULE_ID, SeparationMode.AROUND);
        menuModule.registerMenuItem(popupMenuId, MODULE_ID, clipboardCodeHandler.getCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getCopyAction().getValue(Action.NAME)));
        menuModule.registerMenuItem(popupMenuId, MODULE_ID, clipboardCodeHandler.getPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getPasteAction().getValue(Action.NAME)));

        JPopupMenu popupMenu = new JPopupMenu();
        menuModule.buildMenu(popupMenu, popupMenuId);
        return popupMenu;
    }

    private JPopupMenu createCodeAreaPopupMenu(final CodeArea codeArea, String menuPostfix) {
        getClipboardCodeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);
        final Action copyAsCodeAction = clipboardCodeHandler.createCopyAsCodeAction(codeArea);
        final Action pasteFromCodeAction = clipboardCodeHandler.createPasteFromCodeAction(codeArea);
        ClipboardActions clipboardActions = menuModule.createClipboardActions(new ClipboardActionsHandler() {
            @Override
            public void performCut() {
                codeArea.cut();
            }

            @Override
            public void performCopy() {
                codeArea.copy();
            }

            @Override
            public void performPaste() {
                codeArea.paste();
            }

            @Override
            public void performDelete() {
                codeArea.delete();
            }

            @Override
            public void performSelectAll() {
                codeArea.selectAll();
            }

            @Override
            public boolean isSelection() {
                return codeArea.hasSelection();
            }

            @Override
            public boolean isEditable() {
                return codeArea.isEditable();
            }

            @Override
            public boolean canSelectAll() {
                return true;
            }

            @Override
            public boolean canPaste() {
                return codeArea.canPaste();
            }

            @Override
            public void setUpdateListener(final ClipboardActionsUpdateListener updateListener) {
                codeArea.addSelectionChangedListener(new SelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionRange sr) {
                        updateListener.stateChanged();
                        copyAsCodeAction.setEnabled(codeArea.hasSelection());
                        pasteFromCodeAction.setEnabled(codeArea.canPaste());
                    }
                });
                updateListener.stateChanged();
            }
        });
        menuModule.registerClipboardMenuItems(clipboardActions, CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, SeparationMode.AROUND);
        menuModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, copyAsCodeAction, new MenuPosition(NextToMode.AFTER, (String) clipboardActions.getCopyAction().getValue(Action.NAME)));
        menuModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, pasteFromCodeAction, new MenuPosition(NextToMode.AFTER, (String) clipboardActions.getPasteAction().getValue(Action.NAME)));

        JPopupMenu popupMenu = new JPopupMenu();
        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID + menuPostfix);
        return popupMenu;
    }

    private void dropCodeAreaPopupMenu(String menuPostfix) {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }

    public void loadFromPreferences(Preferences preferences) {
        encodingsHandler.loadFromPreferences(preferences);

        // TODO move it out of panels
        if (hexColorOptionsPanel != null) {
            hexColorOptionsPanel.loadFromPreferences(preferences);
            hexColorOptionsPanel.applyPreferencesChanges();
        }

        if (textFontOptionsPanel != null) {
            textFontOptionsPanel.loadFromPreferences(preferences);
            textFontOptionsPanel.applyPreferencesChanges();
        }

        if (hexAppearanceOptionsPanel != null) {
            hexAppearanceOptionsPanel.loadFromPreferences(preferences);
            hexAppearanceOptionsPanel.applyPreferencesChanges();
        }
    }

    public CodeAreaPopupMenuHandler getCodeAreaPopupMenuHandler() {
        if (codeAreaPopupMenuHandler == null) {
            codeAreaPopupMenuHandler = new CodeAreaPopupMenuHandler() {
                @Override
                public JPopupMenu createPopupMenu(CodeArea codeArea, String menuPostfix) {
                    return createCodeAreaPopupMenu(codeArea, menuPostfix);
                }

                @Override
                public void dropPopupMenu(String menuPostfix) {
                    dropCodeAreaPopupMenu(menuPostfix);
                }
            };
        }
        return codeAreaPopupMenuHandler;
    }

    public void registerCodeAreaPopupEventDispatcher() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.addComponentPopupEventDispatcher(new ComponentPopupEventDispatcher() {

            private static final String DEFAULT_MENU_POSTFIX = ".default";
            private JPopupMenu popupMenu = null;

            @Override
            public boolean dispatchMouseEvent(MouseEvent mouseEvent) {
                Component component = getSource(mouseEvent);
                if (component instanceof CodeArea) {
                    if (((CodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = getCodeAreaPopupMenuHandler();
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        popupMenu = handler.createPopupMenu((CodeArea) component, DEFAULT_MENU_POSTFIX);

                        Point point = component.getMousePosition();
                        if (point != null) {
                            popupMenu.show(component, (int) point.getX(), (int) point.getY());
                        } else {
                            popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                        }
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (component instanceof CodeArea) {
                    if (((CodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = getCodeAreaPopupMenuHandler();
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        popupMenu = handler.createPopupMenu((CodeArea) component, DEFAULT_MENU_POSTFIX);

                        Point point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                        popupMenu.show(component, (int) point.getX(), (int) point.getY());
                        return true;
                    }
                }

                return false;
            }

            private Component getSource(MouseEvent e) {
                return SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
}
