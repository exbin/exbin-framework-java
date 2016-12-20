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
package org.exbin.framework.deltahex;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.Action;
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
import org.exbin.framework.deltahex.panel.HexAppearancePanelFrame;
import org.exbin.framework.deltahex.panel.HexColorOptionsPanel;
import org.exbin.framework.deltahex.panel.HexColorPanelApi;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanelApi;
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
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Hexadecimal editor module.
 *
 * @version 0.2.0 2016/12/08
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
    private static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    public static final String HEX_STATUS_BAR_ID = "hexStatusBar";

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private HexEditorProvider editorProvider;
    private HexStatusPanel hexStatusPanel;

    private FindReplaceHandler findReplaceHandler;
    private ViewNonprintablesHandler viewNonprintablesHandler;
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

    private boolean deltaMode = false;

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
            HexPanel panel;
            if (deltaMode) {
                SegmentsRepository segmentsRepository = new SegmentsRepository();
                panel = new HexPanel(segmentsRepository);
            } else {
                panel = new HexPanel();
            }
            editorProvider = panel;

            panel.setPopupMenu(createPopupMenu(panel.getId()));
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

        optionsModule.addOptionsPanel(new HexColorOptionsPanel(textColorPanelFrame));

        HexAppearancePanelFrame textAppearancePanelFrame;
        textAppearancePanelFrame = new HexAppearancePanelFrame() {
            @Override
            public boolean getWordWrapMode() {
                return getEditorProvider().isWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                getEditorProvider().setWordWrapMode(mode);
            }
        };

        optionsModule.extendAppearanceOptionsPanel(new HexAppearanceOptionsPanel(textAppearancePanelFrame));

        TextEncodingPanelApi textEncodingPanelFrame = new TextEncodingPanelApi() {
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
        optionsModule.addOptionsPanel(new TextEncodingOptionsPanel(textEncodingPanelFrame));
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

    public void setDeltaMode(boolean deltaMode) {
        this.deltaMode = deltaMode;
    }

    public static interface EncodingStatusHandler {

        void cycleEncodings();

        void popupEncodingsMenu(MouseEvent mouseEvent);
    }

    public static interface CodeAreaPopupMenuHandler {

        JPopupMenu createPopupMenu(CodeArea codeArea, String menuPostfix);

        void dropPopupMenu(String menuPostfix);
    }
}
