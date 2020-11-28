/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
