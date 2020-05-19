package com.Acrobot.ChestShop.Database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents the filename of the database (inside ChestShop's folder)
 *
 * @author Andrzej Pomirski
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseFileName {
    String value();
}
