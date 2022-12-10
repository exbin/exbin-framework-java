/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.data.stub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exbin.framework.data.TableDataRow;
import org.exbin.framework.data.TableDataSource;
import org.exbin.framework.data.TableDataSource.ColumnDefinition;
import org.exbin.xbup.client.XBCatalogServiceClient;

/**
 * RPC Stub for data operations.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class DataStub {

    // public static long[] NODE_INFO_PROCEDURE = {0, 2, 11, 0, 0};
    private final XBCatalogServiceClient client;

    public DataStub(XBCatalogServiceClient client) {
        this.client = client;

        // TODO Remove later
        // Database Initialization
        String derbyPath = getClass().getPackage().getName().replace(".", "/");
        String derbyHome = System.getProperty("user.home") + "/.java/.userPrefs/" + derbyPath + "-dev";
        System.out.println("Derby home: " + derbyHome);
        System.setProperty("derby.system.home", derbyHome);

        Connection connection = getConnection();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE data_test (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, TEST1 VARCHAR(20), TEST2 VARCHAR(20))");
            stmt.execute("INSERT INTO data_test (TEST1, TEST2) VALUES ('Test 1', 'Test 2')");
            ResultSet rs = stmt.executeQuery("SELECT * FROM data_test");
            int rows = 0;
            while (rs.next()) {
                rows++;
            }
            System.out.println("Rows : " + rows);
        } catch (SQLException ex) {
            Logger.getLogger(DataStub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TableDataSource getTableDataSource(String tableSourceId) {
        return new RemoteTableDataSource(this, tableSourceId);
    }

    public List<ColumnDefinition> getColumDefinition(String tableSourceId) {
        List<TableDataSource.ColumnDefinition> columns = new ArrayList<>();
        TableDataSource.ColumnDefinition testColumn = new TableDataSource.ColumnDefinition() {
            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public Class<?> getValueClass() {
                return String.class;
            }
        };
        columns.add(testColumn);
        TableDataSource.ColumnDefinition testColumn2 = new TableDataSource.ColumnDefinition() {
            @Override
            public String getName() {
                return "Test2";
            }

            @Override
            public Class<?> getValueClass() {
                return String.class;
            }
        };
        columns.add(testColumn2);

        return columns;
    }

    public int getTableRowCount(String tableSourceId) {
        return 20;
    }

    public List<TableDataRow> getTableRows(String tableSourceId, int startRow, int rowCount) {
        int rowsToLoad = rowCount;
        if (startRow > 20) {
            rowsToLoad = 0;
        } else if (startRow + rowCount > 20) {
            rowsToLoad = 20 - startRow;
        }
        List<TableDataRow> rows = new ArrayList<>();
        for (int i = 0; i < rowsToLoad; i++) {
            final int rowIndex = startRow + i;
            TableDataRow data = new TableDataRow() {
                @Override
                public Object[] getRowData() {
                    return new String[]{"Cell " + rowIndex + ", 0", "Cell " + rowIndex + ", 1"};
                }
            };
            rows.add(data);
        }

        return rows;
    }

    public Connection getConnection() {

        Connection conn = null;
        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", "app");
            connectionProps.put("password", "app");
            conn = DriverManager.getConnection("jdbc:derby:testdb;create=true", connectionProps);

        } catch (SQLException ex) {
            Logger.getLogger(DataStub.class.getName()).log(Level.SEVERE, null, ex);
        }

        return conn;
    }
}
