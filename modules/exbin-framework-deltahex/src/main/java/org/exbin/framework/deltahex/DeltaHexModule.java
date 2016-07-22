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
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import org.exbin.deltahex.CodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.deltahex.panel.HexAppearanceOptionsPanel;
import org.exbin.framework.deltahex.panel.HexColorOptionsPanel;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.gui.editor.api.XBEditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.menu.api.ToolBarGroup;
import org.exbin.framework.gui.menu.api.ToolBarPosition;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.deltahex.panel.HexAppearancePanelFrame;
import org.exbin.framework.deltahex.panel.HexColorPanelApi;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanelApi;
import org.exbin.framework.gui.menu.api.ClipboardActions;
import org.exbin.framework.gui.menu.api.ClipboardActionsHandler;
import org.exbin.framework.gui.menu.api.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.menu.api.NextToMode;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Hexadecimal editor module.
 *
 * @version 0.1.0 2016/07/21
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

    private final java.util.ResourceBundle bundle = ActionUtils.getResourceBundleByClass(DeltaHexModule.class);

    private XBApplication application;
    private XBEditorProvider editorProvider;
    private HexStatusPanel textStatusPanel;

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

    public DeltaHexModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public XBEditorProvider getEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new HexPanel();
            ((HexPanel) editorProvider).setPopupMenu(createPopupMenu());
            ((HexPanel) editorProvider).setHexCodePopupMenuHandler(new HexCodePopupMenuHandler() {
                @Override
                public JPopupMenu createPopupMenu(CodeArea codeArea, String menuPostfix) {
                    return createCodeAreaPopupMenu(codeArea, menuPostfix);
                }

                @Override
                public void dropPopupMenu(String menuPostfix) {
                    dropCodeAreaPopupMenu(menuPostfix);
                }
            });
            ((HexPanel) editorProvider).setGoToLineAction(getGoToLineHandler().getGoToLineAction());
            ((HexPanel) editorProvider).setCopyAsCode(getClipboardCodeHandler().getCopyAsCodeAction());
            ((HexPanel) editorProvider).setPasteFromCode(getClipboardCodeHandler().getPasteFromCodeAction());
            ((HexPanel) editorProvider).setEncodingStatusHandler(new EncodingStatusHandler() {
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

    public void registerStatusBar() {
        textStatusPanel = new HexStatusPanel();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, HEX_STATUS_BAR_ID, textStatusPanel);
        frameModule.switchStatusBar(HEX_STATUS_BAR_ID);
        ((HexPanel) getEditorProvider()).registerTextStatus(textStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(textStatusPanel);
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
                return ((HexPanel) getEditorProvider()).getCurrentColors();
            }

            @Override
            public Map<HexColorType, Color> getDefaultTextColors() {
                return ((HexPanel) getEditorProvider()).getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Map<HexColorType, Color> colors) {
                ((HexPanel) getEditorProvider()).setCurrentColors(colors);
            }
        };

        optionsModule.addOptionsPanel(new HexColorOptionsPanel(textColorPanelFrame));

        HexAppearancePanelFrame textAppearancePanelFrame;
        textAppearancePanelFrame = new HexAppearancePanelFrame() {
            @Override
            public boolean getWordWrapMode() {
                return ((HexPanel) getEditorProvider()).getWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                ((HexPanel) getEditorProvider()).setWordWrapMode(mode);
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
                return ((HexPanel) getEditorProvider()).getCharset().name();
            }

            @Override
            public void setEncodings(List<String> encodings) {
                getEncodingsHandler().setEncodings(encodings);
                getEncodingsHandler().encodingsRebuild();
            }

            @Override
            public void setSelectedEncoding(String encoding) {
                if (encoding != null) {
                    ((HexPanel) getEditorProvider()).setCharset(Charset.forName(encoding));
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
        return textStatusPanel;
    }

    private FindReplaceHandler getFindReplaceHandler() {
        if (findReplaceHandler == null) {
            findReplaceHandler = new FindReplaceHandler(application, (HexPanel) getEditorProvider());
            findReplaceHandler.init();
        }

        return findReplaceHandler;
    }

    private ViewNonprintablesHandler getViewNonprintablesHandler() {
        if (viewNonprintablesHandler == null) {
            viewNonprintablesHandler = new ViewNonprintablesHandler(application, (HexPanel) getEditorProvider());
            viewNonprintablesHandler.init();
        }

        return viewNonprintablesHandler;
    }

    private ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, (HexPanel) getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    private LineWrappingHandler getWordWrappingHandler() {
        if (wordWrappingHandler == null) {
            wordWrappingHandler = new LineWrappingHandler(application, (HexPanel) getEditorProvider());
            wordWrappingHandler.init();
        }

        return wordWrappingHandler;
    }

    private GoToPositionHandler getGoToLineHandler() {
        if (goToLineHandler == null) {
            goToLineHandler = new GoToPositionHandler(application, (HexPanel) getEditorProvider());
            goToLineHandler.init();
        }

        return goToLineHandler;
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, (HexPanel) getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    private EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler(application, (HexPanel) getEditorProvider(), getTextStatusPanel());
            encodingsHandler.init();
        }

        return encodingsHandler;
    }

    private PrintHandler getPrintHandler() {
        if (printHandler == null) {
            printHandler = new PrintHandler(application, (HexPanel) getEditorProvider());
            printHandler.init();
        }

        return printHandler;
    }

    private ViewModeHandler getViewModeHandler() {
        if (viewModeHandler == null) {
            viewModeHandler = new ViewModeHandler(application, (HexPanel) getEditorProvider());
            viewModeHandler.init();
        }

        return viewModeHandler;
    }

    private CodeTypeHandler getCodeTypeHandler() {
        if (codeTypeHandler == null) {
            codeTypeHandler = new CodeTypeHandler(application, (HexPanel) getEditorProvider());
            codeTypeHandler.init();
        }

        return codeTypeHandler;
    }

    private PositionCodeTypeHandler getPositionCodeTypeHandler() {
        if (positionCodeTypeHandler == null) {
            positionCodeTypeHandler = new PositionCodeTypeHandler(application, (HexPanel) getEditorProvider());
            positionCodeTypeHandler.init();
        }

        return positionCodeTypeHandler;
    }

    private HexCharactersCaseHandler getHexCharactersCaseHandler() {
        if (hexCharactersCaseHandler == null) {
            hexCharactersCaseHandler = new HexCharactersCaseHandler(application, (HexPanel) getEditorProvider());
            hexCharactersCaseHandler.init();
        }

        return hexCharactersCaseHandler;
    }

    private ClipboardCodeHandler getClipboardCodeHandler() {
        if (clipboardCodeHandler == null) {
            clipboardCodeHandler = new ClipboardCodeHandler(application, (HexPanel) getEditorProvider());
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
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, VIEW_MODE_SUBMENU_ID, bundle.getString("viewModeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(VIEW_MODE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getDualModeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getCodeMatrixModeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeHandler.getTextPreviewModeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerCodeTypeMenu() {
        getCodeTypeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, CODE_TYPE_SUBMENU_ID, bundle.getString("codeTypeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(CODE_TYPE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getBinaryCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeHandler.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerPositionCodeTypeMenu() {
        getPositionCodeTypeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_SUBMENU_ID, bundle.getString("positionCodeTypeSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeHandler.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerHexCharactersCaseHandlerMenu() {
        getHexCharactersCaseHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, HEX_CHARACTERS_CASE_SUBMENU_ID, bundle.getString("hexCharsCaseSubMenu.text"), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseHandler.getUpperHexCharsAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseHandler.getLowerHexCharsAction(), new MenuPosition(PositionMode.TOP));
    }

    private JPopupMenu createPopupMenu() {
        getClipboardCodeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenu(HEX_POPUP_MENU_ID, MODULE_ID);
        menuModule.registerClipboardMenuItems(HEX_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);
        menuModule.registerMenuItem(HEX_POPUP_MENU_ID, MODULE_ID, clipboardCodeHandler.getCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getCopyAction().getValue(Action.NAME)));
        menuModule.registerMenuItem(HEX_POPUP_MENU_ID, MODULE_ID, clipboardCodeHandler.getPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getPasteAction().getValue(Action.NAME)));

        JPopupMenu popupMenu = new JPopupMenu();
        menuModule.buildMenu(popupMenu, HEX_POPUP_MENU_ID);
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
                codeArea.addSelectionChangedListener(new CodeArea.SelectionChangedListener() {
                    @Override
                    public void selectionChanged(CodeArea.SelectionRange sr) {
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

    public static interface EncodingStatusHandler {

        void cycleEncodings();

        void popupEncodingsMenu(MouseEvent mouseEvent);
    }

    public static interface HexCodePopupMenuHandler {

        JPopupMenu createPopupMenu(CodeArea codeArea, String menuPostfix);

        void dropPopupMenu(String menuPostfix);
    }
}
