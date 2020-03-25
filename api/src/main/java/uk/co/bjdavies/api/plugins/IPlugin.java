package uk.co.bjdavies.api.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IPlugin extends IPluginEvents {
    /**
     * This is the name of the Plugin.
     *
     * @return String
     */
    String getName();

    /**
     * This is the version of the Plugin.
     *
     * @return String
     * @deprecated Not required for plugin processing
     */
    String getVersion();

    /**
     * This is the author of the Plugin.
     *
     * @return String
     */
    String getAuthor();

    /**
     * This is the minimum version of the server that the plugin can run on. Default: 1
     *
     * @return String
     */
    String getMinimumServerVersion();

    /**
     * This is the maximum version of the server that the plugin can run on. Default: 0 (no limit)
     *
     * @return String
     */
    String getMaximumServerVersion();

    /**
     * This is the namespace for your commands
     * Please don't use ""
     * <p>
     * Because if it clashes with any commands your commands will not be added to system.
     *
     * @return String
     */
    String getNamespace();
}
