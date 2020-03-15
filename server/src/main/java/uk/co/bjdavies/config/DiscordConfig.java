package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.IDiscordConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: DiscordConfig.java
 * Compiled Class Name: DiscordConfig.class
 * Date Created: 31/01/2018
 */

public class DiscordConfig implements IDiscordConfig {
    /**
     * This is used to connect to the discord api with your selected bot.
     */
    private String token;


    /**
     * This is what will be used to determine if a discord message can be considered a command.
     */
    private String commandPrefix;

    /**
     * This will be used to set the playing text on startup.
     */
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private String playingText = "BabbleBot 2020. Ben Davies";


    /**
     * This will return the token.
     *
     * @return String
     */
    public String getToken() {
        return token;
    }

    /**
     * This will return the command prefix.
     *
     * @return String
     */
    public String getCommandPrefix() {
        return commandPrefix;
    }

    @Override
    public String getPlayingText() {
        return playingText;
    }
}
