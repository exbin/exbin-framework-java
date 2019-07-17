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
package org.exbin.framework.bined;

import org.exbin.framework.bined.handler.BinaryEditorHandler;
import org.exbin.framework.bined.handler.LayoutHandler;
import org.exbin.framework.bined.handler.ClipboardCodeHandler;
import org.exbin.framework.bined.handler.GoToPositionHandler;
import org.exbin.framework.bined.handler.ViewValuesPanelHandler;
import org.exbin.framework.bined.handler.EncodingStatusHandler;
import org.exbin.framework.bined.handler.CodeTypeHandler;
import org.exbin.framework.bined.handler.ToolsOptionsHandler;
import org.exbin.framework.bined.handler.ViewModeHandler;
import org.exbin.framework.bined.handler.PrintHandler;
import org.exbin.framework.bined.handler.ViewNonprintablesHandler;
import org.exbin.framework.bined.handler.RowWrappingHandler;
import org.exbin.framework.bined.handler.PropertiesHandler;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.handler.FindReplaceHandler;
import org.exbin.framework.bined.handler.HexCharactersCaseHandler;
import org.exbin.framework.bined.handler.PositionCodeTypeHandler;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.bined.BasicCodeAreaZone;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.options.panel.BinaryAppearanceOptionsPanel;
import org.exbin.framework.bined.panel.BinaryPanel;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.panel.AddEncodingPanel;
import org.exbin.framework.editor.text.options.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.panel.TextFontOptionsPanel;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.gui.docking.api.GuiDockingModuleApi;
import org.exbin.framework.gui.file.api.FileHandlingActionsApi;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
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
import org.exbin.framework.bined.options.panel.CodeAreaOptionsPanel;
import org.exbin.framework.bined.options.panel.CodeAreaOptionsPanelApi;
import org.exbin.framework.bined.options.panel.ColorProfilesOptionsPanel;
import org.exbin.framework.bined.options.panel.EditorOptionsPanel;
import org.exbin.framework.bined.options.panel.EditorOptionsPanelApi;
import org.exbin.framework.bined.options.panel.LayoutProfilesOptionsPanel;
import org.exbin.framework.bined.options.panel.StatusOptionsPanel;
import org.exbin.framework.bined.options.panel.ThemeProfilesOptionsPanel;
import org.exbin.framework.bined.preferences.EditorParameters;
import org.exbin.framework.bined.preferences.StatusParameters;
import org.exbin.framework.editor.text.preferences.TextEncodingParameters;
import org.exbin.framework.editor.text.preferences.TextFontParameters;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.bined.service.BinaryAppearanceService;
import org.exbin.framework.bined.service.impl.BinaryAppearanceServiceImpl;
import org.exbin.framework.editor.text.service.TextEncodingService;
import org.exbin.framework.editor.text.service.TextFontService;

/**
 * Binary data editor module.
 *
 * @version 0.2.1 2019/07/17
 * @author ExBin Project (http://exbin.org)
 */
