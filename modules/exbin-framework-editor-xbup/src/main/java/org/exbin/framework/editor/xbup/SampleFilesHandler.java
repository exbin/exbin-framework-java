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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.core.parser.XBProcessingException;

/**
 * Sample files handler.
 *
 * @version 0.2.1 2020/09/24
 * @author ExBin Project (http://exbin.org)
 */
public class SampleFilesHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action sampleHtmlFileAction;
    private Action samplePictureFileAction;
    private Action sampleTypesFileAction;

    public SampleFilesHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        sampleHtmlFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    DocumentViewerProvider provider = (DocumentViewerProvider) editorProvider;
                    provider.newFile();
                    try {
                        provider.getDoc().fromStreamUB(getClass().getResourceAsStream("/org/exbin/framework/editor/xbup/resources/samples/xhtml_example.xb"));
                        provider.getDoc().processSpec();
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(SampleFilesHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    provider.reportStructureChange(null);
//                    provider.updateItem();
                }
            }
        };
        ActionUtils.setupAction(sampleHtmlFileAction, resourceBundle, "sampleHtmlFileAction");

        samplePictureFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    DocumentViewerProvider provider = (DocumentViewerProvider) editorProvider;
                    provider.newFile();
                    try {
                        provider.getDoc().fromStreamUB(getClass().getResourceAsStream("/org/exbin/framework/editor/xbup/resources/samples/xblogo.xbp"));
                        provider.getDoc().processSpec();
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(SampleFilesHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    provider.reportStructureChange(null);
//                    provider.updateItem();
                }
            }
        };
        ActionUtils.setupAction(samplePictureFileAction, resourceBundle, "samplePictureFileAction");

        sampleTypesFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    DocumentViewerProvider provider = (DocumentViewerProvider) editorProvider;
                    provider.newFile();
                    try {
                        provider.getDoc().fromStreamUB(getClass().getResourceAsStream("/org/exbin/framework/editor/xbup/resources/samples/xbtypes.xb"));
                        provider.getDoc().processSpec();
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(SampleFilesHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    provider.reportStructureChange(null);
//                    provider.updateItem();
                }
            }
        };
        ActionUtils.setupAction(sampleTypesFileAction, resourceBundle, "sampleTypesFileAction");
    }

    @Nonnull
    public Action getSampleHtmlFileAction() {
        return sampleHtmlFileAction;
    }

    @Nonnull
    public Action getSamplePictureFileAction() {
        return samplePictureFileAction;
    }

    @Nonnull
    public Action getSampleTypesFileAction() {
        return sampleTypesFileAction;
    }
}
