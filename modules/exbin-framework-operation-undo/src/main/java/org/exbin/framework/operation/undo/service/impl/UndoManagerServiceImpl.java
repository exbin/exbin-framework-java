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
package org.exbin.framework.operation.undo.service.impl;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import org.exbin.framework.operation.undo.gui.UndoManagerPanel;
import org.exbin.framework.operation.undo.service.UndoManagerService;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.XBTDocOperation;
import org.exbin.xbup.operation.XBTOpDocCommand;

/**
 * Undo manager service implementation.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoManagerServiceImpl implements UndoManagerService {

    @Override
    public void exportCommand(Component parentComponent, Command command) {
        if (command instanceof XBTOpDocCommand) {
            JFileChooser exportFileChooser = new JFileChooser();
            exportFileChooser.setAcceptAllFileFilterUsed(true);
            if (exportFileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                FileOutputStream fileStream;
                try {
                    fileStream = new FileOutputStream(exportFileChooser.getSelectedFile().getAbsolutePath());
                    try {
                        Optional<XBTDocOperation> operation = ((XBTOpDocCommand) command).getOperation();
                        if (operation.isPresent()) {
                            operation.get().getData().saveToStream(fileStream);
                        }
                    } finally {
                        fileStream.close();
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(UndoManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(UndoManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
