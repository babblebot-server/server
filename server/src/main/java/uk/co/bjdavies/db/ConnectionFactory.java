package uk.co.bjdavies.db;

import uk.co.bjdavies.api.config.IDatabaseConfig;
import uk.co.bjdavies.api.db.IConnection;
import uk.co.bjdavies.db.impl.SqliteConnection;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ConnectionFactory {
    public static IConnection make(IDatabaseConfig databaseConfig) {
        if (databaseConfig == null) {
            throw new RuntimeException("Must have a database config in your config! all you need is \"database\": {} the server defaults to sqlite and using Core.db");
        }
        switch (getTypeFromString(databaseConfig.getType())) {
            case SQLITE:
                return new SqliteConnection(databaseConfig);
            default:
                throw new RuntimeException("Connection type not supported please choose from PG, SQLITE, MYSQL, " +
                        "and MongoDB");
        }
    }

    private static ConnectionType getTypeFromString(String type) {
        switch (type.toLowerCase()) {
            case "sqlite":
                return ConnectionType.SQLITE;
            default:
                throw new RuntimeException("Connection type not supported please choose from PG, SQLITE, MYSQL, " +
                        "and MongoDB");
        }
    }

    private enum ConnectionType {
        MYSQL,
        SQLITE,
        PG,
        MONGODB
    }

}