public class BinedModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(BinedModule.class);
    public static final String BINARY_POPUP_MENU_ID = MODULE_ID + ".binaryPopupMenu";
    public static final String CODE_AREA_POPUP_MENU_ID = MODULE_ID + ".codeAreaPopupMenu";
    public static final String VIEW_MODE_SUBMENU_ID = MODULE_ID + ".viewModeSubMenu";
    public static final String CODE_TYPE_SUBMENU_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String POSITION_CODE_TYPE_SUBMENU_ID = MODULE_ID + ".positionCodeTypeSubMenu";
    public static final String HEX_CHARACTERS_CASE_SUBMENU_ID = MODULE_ID + ".hexCharactersCaseSubMenu";

    private static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    private static final String VIEW_NONPRINTABLES_MENU_GROUP_ID = MODULE_ID + ".viewNonprintablesMenuGroup";
    private static final String VIEW_VALUES_PANEL_MENU_GROUP_ID = MODULE_ID + ".viewValuesPanelMenuGroup";
    private static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    public static final String BINARY_STATUS_BAR_ID = "binaryStatusBar";

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private BinaryEditorProvider editorProvider;
    private BinaryStatusPanel binaryStatusPanel;
    private TextEncodingOptionsPanel textEncodingOptionsPanel;
    private TextFontOptionsPanel textFontOptionsPanel;
    private BinaryAppearanceOptionsPanel binaryAppearanceOptionsPanel;
    private EditorOptionsPanel editorOptionsPanel;
    private CodeAreaOptionsPanel codeAreaOptionsPanel;
    private StatusOptionsPanel statusOptionsPanel;
    private ThemeProfilesOptionsPanel themeProfilesOptionsPanel;
    private LayoutProfilesOptionsPanel layoutProfilesOptionsPanel;
    private ColorProfilesOptionsPanel colorProfilesOptionsPanel;

    private FindReplaceHandler findReplaceHandler;
    private ViewNonprintablesHandler viewNonprintablesHandler;
    private ViewValuesPanelHandler viewValuesPanelHandler;
    private ToolsOptionsHandler toolsOptionsHandler;
    private RowWrappingHandler wordWrappingHandler;
    private EncodingsHandler encodingsHandler;
    private GoToPositionHandler goToRowHandler;
    private PropertiesHandler propertiesHandler;
    private PrintHandler printHandler;
    private ViewModeHandler viewModeHandler;
    private LayoutHandler layoutHandler;
    private CodeTypeHandler codeTypeHandler;
    private PositionCodeTypeHandler positionCodeTypeHandler;
    private HexCharactersCaseHandler hexCharactersCaseHandler;
    private ClipboardCodeHandler clipboardCodeHandler;
    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;

    private final int metaMask;

    public BinedModule() {
        int metaMaskValue;
        try {
            metaMaskValue = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        } catch (java.awt.HeadlessException ex) {
            metaMaskValue = java.awt.Event.CTRL_MASK;
        }
        metaMask = metaMaskValue;
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
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
        }

        return resourceBundle;
    }

    public BinaryEditorProvider getEditorProvider() {
        if (editorProvider == null) {
            EditorParameters editorParameters = new EditorParameters(application.getAppPreferences());

            String deltaModeString = editorParameters.getMemoryMode();
            BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.findByPreferencesValue(deltaModeString);
            BinaryPanel panel = new BinaryPanel();
            panel.setSegmentsRepository(new SegmentsRepository());
            panel.setMemoryMode(memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE);
            editorProvider = panel;

            panel.setApplication(application);
            panel.setPopupMenu(createPopupMenu(panel.getId(), editorProvider.getCodeArea()));
            panel.setCodeAreaPopupMenuHandler(getCodeAreaPopupMenuHandler());
            panel.setGoToPositionAction(getGoToPositionHandler().getGoToRowAction());
            panel.setCopyAsCode(getClipboardCodeHandler().getCopyAsCodeAction());
            panel.setPasteFromCode(getClipboardCodeHandler().getPasteFromCodeAction());
            panel.setEncodingsHandler(new EncodingStatusHandler() {
                @Override
                public void cycleEncodings() {
                    encodingsHandler.cycleEncodings();
                }

                @Override
                public void popupEncodingsMenu(MouseEvent mouseEvent) {
                    encodingsHandler.popupEncodingsMenu(mouseEvent);
                }
            });
            panel.setReleaseFileMethod(() -> {
                GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
                FileHandlingActionsApi fileHandlingActions = fileModule.getFileHandlingActions();
                return fileHandlingActions.releaseFile();
            });
        }

        return editorProvider;
    }

    public BinaryEditorProvider getMultiEditorProvider() {
        if (editorProvider == null) {
            GuiDockingModuleApi dockingModule = application.getModuleRepository().getModuleByInterface(GuiDockingModuleApi.class);
            editorProvider = new BinaryEditorHandler();
            ((BinaryEditorHandler) editorProvider).setBinaryPanelInit((BinaryPanel panel) -> {
                panel.setPopupMenu(createPopupMenu(panel.getId(), editorProvider.getCodeArea()));
                panel.setCodeAreaPopupMenuHandler(getCodeAreaPopupMenuHandler());
                panel.setGoToPositionAction(getGoToPositionHandler().getGoToRowAction());
                panel.setCopyAsCode(getClipboardCodeHandler().getCopyAsCodeAction());
                panel.setPasteFromCode(getClipboardCodeHandler().getPasteFromCodeAction());
                panel.setEncodingsHandler(new EncodingStatusHandler() {
                    @Override
                    public void cycleEncodings() {
                        encodingsHandler.cycleEncodings();
                    }

                    @Override
                    public void popupEncodingsMenu(MouseEvent mouseEvent) {
                        encodingsHandler.popupEncodingsMenu(mouseEvent);
                    }
                });
            });
            ((BinaryEditorHandler) editorProvider).setEditorViewHandling(dockingModule.getEditorViewHandling());
            ((BinaryEditorHandler) editorProvider).setSegmentsRepository(new SegmentsRepository());
            ((BinaryEditorHandler) editorProvider).init();
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            FileHandlingActionsApi fileHandlingActions = fileModule.getFileHandlingActions();
            fileHandlingActions.setFileHandler(editorProvider);
        }

        return editorProvider;
    }

    public void registerStatusBar() {
        binaryStatusPanel = new BinaryStatusPanel();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, BINARY_STATUS_BAR_ID, binaryStatusPanel);
        frameModule.switchStatusBar(BINARY_STATUS_BAR_ID);
        getEditorProvider().registerBinaryStatus(binaryStatusPanel);
        getEditorProvider().registerEncodingStatus(binaryStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(binaryStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);

        BinaryAppearanceService binaryAppearanceService;
        binaryAppearanceService = new BinaryAppearanceServiceImpl(this);

        binaryAppearanceOptionsPanel = new BinaryAppearanceOptionsPanel(binaryAppearanceService);
        optionsModule.extendAppearanceOptionsPanel(binaryAppearanceOptionsPanel);

        TextEncodingService textEncodingPanelApi = new TextEncodingService() {
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
                getEncodingsHandler().rebuildEncodings();
            }

            @Override
            public void setSelectedEncoding(String encoding) {
                if (encoding != null) {
                    getEditorProvider().setCharset(Charset.forName(encoding));
                }
            }
        };
        textEncodingOptionsPanel = new TextEncodingOptionsPanel(textEncodingPanelApi);
        textEncodingOptionsPanel.setAddEncodingsOperation((List<String> usedEncodings) -> {
            final List<String> result = new ArrayList<>();
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
            addEncodingPanel.setUsedEncodings(usedEncodings);
            DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
            JPanel dialogPanel = WindowUtils.createDialogPanel(addEncodingPanel, controlPanel);
            final DialogWrapper addEncodingDialog = frameModule.createDialog(dialogPanel);
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    result.addAll(addEncodingPanel.getEncodings());
                }

                addEncodingDialog.close();
            });
            frameModule.setDialogTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
            addEncodingDialog.showCentered(textEncodingOptionsPanel);
            addEncodingDialog.dispose();
            return result;
        });
        optionsModule.addOptionsPanel(textEncodingOptionsPanel);

        TextFontService textFontPanelApi = new TextFontService() {
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
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle(), controlPanel);
                frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                    if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            TextFontParameters parameters = new TextFontParameters(application.getAppPreferences());
                            parameters.setDefaultFont(false);
                            parameters.setFont(fontPanel.getStoredFont());
                        }
                        result.font = fontPanel.getStoredFont();
                    }

                    dialog.close();
                });
                dialog.showCentered(textFontOptionsPanel);
                dialog.dispose();

                return result.font;
            }

            class Result {

                Font font;
            }
        });
        optionsModule.addOptionsPanel(textFontOptionsPanel);

        editorOptionsPanel = new EditorOptionsPanel(new EditorOptionsPanelApi() {
            @Override
            public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
                getEditorProvider().setFileHandlingMode(fileHandlingMode);
            }

            @Override
            public void setIsShowValuesPanel(boolean showValuesPanel) {
                boolean valuesPanelVisible = getEditorProvider().isValuesPanelVisible();
                if (valuesPanelVisible != showValuesPanel) {
                    if (showValuesPanel) {
                        getEditorProvider().showValuesPanel();
                    } else {
                        getEditorProvider().hideValuesPanel();
                    }
                }
            }

            @Override
            public void setEditorHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode) {
                CodeAreaCommandHandler commandHandler = getEditorProvider().getCodeArea().getCommandHandler();
                ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(enterKeyHandlingMode);
            }
        });
        optionsModule.addOptionsPanel(editorOptionsPanel);

        CodeAreaOptionsPanelApi codeAreaOptionsPanelApi = getEditorProvider()::getCodeArea;
        codeAreaOptionsPanel = new CodeAreaOptionsPanel(codeAreaOptionsPanelApi);
        optionsModule.addOptionsPanel(codeAreaOptionsPanel);

        statusOptionsPanel = new StatusOptionsPanel((StatusOptions statusOptions) -> {
            statusOptionsPanel.loadFromOptions(statusOptions);
        });
        optionsModule.addOptionsPanel(statusOptionsPanel);

        themeProfilesOptionsPanel = new ThemeProfilesOptionsPanel(codeAreaOptionsPanelApi);
        optionsModule.addOptionsPanel(themeProfilesOptionsPanel);
        layoutProfilesOptionsPanel = new LayoutProfilesOptionsPanel(codeAreaOptionsPanelApi);
        optionsModule.addOptionsPanel(layoutProfilesOptionsPanel);
        colorProfilesOptionsPanel = new ColorProfilesOptionsPanel(codeAreaOptionsPanelApi);
        optionsModule.addOptionsPanel(colorProfilesOptionsPanel);
    }

    public void registerWordWrapping() {
        getRowWrappingHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, wordWrappingHandler.getViewLineWrapAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        getGoToPositionHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, goToRowHandler.getGoToRowAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public BinaryStatusPanel getBinaryStatusPanel() {
        return binaryStatusPanel;
    }

    @Nonnull
    public FindReplaceHandler getFindReplaceHandler() {
        if (findReplaceHandler == null) {
            findReplaceHandler = new FindReplaceHandler(application, getEditorProvider());
            findReplaceHandler.init();
        }

        return findReplaceHandler;
    }

    @Nonnull
    public ViewNonprintablesHandler getViewNonprintablesHandler() {
        if (viewNonprintablesHandler == null) {
            viewNonprintablesHandler = new ViewNonprintablesHandler(application, getEditorProvider());
            viewNonprintablesHandler.init();
        }

        return viewNonprintablesHandler;
    }

    @Nonnull
    public ViewValuesPanelHandler getViewValuesPanelHandler() {
        if (viewValuesPanelHandler == null) {
            viewValuesPanelHandler = new ViewValuesPanelHandler(application, getEditorProvider());
            viewValuesPanelHandler.init();
        }

        return viewValuesPanelHandler;
    }

    @Nonnull
    public ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    @Nonnull
    public RowWrappingHandler getRowWrappingHandler() {
        if (wordWrappingHandler == null) {
            wordWrappingHandler = new RowWrappingHandler(application, getEditorProvider());
            wordWrappingHandler.init();
        }

        return wordWrappingHandler;
    }

    @Nonnull
    public GoToPositionHandler getGoToPositionHandler() {
        if (goToRowHandler == null) {
            goToRowHandler = new GoToPositionHandler(application, getEditorProvider());
            goToRowHandler.init();
        }

        return goToRowHandler;
    }

    @Nonnull
    public PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    @Nonnull
    public EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler(application, getBinaryStatusPanel()); // getEditorProvider(), 
            // encodingsHandler.init();
        }

        return encodingsHandler;
    }

    @Nonnull
    public PrintHandler getPrintHandler() {
        if (printHandler == null) {
            printHandler = new PrintHandler(application, getEditorProvider());
            printHandler.init();
        }

        return printHandler;
    }

    @Nonnull
    public ViewModeHandler getViewModeHandler() {
        if (viewModeHandler == null) {
            getResourceBundle();
            viewModeHandler = new ViewModeHandler(application, getEditorProvider());
            viewModeHandler.init();
        }

        return viewModeHandler;
    }

    @Nonnull
    public LayoutHandler getLayoutHandler() {
        if (layoutHandler == null) {
            getResourceBundle();
            layoutHandler = new LayoutHandler(application, getEditorProvider());
            layoutHandler.init();
        }

        return layoutHandler;
    }

    @Nonnull
    public CodeTypeHandler getCodeTypeHandler() {
        if (codeTypeHandler == null) {
            getResourceBundle();
            codeTypeHandler = new CodeTypeHandler(application, getEditorProvider());
            codeTypeHandler.init();
        }

        return codeTypeHandler;
    }

    @Nonnull
    public PositionCodeTypeHandler getPositionCodeTypeHandler() {
        if (positionCodeTypeHandler == null) {
            getResourceBundle();
            positionCodeTypeHandler = new PositionCodeTypeHandler(application, getEditorProvider());
            positionCodeTypeHandler.init();
        }

        return positionCodeTypeHandler;
    }

    @Nonnull
    public HexCharactersCaseHandler getHexCharactersCaseHandler() {
        if (hexCharactersCaseHandler == null) {
            getResourceBundle();
            hexCharactersCaseHandler = new HexCharactersCaseHandler(application, getEditorProvider());
            hexCharactersCaseHandler.init();
        }

        return hexCharactersCaseHandler;
    }

    @Nonnull
    public ClipboardCodeHandler getClipboardCodeHandler() {
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

    public void registerLayoutMenu() {
        getLayoutHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, layoutHandler.getShowHeaderAction(), new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, layoutHandler.getShowRowPositionAction(), new MenuPosition(PositionMode.BOTTOM));
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

    private JPopupMenu createPopupMenu(int postfix, ExtCodeArea codeArea) {
//        getClipboardCodeHandler();
        String popupMenuId = BINARY_POPUP_MENU_ID + "." + postfix;
//        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
//        menuModule.registerMenu(popupMenuId, MODULE_ID);
//        menuModule.registerClipboardMenuItems(popupMenuId, MODULE_ID, SeparationMode.AROUND);
//        menuModule.registerMenuItem(popupMenuId, MODULE_ID, clipboardCodeHandler.getCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getCopyAction().getValue(Action.NAME)));
//        menuModule.registerMenuItem(popupMenuId, MODULE_ID, clipboardCodeHandler.getPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) menuModule.getClipboardActions().getPasteAction().getValue(Action.NAME)));

        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, x, y);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                popupMenu.show(invoker, x, y);
            }
        };
