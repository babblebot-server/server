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


    private final boolean autoUpdate = true;

    /**
     * This will show all the logs for the discord client and other problems that occur throughout the program and all the info.
     */
    private final boolean debug = true;


    @Override
    public boolean isAutoUpdateOn() {
        return autoUpdate;
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
