package uk.co.bjdavies.api.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface ICustomPluginConfig {

    /**
     * This will create a file and populate based on the fields specified in the config class
     * If this is off the user must create a config file in your plugin folder.
     *
     * @return boolean true if you want to create a file automatically
     */
    boolean autoGenerateConfig();

    /**
     * File name of your config
     * e.g. musicbot
     * Note: .config.json is automatically appended.
     *
     * @return
     */
    String fileName();

}