//        menuModule.buildMenu(popupMenu, popupMenuId);
        return popupMenu;
    }

    private JPopupMenu createCodeAreaPopupMenu(final ExtCodeArea codeArea, String menuPostfix, int x, int y) {
        getClipboardCodeHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);

        BasicCodeAreaZone positionZone = codeArea.getPositionZone(x, y);

        final JPopupMenu popupMenu = new JPopupMenu();
        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER: {
                popupMenu.add(createShowHeaderMenuItem(codeArea));
                popupMenu.add(createPositionCodeTypeMenuItem(codeArea));
                break;
            }
            case ROW_POSITIONS: {
                popupMenu.add(createShowRowPositionMenuItem(codeArea));
                popupMenu.add(createPositionCodeTypeMenuItem(codeArea));
                popupMenu.add(new JSeparator());
                popupMenu.add(createGoToMenuItem());

                break;
            }
            default: {
                final JMenuItem cutMenuItem = new JMenuItem("Cut");
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
                cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                cutMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.cut();
                });
                popupMenu.add(cutMenuItem);

                final JMenuItem copyMenuItem = new JMenuItem("Copy");
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, metaMask));
                copyMenuItem.setEnabled(codeArea.hasSelection());
                copyMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copy();
                });
                popupMenu.add(copyMenuItem);

                final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
                copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
                copyAsCodeMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copyAsCode();
                });
                popupMenu.add(copyAsCodeMenuItem);

                final JMenuItem pasteMenuItem = new JMenuItem("Paste");
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, metaMask));
                pasteMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.paste();
                });
                popupMenu.add(pasteMenuItem);

                final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
                pasteFromCodeMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteFromCodeMenuItem.addActionListener((ActionEvent e) -> {
                    try {
                        codeArea.pasteFromCode();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                    }
                });
                popupMenu.add(pasteFromCodeMenuItem);

                final JMenuItem deleteMenuItem = new JMenuItem("Delete");
                deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                deleteMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                deleteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.delete();
                });
                popupMenu.add(deleteMenuItem);
                popupMenu.addSeparator();

                final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
                selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, metaMask));
                selectAllMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.selectAll();
                });
                popupMenu.add(selectAllMenuItem);
                popupMenu.addSeparator();

                JMenuItem goToMenuItem = createGoToMenuItem();
                popupMenu.add(goToMenuItem);

                final JMenuItem findMenuItem = new JMenuItem("Find...");
                findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, metaMask));
                findMenuItem.addActionListener((ActionEvent e) -> {
                    getFindReplaceHandler().getEditFindAction().actionPerformed(e);
                });
                popupMenu.add(findMenuItem);

                final JMenuItem replaceMenuItem = new JMenuItem("Replace...");
                replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, metaMask));
                replaceMenuItem.setEnabled(codeArea.isEditable());
                replaceMenuItem.addActionListener((ActionEvent e) -> {
                    getFindReplaceHandler().getEditReplaceAction().actionPerformed(e);
                });
                popupMenu.add(replaceMenuItem);
            }
        }

        popupMenu.addSeparator();

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER:
            case ROW_POSITIONS: {
                break;
            }
            default: {
                JMenu showMenu = new JMenu("Show");
                JMenuItem showHeader = createShowHeaderMenuItem(codeArea);
                showMenu.add(showHeader);
                JMenuItem showRowPosition = createShowRowPositionMenuItem(codeArea);
                showMenu.add(showRowPosition);
                popupMenu.add(showMenu);
            }
        }

        final JMenuItem optionsMenuItem = new JMenuItem("Options...");
        optionsMenuItem.addActionListener((ActionEvent e) -> {
            optionsModule.getOptionsAction().actionPerformed(e);
//            final BinEdOptionsPanelBorder optionsPanel = new BinEdOptionsPanelBorder();
//            optionsPanel.load();
//            optionsPanel.setApplyOptions(getApplyOptions());
//            OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
//            JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanel, optionsControlPanel);
//            DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, null, "Options", Dialog.ModalityType.MODELESS);
//            optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
//                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
//                    optionsPanel.store();
//                }
//                if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
//                    setApplyOptions(optionsPanel.getApplyOptions());
//                    encodingsHandler.setEncodings(optionsPanel.getApplyOptions().getCharsetOptions().getEncodings());
//                    codeArea.repaint();
//                }
//
//                dialog.close();
//            });
//            WindowUtils.assignGlobalKeyListener(dialog.getWindow(), optionsControlPanel.createOkCancelListener());
//            dialog.getWindow().setSize(650, 460);
//            dialog.show();
        });
        popupMenu.add(optionsMenuItem);

