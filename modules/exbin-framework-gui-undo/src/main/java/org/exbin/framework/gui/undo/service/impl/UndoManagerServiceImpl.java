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
package org.exbin.framework.gui.undo.service.impl;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exbin.framework.gui.undo.service.*;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import org.exbin.framework.gui.undo.panel.UndoManagerPanel;
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
