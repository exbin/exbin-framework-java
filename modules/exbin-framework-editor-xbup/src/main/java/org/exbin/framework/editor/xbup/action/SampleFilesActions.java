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
package org.exbin.framework.editor.xbup.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupFileHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.xbup.core.parser.XBProcessingException;

/**
 * Sample files handler.
 *
 * @version 0.2.1 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SampleFilesActions {

    public static final String SAMPLE_HTML_FILE_ACTION_ID = "sampleHtmlFileAction";
    public static final String SAMPLE_PICTURE_FILE_ACTION_ID = "samplePictureFileAction";
    public static final String SAMPLE_TYPES_FILE_ACTION_ID = "sampleTypesFileAction";
    private static final String SAMPLE_FILES_DIR = "/org/exbin/framework/editor/xbup/resources/samples/";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action sampleHtmlFileAction;
    private Action samplePictureFileAction;
    private Action sampleTypesFileAction;

    public SampleFilesActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getSampleHtmlFileAction() {
        if (sampleHtmlFileAction == null) {
            sampleHtmlFileAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupEditorProvider) {
                        XbupEditorProvider provider = (XbupEditorProvider) editorProvider;
                        provider.newFile();
                        XbupFileHandler xbupFile = (XbupFileHandler) provider.getActiveFile().get();
                        try {
                            xbupFile.getDoc().fromStreamUB(getClass().getResourceAsStream(SAMPLE_FILES_DIR + "xhtml_example.xb"));
                            xbupFile.getDoc().processSpec();
                        } catch (XBProcessingException | IOException ex) {
                            Logger.getLogger(SampleFilesActions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        xbupFile.reportStructureChange(null);
//                    provider.updateItem();
                    }
                }
            };
            ActionUtils.setupAction(sampleHtmlFileAction, resourceBundle, SAMPLE_HTML_FILE_ACTION_ID);
        }
        return sampleHtmlFileAction;
    }

    @Nonnull
    public Action getSamplePictureFileAction() {
        if (samplePictureFileAction == null) {
            samplePictureFileAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupEditorProvider) {
                        XbupEditorProvider provider = (XbupEditorProvider) editorProvider;
                        provider.newFile();
                        XbupFileHandler xbupFile = (XbupFileHandler) provider.getActiveFile().get();
                        try {
                            xbupFile.getDoc().fromStreamUB(getClass().getResourceAsStream(SAMPLE_FILES_DIR + "xblogo.xbp"));
                            xbupFile.getDoc().processSpec();
                        } catch (XBProcessingException | IOException ex) {
                            Logger.getLogger(SampleFilesActions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        xbupFile.reportStructureChange(null);
//                    provider.updateItem();
                    }
                }
            };
            ActionUtils.setupAction(samplePictureFileAction, resourceBundle, SAMPLE_PICTURE_FILE_ACTION_ID);

        }
        return samplePictureFileAction;
    }

    @Nonnull
    public Action getSampleTypesFileAction() {
        if (sampleTypesFileAction == null) {
            sampleTypesFileAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupEditorProvider) {
                        XbupEditorProvider provider = (XbupEditorProvider) editorProvider;
                        provider.newFile();
                        XbupFileHandler xbupFile = (XbupFileHandler) provider.getActiveFile().get();
                        try {
                            xbupFile.getDoc().fromStreamUB(getClass().getResourceAsStream(SAMPLE_FILES_DIR + "xbtypes.xb"));
                            xbupFile.getDoc().processSpec();
                        } catch (XBProcessingException | IOException ex) {
                            Logger.getLogger(SampleFilesActions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        xbupFile.reportStructureChange(null);
//                    provider.updateItem();
                    }
                }
            };
            ActionUtils.setupAction(sampleTypesFileAction, resourceBundle, SAMPLE_TYPES_FILE_ACTION_ID);

        }
        return sampleTypesFileAction;
    }
}
