package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.IPluginConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: ModuleConfig.java
 * Compiled Class Name: ModuleConfig.class
 * Date Created: 31/01/2018
 */

public class PluginConfig implements IPluginConfig {
    /**
     * This is the location that the plugin's Jar file and configs are kept
     * Keep in mind there is a convention for the structure. Convention over Configuration approach.
     * e.g. AudioDJ -> AudioDJ.jar , AudioDJ.json
     */
    private String pluginLocation;

    /**
     * This is the class path that the class that implements
     */
    private String pluginClassPath;

    /**
     * This is the type of module it is either "js" or "java" <strong>default</strong> is java
     */
    private String pluginType;


    @Override
    public String getPluginLocation() {
        return pluginLocation;
    }

    @Override
    public String getPluginClassPath() {
        return pluginClassPath;
    }

    @Override
    public String getPluginType() {
        return pluginType;
    }
}
