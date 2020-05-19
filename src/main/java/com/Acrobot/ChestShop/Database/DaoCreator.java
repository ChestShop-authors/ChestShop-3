package com.Acrobot.ChestShop.Database;

import com.Acrobot.ChestShop.ChestShop;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.LruObjectCache;
import com.j256.ormlite.db.SqliteDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.security.InvalidParameterException;
import java.sql.SQLException;

/**
 * Creates a DAO appropriate for the plugin
 *
 * @author Andrzej Pomirski
 */
public class DaoCreator {

    /**
     * Returns a DAO for the given entity and with the given ID
     *
     * @param entity   Entity's class
     * @param <ENTITY> Type of the entity
     * @return Dao
     * @throws InvalidParameterException
     * @throws SQLException
     */
    public static <ENTITY, ID> Dao<ENTITY, ID> getDao(Class<ENTITY> entity) throws InvalidParameterException, SQLException {
        if (!entity.isAnnotationPresent(DatabaseFileName.class)) {
            throw new InvalidParameterException("Entity not annotated with @DatabaseFileName!");
        }

        String fileName = entity.getAnnotation(DatabaseFileName.class).value();
        String uri = ConnectionManager.getURI(ChestShop.loadFile(fileName));

        ConnectionSource connectionSource = new JdbcConnectionSource(uri, new SqliteDatabaseType());

        Dao<ENTITY, ID> dao = DaoManager.createDao(connectionSource, entity);
        dao.setObjectCache(new LruObjectCache(200));

        return dao;
    }

    /**
     * Creates a dao as well as a default table, if doesn't exist
     *
     * @throws SQLException
     * @throws InvalidParameterException
     * @see #getDao(Class)
     */
    public static <ENTITY, ID> Dao<ENTITY, ID> getDaoAndCreateTable(Class<ENTITY> entity) throws SQLException, InvalidParameterException {
        Dao<ENTITY, ID> dao = getDao(entity);

        TableUtils.createTableIfNotExists(dao.getConnectionSource(), entity);

        return dao;
    }
}
