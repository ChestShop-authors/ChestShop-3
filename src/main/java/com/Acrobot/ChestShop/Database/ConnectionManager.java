package com.Acrobot.ChestShop.Database;

import java.io.File;

/**
 * @author Andrzej Pomirski (Acrobot)
 */
public class ConnectionManager {
    private static final String URI_STRING = "jdbc:sqlite:%s";

    public static String getURI(File databaseFile) {
        return String.format(URI_STRING, databaseFile.getAbsolutePath());
    }
}
