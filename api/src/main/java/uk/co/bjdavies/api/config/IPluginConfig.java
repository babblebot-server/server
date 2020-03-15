package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IPluginConfig {
    /**
     * This will return the plugin's location.
     *
     * @return String
     */
    String getPluginLocation();

    /**
     * This will return the plugin's class path.
     *
     * @return String
     */
    String getPluginClassPath();

    /**
     * This will return the plugin type either "js" or "java"
     *
     * @return String
     */
    String getPluginType();
}
