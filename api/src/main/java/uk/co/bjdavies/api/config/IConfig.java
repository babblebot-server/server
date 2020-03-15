package uk.co.bjdavies.api.config;

import java.util.Collection;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: Config.java
 * Compiled Class Name: Config.class
 * Date Created: 30/01/2018
 */

public interface IConfig {

    /**
     * This will return the config for the discord part of this bot.
     *
     * @return {@link IDiscordConfig}
     */
    IDiscordConfig getDiscordConfig();


    /**
     * This will return the config for the system part of this bot.
     *
     * @return {@link ISystemConfig}
     */
    ISystemConfig getSystemConfig();

    /**
     * This will return the config for the HTTP Server.
     *
     * @return {@link IHttpConfig}
     */
    IHttpConfig getHttpConfig();


    /**
     * This will return the config for the database part of the bot.
     *
     * @return {@link IDatabaseConfig}
     */
    IDatabaseConfig getDatabaseConfig();


    /**
     * This will return the config for the plugins used in this bot.
     *
     * @return {@link Collection<IPluginConfig>}
     */
    Collection<IPluginConfig> getPlugins();

}
