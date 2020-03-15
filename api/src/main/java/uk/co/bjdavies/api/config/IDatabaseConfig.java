package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IDatabaseConfig {

    /**
     * This is the type of the database.
     * Only be:
     * SQLite
     * MySQL
     * PgSQL
     * MongoDB
     * Note: Not case sensitive.
     * Note: The database will not accept any other kind.
     *
     * @return String
     */
    String getType();

    /**
     * User name to connect to the db.
     *
     * @return String
     */
    String getUsername();

    /**
     * Password to connect to the database.
     *
     * @return String
     */
    String getPassword();

    /**
     * The hostname to connect to the database.
     *
     * @return String
     */
    String getHostname();

    /**
     * The port to connect to the database. default 3306
     *
     * @return String
     */
    String getPort();

    /**
     * Get the database
     * NOTE: this is where you put the .db file for Sqlite.
     *
     * @return String
     */
    String getDatabase();
}
