package com.Acrobot.Breeze.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database class, which can be used to connect to JDBC
 *
 * @author Acrobot
 */
public class Database {
    private String uri;
    private String username;
    private String password;

    public Database(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
    }


    /**
     * Gets the table with given name, even if it doesn't exist in the database
     *
     * @param name Table's name
     * @return Table
     */
    public Table getTable(String name) {
        return new Table(this, name);
    }

    /**
     * @return Connection to the database
     * @throws SQLException exception
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(uri, username, password);
    }
}