//        final Action copyAsCodeAction = clipboardCodeHandler.createCopyAsCodeAction(codeArea);
//        final Action pasteFromCodeAction = clipboardCodeHandler.createPasteFromCodeAction(codeArea);
//        ClipboardActions clipboardActions = menuModule.createClipboardActions(new ClipboardActionsHandler() {
//            @Override
//            public void performCut() {
//                codeArea.cut();
//            }
//
//            @Override
//            public void performCopy() {
//                codeArea.copy();
//            }
//
//            @Override
//            public void performPaste() {
//                codeArea.paste();
//            }
//
//            @Override
//            public void performDelete() {
//                codeArea.delete();
//            }
//
//            @Override
//            public void performSelectAll() {
//                codeArea.selectAll();
//            }
//
//            @Override
//            public boolean isSelection() {
//                return codeArea.hasSelection();
//            }
//
//            @Override
//            public boolean isEditable() {
//                return ((EditationModeCapable) codeArea).isEditable();
//            }
//
//            @Override
//            public boolean canSelectAll() {
//                return true;
//            }
//
//            @Override
//            public boolean canPaste() {
//                return codeArea.canPaste();
//            }
//
//            @Override
//            public void setUpdateListener(final ClipboardActionsUpdateListener updateListener) {
//                ((SelectionCapable) codeArea).addSelectionChangedListener((SelectionRange sr) -> {
//                    updateListener.stateChanged();
//                    copyAsCodeAction.setEnabled(codeArea.hasSelection());
//                    pasteFromCodeAction.setEnabled(codeArea.canPaste());
//                });
//                updateListener.stateChanged();
//            }
//        });
//        menuModule.registerClipboardMenuItems(clipboardActions, CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, SeparationMode.AROUND);
//        menuModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, copyAsCodeAction, new MenuPosition(NextToMode.AFTER, (String) clipboardActions.getCopyAction().getValue(Action.NAME)));
//        menuModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID, pasteFromCodeAction, new MenuPosition(NextToMode.AFTER, (String) clipboardActions.getPasteAction().getValue(Action.NAME)));
//
//        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID + menuPostfix);
        return popupMenu;
    }

    @Nonnull
    private JMenuItem createGoToMenuItem() {
        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, metaMask));
        goToMenuItem.addActionListener(goToRowHandler.getGoToRowAction());
        return goToMenuItem;
    }

    @Nonnull
    private JMenuItem createShowHeaderMenuItem(ExtCodeArea codeArea) {
        final JCheckBoxMenuItem showHeader = new JCheckBoxMenuItem("Show Header");
        showHeader.setSelected(Objects.requireNonNull(codeArea.getLayoutProfile()).isShowHeader());
        showHeader.addActionListener(layoutHandler.getShowHeaderAction());
        return showHeader;
    }

    @Nonnull
    private JMenuItem createShowRowPositionMenuItem(ExtCodeArea codeArea) {
        final JCheckBoxMenuItem showRowPosition = new JCheckBoxMenuItem("Show Row Position");
        showRowPosition.setSelected(Objects.requireNonNull(codeArea.getLayoutProfile()).isShowRowPosition());
        showRowPosition.addActionListener(layoutHandler.getShowRowPositionAction());
        return showRowPosition;
    }

    @Nonnull
    private JMenuItem createPositionCodeTypeMenuItem(ExtCodeArea codeArea) {
        JMenu menu = new JMenu("Position Code Type");
        PositionCodeType codeType = codeArea.getPositionCodeType();

        final JRadioButtonMenuItem octalCodeTypeMenuItem = new JRadioButtonMenuItem("Octal");
        octalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.OCTAL);
        octalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.OCTAL);
