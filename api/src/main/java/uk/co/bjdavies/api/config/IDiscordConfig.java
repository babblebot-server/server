package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IDiscordConfig {
    /**
     * This will return the token.
     *
     * @return String
     */
    String getToken();

    /**
     * This will return the command prefix.
     *
     * @return String
     */
    String getCommandPrefix();

    /**
     * This is the default playing text when the bot starts
     * NOTE: this can be changed by any module consult the module author if you wish then to add config to stop this.
     *
     * @return String
     */
    String getPlayingText();

    /**
     * This is the password required for the bot to be shutdown.
     *
     * @return String
     */
    String getShutdownPassword();
}
