package com.lennardf1989.bukkitex;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Database {
    private final JavaPlugin javaPlugin;
    private ClassLoader classLoader;
    private Level loggerLevel;
    private boolean usingSQLite;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;

    /**
     * Create an instance of Database
     *
     * @param javaPlugin Plugin instancing this database
     */
    public Database(JavaPlugin javaPlugin) {
        //Store the JavaPlugin
        this.javaPlugin = javaPlugin;

        //Try to get the ClassLoader of the plugin using Reflection
        try {
            //Find the "getClassLoader" method and make it "public" instead of "protected"
            Method method = JavaPlugin.class.getDeclaredMethod("getClassLoader");
            method.setAccessible(true);

            //Store the ClassLoader
            this.classLoader = (ClassLoader) method.invoke(javaPlugin);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to retrieve the ClassLoader of the plugin using Reflection", ex);
        }
    }

    /**
     * Initialize the database using the passed arguments
     *
     * @param driver    Database-driver to use. For example: org.sqlite.JDBC
     * @param url       Location of the database. For example: jdbc:sqlite:{DIR}{NAME}.db
     * @param username  Username required to access the database
     * @param password  Password belonging to the username, may be empty
     * @param isolation Isolation type. For example: SERIALIZABLE, also see TransactionIsolation
     * @param logging   If set to false, all logging will be disabled
     * @param rebuild   If set to true, all tables will be dropped and recreated. Be sure to create a backup before doing so!
     */
    public void initializeDatabase(String driver, String url, String username, String password, String isolation, boolean logging, boolean rebuild) {
        //Logging needs to be set back to the original level, no matter what happens
        try {
            //Disable all logging
            disableDatabaseLogging(logging);

            //Prepare the database
            prepareDatabase(driver, url, username, password, isolation);

            //Load the database
            loadDatabase();

            //Create all tables
            installDatabase(rebuild);
        } catch (Exception ex) {
            throw new RuntimeException("An exception has occured while initializing the database", ex);
        } finally {
            //Enable all logging
            enableDatabaseLogging(logging);
        }
    }

    private void prepareDatabase(String driver, String url, String username, String password, String isolation) {
        //Setup the data source
        DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(driver);
        ds.setUrl(replaceDatabaseString(url));
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setIsolationLevel(TransactionIsolation.getLevel(isolation));

        //Setup the server configuration
        ServerConfig sc = new ServerConfig();
        sc.setDefaultServer(false);
        sc.setRegister(false);
        sc.setName(ds.getUrl().replaceAll("[^a-zA-Z0-9]", ""));

        //Get all persistent classes
        List<Class<?>> classes = getDatabaseClasses();

        //Do a sanity check first
        if (classes.isEmpty()) throw new RuntimeException("Database has been enabled, but no classes are registered to it");

        //Register them with the EbeanServer
        sc.setClasses(classes);

        //Check if the SQLite JDBC supplied with Bukkit is being used
        if (ds.getDriver().equalsIgnoreCase("org.sqlite.JDBC")) {
            //Remember the database is a SQLite-database
            usingSQLite = true;

            //Modify the platform, as SQLite has no AUTO_INCREMENT field
            sc.setDatabasePlatform(new SQLitePlatform());
            sc.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        //Finally the data source
        sc.setDataSourceConfig(ds);

        //Store the ServerConfig
        serverConfig = sc;
    }

    private void loadDatabase() {
        //Declare a few local variables for later use
        ClassLoader currentClassLoader = null;
        Field cacheField = null;
        boolean cacheValue = true;

        try {
            //Store the current ClassLoader, so it can be reverted later
            currentClassLoader = Thread.currentThread().getContextClassLoader();

            //Set the ClassLoader to Plugin ClassLoader
            Thread.currentThread().setContextClassLoader(classLoader);

            //Get a reference to the private static "defaultUseCaches"-field in URLConnection
            cacheField = URLConnection.class.getDeclaredField("defaultUseCaches");

            //Make it accessible, store the default value and set it to false
            cacheField.setAccessible(true);
            cacheValue = cacheField.getBoolean(null);
            cacheField.setBoolean(null, false);

            //Setup Ebean based on the configuration
            ebeanServer = EbeanServerFactory.create(serverConfig);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create a new instance of the EbeanServer", ex);
        } finally {
            //Revert the ClassLoader back to its original value
            if (currentClassLoader != null) Thread.currentThread().setContextClassLoader(currentClassLoader);

            //Revert the "defaultUseCaches"-field in URLConnection back to its original value
            try {
                if (cacheField != null) cacheField.setBoolean(null, cacheValue);
            } catch (Exception e) {
                System.out.println("Failed to revert the \"defaultUseCaches\"-field back to its original value, URLConnection-caching remains disabled.");
            }
        }
    }

    private void installDatabase(boolean rebuild) {
        //Check if the database has to be rebuild
        if (!rebuild) return;

        //Create a DDL generator
        SpiEbeanServer serv = (SpiEbeanServer) ebeanServer;
        DdlGenerator gen = serv.getDdlGenerator();

        //Check if the database already (partially) exists
        boolean databaseExists = false;

        for (Class<?> aClass : getDatabaseClasses()) {
            try {
                //Do a simple query which only throws an exception if the table does not exist
                ebeanServer.find(aClass).findRowCount();

                //Query passed without throwing an exception, a database therefore already exists
                databaseExists = true;
                break;
            } catch (Exception ignored) {}
        }

        if(databaseExists) return;

        //Fire "before drop" event
        try {
            beforeDropDatabase();
        } catch (Exception ex) {
            //If the database exists, dropping has to be canceled to prevent data-loss
            if (databaseExists) throw new RuntimeException("An unexpected exception occured", ex);
        }

        //Generate a DropDDL-script
        gen.runScript(true, gen.generateDropDdl());

        //If SQLite is being used, the database has to reloaded to release all resources
        if (usingSQLite) loadDatabase();

        //Generate a CreateDDL-script
        //If SQLite is being used, the CreateDLL-script has to be validated and potentially fixed to be valid
        gen.runScript(false, (usingSQLite ? validateCreateDDLSqlite(gen.generateCreateDdl()) : gen.generateCreateDdl()));

        //Fire "after create" event
        try {
            afterCreateDatabase();
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected exception occured", ex);
        }
    }

    private String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", javaPlugin.getDataFolder().getPath().replaceAll("\\\\", "/") + '/');
        input = input.replaceAll("\\{NAME\\}", javaPlugin.getDescription().getName().replaceAll("[^\\w_-]", ""));

        return input;
    }

    private static String validateCreateDDLSqlite(String oldScript) {
        try {
            //Create a BufferedReader out of the potentially invalid script
            BufferedReader scriptReader = new BufferedReader(new StringReader(oldScript));

            //Create an array to store all the lines
            List<String> scriptLines = new ArrayList<String>();

            //Create some additional variables for keeping track of tables
            HashMap<String, Integer> foundTables = new HashMap<String, Integer>();
            String currentTable = null;
            int tableOffset = 0;

            //Loop through all lines
            String currentLine;
            while ((currentLine = scriptReader.readLine()) != null) {
                //Trim the current line to remove trailing spaces
                currentLine = currentLine.trim();

                //Add the current line to the rest of the lines
                scriptLines.add(currentLine.trim());

                //Check if the current line is of any use
                if (currentLine.startsWith("create table")) {
                    //Found a table, so get its name and remember the line it has been encountered on
                    currentTable = currentLine.split(" ", 4)[2];
                    foundTables.put(currentLine.split(" ", 3)[2], scriptLines.size() - 1);
                } else if (currentLine.length() > 0 && currentLine.charAt(0) == ';' && currentTable != null && !currentTable.isEmpty()) {
                    //Found the end of a table definition, so update the entry
                    int index = scriptLines.size() - 1;
                    foundTables.put(currentTable, index);

                    //Remove the last ")" from the previous line
                    String previousLine = scriptLines.get(index - 1);
                    previousLine = previousLine.substring(0, previousLine.length() - 1);
                    scriptLines.set(index - 1, previousLine);

                    //Change ";" to ");" on the current line
                    scriptLines.set(index, ");");

                    //Reset the table-tracker
                    currentTable = null;
                } else if (currentLine.startsWith("alter table")) {
                    //Found a potentially unsupported action
                    String[] alterTableLine = currentLine.split(" ", 4);

                    if (alterTableLine[3].startsWith("add constraint")) {
                        //Found an unsupported action: ALTER TABLE using ADD CONSTRAINT
                        String[] addConstraintLine = alterTableLine[3].split(" ", 4);

                        //Check if this line can be fixed somehow
                        if (addConstraintLine[3].startsWith("foreign key")) {
                            //Calculate the index of last line of the current table
                            int tableLastLine = foundTables.get(alterTableLine[2]) + tableOffset;

                            //Add a "," to the previous line
                            scriptLines.set(tableLastLine - 1, scriptLines.get(tableLastLine - 1) + ',');

                            //Add the constraint as a new line - Remove the ";" on the end
                            String constraintLine = String.format("%s %s %s", addConstraintLine[1], addConstraintLine[2], addConstraintLine[3]);
                            scriptLines.add(tableLastLine, constraintLine.substring(0, constraintLine.length() - 1));

                            //Remove this line and raise the table offset because a line has been inserted
                            scriptLines.remove(scriptLines.size() - 1);
                            tableOffset++;
                        } else {
                            //Exception: This line cannot be fixed but is known the be unsupported by SQLite
                            throw new RuntimeException("Unsupported action encountered: ALTER TABLE using ADD CONSTRAINT with " + addConstraintLine[3]);
                        }
                    }
                }
            }

            //Turn all the lines back into a single string
            StringBuilder newScript = new StringBuilder(5);
            for (String newLine : scriptLines) newScript.append(newLine).append('\n');

            //Print the new script
            System.out.println(newScript);

            //Return the fixed script
            return newScript.toString();
        } catch (Exception ex) {
            //Exception: Failed to fix the DDL or something just went plain wrong
            throw new RuntimeException("Failed to validate the CreateDDL-script for SQLite", ex);
        }
    }

    private void disableDatabaseLogging(boolean logging) {
        //If logging is allowed, nothing has to be changed
        if (logging) return;

        //Retrieve the level of the root logger
        loggerLevel = Logger.getLogger("").getLevel();

        //Set the level of the root logger to OFF
        Logger.getLogger("").setLevel(Level.OFF);
    }

    private void enableDatabaseLogging(boolean logging) {
        //If logging is allowed, nothing has to be changed
        if (logging) return;

        //Set the level of the root logger back to the original value
        Logger.getLogger("").setLevel(loggerLevel);
    }

    /**
     * Get a list of classes which should be registered with the EbeanServer
     *
     * @return List List of classes which should be registered with the EbeanServer
     */
    protected List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
    }

    /**
     * Method called before the loaded database is being dropped
     */
    protected void beforeDropDatabase() {
    }

    /**
     * Method called after the loaded database has been created
     */
    protected void afterCreateDatabase() {
    }

    /**
     * Get the instance of the EbeanServer
     *
     * @return EbeanServer Instance of the EbeanServer
     */
    public EbeanServer getDatabase() {
        return ebeanServer;
    }
}