//                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.OCTAL);
            }
        });
        menu.add(octalCodeTypeMenuItem);

        final JRadioButtonMenuItem decimalCodeTypeMenuItem = new JRadioButtonMenuItem("Decimal");
        decimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.DECIMAL);
        decimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.DECIMAL);
//                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        });
        menu.add(decimalCodeTypeMenuItem);

        final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem("Hexadecimal");
        hexadecimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.HEXADECIMAL);
        hexadecimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.HEXADECIMAL);
//                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        });
        menu.add(hexadecimalCodeTypeMenuItem);

        return menu;
    }

    private void dropCodeAreaPopupMenu(String menuPostfix) {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }

    public void loadFromPreferences(Preferences preferences) {
        encodingsHandler.loadFromPreferences(new TextEncodingParameters(preferences));
        binaryStatusPanel.loadFromPreferences(new StatusParameters(preferences));

        if (textFontOptionsPanel != null) {
            textFontOptionsPanel.loadFromPreferences(preferences);
            textFontOptionsPanel.applyPreferencesChanges();
        }

        if (binaryAppearanceOptionsPanel != null) {
            binaryAppearanceOptionsPanel.loadFromPreferences(preferences);
            binaryAppearanceOptionsPanel.applyPreferencesChanges();
        }

        if (editorOptionsPanel != null) {
            editorOptionsPanel.loadFromPreferences(preferences);
            editorOptionsPanel.applyPreferencesChanges();
        }

        if (codeAreaOptionsPanel != null) {
            codeAreaOptionsPanel.loadFromPreferences(preferences);
            codeAreaOptionsPanel.applyPreferencesChanges();
        }

        if (statusOptionsPanel != null) {
            statusOptionsPanel.loadFromPreferences(preferences);
            statusOptionsPanel.applyPreferencesChanges();
        }

        if (themeProfilesOptionsPanel != null) {
            themeProfilesOptionsPanel.loadFromPreferences(preferences);
            themeProfilesOptionsPanel.applyPreferencesChanges();
        }

        if (layoutProfilesOptionsPanel != null) {
            layoutProfilesOptionsPanel.loadFromPreferences(preferences);
            layoutProfilesOptionsPanel.applyPreferencesChanges();
        }

        if (colorProfilesOptionsPanel != null) {
            colorProfilesOptionsPanel.loadFromPreferences(preferences);
            colorProfilesOptionsPanel.applyPreferencesChanges();
        }
    }

    public CodeAreaPopupMenuHandler getCodeAreaPopupMenuHandler() {
        if (codeAreaPopupMenuHandler == null) {
            codeAreaPopupMenuHandler = new CodeAreaPopupMenuHandler() {
                @Override
                public JPopupMenu createPopupMenu(ExtCodeArea codeArea, String menuPostfix, int x, int y) {
                    return createCodeAreaPopupMenu(codeArea, menuPostfix, x, y);
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
                if (component instanceof ExtCodeArea) {
                    if (((ExtCodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = getCodeAreaPopupMenuHandler();
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        int x;
                        int y;
                        Point point = component.getMousePosition();
                        if (point != null) {
                            x = (int) point.getX();
                            y = (int) point.getY();
                        } else {
                            x = mouseEvent.getX();
                            y = mouseEvent.getY();
                        }

                        popupMenu = handler.createPopupMenu((ExtCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

                        if (point != null) {
                            popupMenu.show(component, x, y);
                        } else {
                            popupMenu.show(mouseEvent.getComponent(), x, y);
                        }
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (component instanceof ExtCodeArea) {
                    if (((ExtCodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = getCodeAreaPopupMenuHandler();
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        Point point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                        int x = (int) point.getX();
                        int y = (int) point.getY();
                        popupMenu = handler.createPopupMenu((ExtCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

                        popupMenu.show(component, x, y);
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
