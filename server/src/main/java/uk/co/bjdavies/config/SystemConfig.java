package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.ISystemConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: SystemConfig.java
 * Compiled Class Name: SystemConfig.class
 * Date Created: 31/01/2018
 */

public class SystemConfig implements ISystemConfig {
    /**
     * This is the server's version that is currently running this is a preset version it cannot be changed unless changed by the developer.
     */
    private final String serverVersion = "1.0.0";
    /**
     * This is the password used to encrypt the server's JWT tokens for authentication between communication of both client and server.
     */
    private String JWTTokenPassword;
    /**
     * This will show all the logs for the discord client and other problems that occur throughout the program and all the info.
     */
    private boolean debug;


    /**
     * This will return the JWTTokenPassword.
     *
     * @return String
     */
    public String getJWTTokenPassword() {
        return JWTTokenPassword;
    }

    /**
     * This will return the serverVersion
     *
     * @return String
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * This will return if debug is on.
     *
     * @return boolean
     */
    public boolean isDebugOn() {
        return debug;
    }

}
