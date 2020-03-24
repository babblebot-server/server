package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.*;

import java.util.Arrays;
import java.util.List;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: Config.java
 * Compiled Class Name: Config.class
 * Date Created: 30/01/2018
 */

public class Config implements IConfig {
    /**
     * This is the config settings for the discord part of this bot.
     */
    private DiscordConfig discord;


    /**
     * This is the config settings for the system part of this bot.
     */
    private SystemConfig system;


    /**
     * This is the config settings for the modules used in this bot.
     */
    private PluginConfig[] plugins;

    private DatabaseConfig database;

    private HttpConfig http;


    /**
     * This will return the config for the discord part of this bot.
     *
     * @return DiscordConfig
     */
    public IDiscordConfig getDiscordConfig() {
        return discord;
    }


    /**
     * This will return the config for the system part of this bot.
     *
     * @return SystemConfig
     */
    public ISystemConfig getSystemConfig() {
        return system;
    }

    @Override
    public IHttpConfig getHttpConfig() {
        return http;
    }

    /**
     * This will return database configuration for the bot.
     *
     * @return {@link IDatabaseConfig}
     */
    @Override
    public IDatabaseConfig getDatabaseConfig() {
        return database;
    }


    /**
     * This will return the config for the modules used in this bot.
     *
     * @return Collection(ModuleConfig)
     */
    public List<IPluginConfig> getPlugins() {
        return Arrays.asList(plugins);
    }

}
