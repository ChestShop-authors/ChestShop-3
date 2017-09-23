package com.Acrobot.Breeze.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Represents a table in database
 *
 * @author Acrobot
 */
public class Table {
    private final Database database;
    private final String name;

    private final String SELECT_ALL;
    private final String SELECT_STATEMENT;

    private final String INSERT_VALUES;
    private final String UPDATE;

    private final String CREATE;


    public Table(Database database, String name) {
        this.database = database;
        this.name = name;

        SELECT_ALL = "SELECT * FROM " + name;
        SELECT_STATEMENT = "SELECT * FROM " + name + " WHERE %s";

        INSERT_VALUES = "INSERT OR IGNORE INTO " + name + " VALUES (%s)";
        UPDATE = "UPDATE " + name + " SET %s WHERE %s";

        CREATE = "CREATE TABLE IF NOT EXISTS " + name + " (%s)";
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

        results.close();
        statement.close();
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
        return (!rs.isEmpty() ? rs.get(0) : new Row());
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
            String format = '\'' + row.getValues().stream().collect(Collectors.joining("', ")) + '\'';
            statement = String.format(INSERT_VALUES, format);
        } else {
            String format = row.getKeysAndValues().entrySet().stream()
                    .map(e -> e.getKey() + "= '" + e.getValue() + "'")
                    .collect(Collectors.joining(", "));
            statement = String.format(UPDATE, format, condition);
        }

        Connection connection = database.getConnection();
        Statement stm = connection.createStatement();

        stm.executeUpdate(statement);

        stm.close();
        connection.close();
    }

    /**
     * Inserts a row into the table
     *
     * @param statement Row to insert
     * @throws SQLException exception
     */
    public void insertRow(String statement) throws SQLException {
        insertRow(statement, null);
    }

    /**
     * Inserts a row into the table
     *
     * @param statement Row to insert
     * @param condition If the conditions are present, the row is updated when the conditions are met
     * @throws SQLException exception
     */
    public void insertRow(String statement, String condition) throws SQLException {
        if (condition == null || condition.isEmpty()) {
            statement = String.format(INSERT_VALUES, statement);
        } else {
            statement = String.format(UPDATE, statement, condition);
        }

        Connection connection = database.getConnection();
        Statement stm = connection.createStatement();

        stm.executeUpdate(statement);

        connection.close();
        stm.close();
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

        stm.close();
        connection.close();
    }

    /**
     * Returns this table's name
     *
     * @return Table's name
     */
    public String getName() {
        return name;
    }
}