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
package org.exbin.framework.editor.xbup;

import java.io.File;
import javax.annotation.Nonnull;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.client.api.ClientConnectionListener;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.action.api.ToolBarGroup;
import org.exbin.framework.gui.action.api.ToolBarPosition;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.service.XBFileType;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * XBUP editor module.
 *
 * @version 0.2.1 2019/06/22
 * @author ExBin Project (http://exbin.org)
 */
public class EditorXbupModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorXbupModule.class);
    public static final String XB_FILE_TYPE = "XBEditor.XBFileType";

    public static final String XBUP_POPUP_MENU_ID = MODULE_ID + ".xbupPopupMenu";
    private static final String EDIT_ITEM_MENU_GROUP_ID = MODULE_ID + ".editItemMenuGroup";
    private static final String EDIT_ITEM_TOOL_BAR_GROUP_ID = MODULE_ID + ".editItemToolBarGroup";
    private static final String VIEW_MODE_MENU_GROUP_ID = MODULE_ID + ".viewModeMenuGroup";
    public static final String DOC_STATUS_BAR_ID = "docStatusBar";
    public static final String SAMPLE_FILE_SUBMENU_ID = MODULE_ID + ".sampleFileSubMenu";

    private XBApplication application;
    private EditorProvider editorProvider;
    private XBACatalog catalog;
    private XBUndoHandler undoHandler;

    private DocEditingHandler docEditingHandler;
    private ViewModeHandler viewModeHandler;
    private StatusPanelHandler statusPanelHandler;
    private SampleFilesHandler sampleFilesHandler;
    private CatalogBrowserHandler catalogBrowserHandler;
    private PropertiesHandler propertiesHandler;
    private ImportExportHandler importExportHandler;

    private boolean devMode;

    public EditorXbupModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public EditorProvider getEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new DocumentViewerProvider(undoHandler);
            ((DocumentViewerProvider) editorProvider).setApplication(application);
            ((DocumentViewerProvider) editorProvider).setDevMode(devMode);

            final DocumentViewerProvider docPanel = (DocumentViewerProvider) editorProvider;

//            docPanel.getComponentPanel().setPopupMenu(createPopupMenu());
//            docPanel.addUpdateListener((ActionEvent e) -> {
//                if (docEditingHandler != null) {
//                    docEditingHandler.setAddEnabled(docPanel.isAddEnabled());
//                    docEditingHandler.setEditEnabled(docPanel.isEditEnabled());
//                    propertiesHandler.setEditEnabled(docPanel.isEditEnabled());
//                }
//            });
        }

        return editorProvider;
    }

    public void registerFileTypes() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.addFileType(new XBFileType());
    }

    private DocEditingHandler getDocEditingHandler() {
        if (docEditingHandler == null) {
            docEditingHandler = new DocEditingHandler(application, editorProvider);
            docEditingHandler.init();
        }

        return docEditingHandler;
    }

    private ViewModeHandler getViewModeHandler() {
        if (viewModeHandler == null) {
            viewModeHandler = new ViewModeHandler(application, editorProvider);
            viewModeHandler.init();
        }

        return viewModeHandler;
    }

    private StatusPanelHandler getStatusPanelHandler() {
        if (statusPanelHandler == null) {
            statusPanelHandler = new StatusPanelHandler(application, editorProvider);
            statusPanelHandler.init();
        }

        return statusPanelHandler;
    }

    private SampleFilesHandler getSampleFilesHandler() {
        if (sampleFilesHandler == null) {
            sampleFilesHandler = new SampleFilesHandler(application, editorProvider);
            sampleFilesHandler.init();
        }

        return sampleFilesHandler;
    }

    private CatalogBrowserHandler getCatalogBrowserHandler() {
        if (catalogBrowserHandler == null) {
            catalogBrowserHandler = new CatalogBrowserHandler(application, editorProvider);
            catalogBrowserHandler.init();
            catalogBrowserHandler.setCatalog(catalog);
        }

        return catalogBrowserHandler;
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, editorProvider);
            propertiesHandler.init();
            propertiesHandler.setDevMode(devMode);
        }

        return propertiesHandler;
    }

    private ImportExportHandler getImportExportHandler() {
        if (importExportHandler == null) {
            importExportHandler = new ImportExportHandler(application, editorProvider);
            importExportHandler.init();
        }

        return importExportHandler;
    }

    public void registerDocEditingMenuActions() {
        getDocEditingHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_ITEM_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, docEditingHandler.getAddItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, docEditingHandler.getEditItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
    }

    public void registerDocEditingToolBarActions() {
        getDocEditingHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_ITEM_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, docEditingHandler.getAddItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, docEditingHandler.getEditItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
    }

    public void registerViewModeMenu() {
        getViewModeHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_MODE_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowViewTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowPropertiesTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowTextTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowBinaryTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
    }

    public void registerStatusBar() {
        getStatusPanelHandler();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, DOC_STATUS_BAR_ID, statusPanelHandler.getDocStatusPanel());
        frameModule.switchStatusBar(DOC_STATUS_BAR_ID);
        // ((XBDocumentPanel) getEditorProvider()).registerTextStatus(docStatusPanel);
    }

    public void registerSampleFilesSubMenuActions() {
        getSampleFilesHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(SAMPLE_FILE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, SAMPLE_FILE_SUBMENU_ID, "Open Sample File", new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleHtmlFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSamplePictureFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleTypesFileAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerCatalogBrowserMenu() {
        getCatalogBrowserHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, catalogBrowserHandler.getCatalogBrowserAction(), new MenuPosition(PositionMode.TOP));
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        // TODO
    }

    public void registerPropertiesMenuAction() {
        getPropertiesHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, propertiesHandler.getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    @Nonnull
    public JPopupMenu createItemPopupMenu() {
        getPropertiesHandler();
        getDocEditingHandler();
        getImportExportHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(XBUP_POPUP_MENU_ID, MODULE_ID);
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, docEditingHandler.getAddItemAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, docEditingHandler.getEditItemAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerClipboardMenuItems(XBUP_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);

        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, importExportHandler.getImportItemAction(), new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, importExportHandler.getExportItemAction(), new MenuPosition(PositionMode.BOTTOM));

        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, propertiesHandler.getItemPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
        JPopupMenu popupMenu = new JPopupMenu();
        actionModule.buildMenu(popupMenu, XBUP_POPUP_MENU_ID);
        return popupMenu;
    }

    public ClientConnectionListener getClientConnectionListener() {
        return getStatusPanelHandler().getClientConnectionListener();
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        ((DocumentViewerProvider) editorProvider).setCatalog(catalog);
        if (catalogBrowserHandler != null) {
            catalogBrowserHandler.setCatalog(catalog);
        }
    }

    public void setUndoHandler(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        ((DocumentViewerProvider) editorProvider).setPluginRepository(pluginRepository);
    }

    /**
     * FileFilter for *.xb* files.
     */
    public class XBFileFilter extends FileFilter implements FileType {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String extension = getExtension(file);
            if (extension != null) {
                if (extension.length() >= 2) {
                    return extension.substring(0, 2).equals("xb");
                }
            }

            return false;
        }

        @Override
        public String getDescription() {
            return "All XB Files (*.xb*)";
        }

        @Override
        public String getFileTypeId() {
            return XB_FILE_TYPE;
        }
    }

    /**
     * Gets the extension part of file name.
     *
     * @param file Source file
     * @return extension part of file name
     */
    public static String getExtension(File file) {
        String ext = null;
        String str = file.getName();
        int i = str.lastIndexOf('.');

        if (i > 0 && i < str.length() - 1) {
            ext = str.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
