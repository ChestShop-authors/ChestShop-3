package com.Acrobot.Breeze.Database;

import com.Acrobot.ChestShop.ChestShop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;

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

    public Database(String uri) {
        this.uri = uri;
        this.username = null;
        this.password = null;
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
     * Creates a table from a given class
     *
     * @param clazz Class with fields
     * @return If table was succesfully created
     */
    public boolean createFromClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class) || !clazz.isAnnotationPresent(javax.persistence.Table.class)) {
            return false;
        }

        String tableName = clazz.getAnnotation(javax.persistence.Table.class).name();
        Table table = getTable(tableName);

        EntityParser parser = new EntityParser(clazz);

        String fields = parser.parseToString();

        try {
            table.create(fields);
        } catch (SQLException e) {
            Logger.getLogger("Database").log(Level.SEVERE, "Error while creating database from " + clazz.getName() + " (" + fields + ")", e);
            return false;
        }

        return true;
    }

    /**
     * @return Connection to the database
     * @throws SQLException exception
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(uri, username, password);
    }
}
