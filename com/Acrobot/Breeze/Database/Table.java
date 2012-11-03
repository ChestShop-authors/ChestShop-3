package com.Acrobot.Breeze.Database;

import java.sql.*;

/**
 * Represents a table in database
 *
 * @author Acrobot
 */
public class Table {
    private Database database;
    private String name;

    private final String SELECT_ALL = "SELECT * FROM " + name;
    private final String SELECT_STATEMENT = "SELECT * FROM " + name + " WHERE 's'";

    private final String INSERT_VALUES = "INSERT INTO " + name + " VALUES ('s')";
    private final String UPDATE = "UPDATE " + name + " SET 's' WHERE 's'";

    private final String CREATE = "CREATE TABLE IF NOT EXISTS " + name + " ('s')";


    public Table(Database database, String name) {
        this.database = database;
        this.name = name;
    }

    /**
     * Executes a select statement
     *
     * @param criteria Criteria to select
     * @return RowSet of results
     * @throws SQLException
     */
    private RowSet select(String criteria) throws SQLException {
        Connection connection = database.getConnection();

        Statement statement = connection.createStatement();

        String query = criteria == null || criteria.isEmpty() ? SELECT_ALL : SELECT_STATEMENT;
        query = String.format(query, criteria);

        ResultSet results = statement.executeQuery(query);
        ResultSetMetaData metaData = results.getMetaData();

        RowSet rowSet = new RowSet();
        int columnCount = metaData.getColumnCount();

        while (results.next()) {
            Row row = new Row();

            for (int i = 1; i <= columnCount; i++) {
                String name = metaData.getColumnName(i);
                String value = results.getString(i);
                row.put(name, value);
            }

            rowSet.add(row);
        }

        connection.close();

        return rowSet;
    }

    /**
     * Gets the first row from the given statement
     *
     * @param criteria Criteria for the statement, "SELECT * FROM table_name WHERE ......."
     * @return First row of the result set
     * @throws SQLException exception
     */
    public Row getRow(String criteria) throws SQLException {
        RowSet rs = select(criteria);
        return rs.get(0);
    }

    /**
     * Gets all rows from the given statement
     *
     * @param criteria Criteria for the statement, "SELECT * FROM table_name WHERE ......."
     * @return Result set
     * @throws SQLException exception
     */
    public RowSet getRows(String criteria) throws SQLException {
        return select(criteria);
    }

    /**
     * Gets all the rows of this table
     *
     * @return All rows of this table
     * @throws SQLException exception
     */
    public RowSet getRows() throws SQLException {
        return getRows(null);
    }

    /**
     * Inserts a row into the table
     *
     * @param row Row to insert
     * @throws SQLException exception
     */
    public void insertRow(Row row) throws SQLException {
        insertRow(row, null);
    }

    /**
     * Inserts a row into the table
     *
     * @param row       Row to insert
     * @param condition If the conditions are present, the row is updated when the conditions are met
     * @throws SQLException exception
     */
    public void insertRow(Row row, String condition) throws SQLException {
        String statement;

        if (condition == null || condition.isEmpty()) {
            statement = String.format(INSERT_VALUES, row.stringOfValues());
        } else {
            StringBuilder builder = new StringBuilder(30);

            for (int i = 0; i < row.getSize(); ++i) {
                String key = row.getKey(i);
                String value = row.get(i);

                builder.append(key).append("= '").append(value).append('\'');

                if (i != row.getSize() - 1) {
                    builder.append(", ");
                }
            }

            statement = String.format(UPDATE, builder.toString(), condition);
        }

        Connection connection = database.getConnection();
        Statement stm = connection.createStatement();

        stm.executeUpdate(statement);
    }

    /**
     * Creates a table with given fields. If the table already exists, nothing happens
     *
     * @param fields Fields of the table
     */
    public void create(String fields) throws SQLException {
        String statement = String.format(CREATE, fields);

        Connection connection = database.getConnection();
        Statement stm = connection.createStatement();

        stm.executeUpdate(statement);
    }
}