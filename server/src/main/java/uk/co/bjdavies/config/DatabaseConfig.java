package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.IDatabaseConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class DatabaseConfig implements IDatabaseConfig {

    private final String type = "sqlite";
    private final String database = "Core.db";
    private String username;
    private String password;
    private String hostname;
    private String port;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getDatabase() {
        return database;
    }
}
