package uk.co.bjdavies.api.plugins;

import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IPluginContainer {

    /**
     * This method will allow you to add a plugin to the container.
     *
     * @param name   - The name of the plugin.
     * @param plugin - The plugin itself.
     */
    void addPlugin(String name, Object plugin);

    /**
     * This method will allow you to add a plugin to the container.
     *
     * @param plugin - The plugin itself.
     */
    void addPlugin(Object plugin);


    /**
     * This method allows you to remove a plugin from the container.
     *
     * @param name - This is the module that you want to remove.
     */
    void removePlugin(String name);


    /**
     * This method checks whether the plugin specified exists in the container.
     *
     * @param name - The name of the plugin.
     * @return boolean
     */
    boolean doesPluginExist(String name);

    /**
     * Return a plugin by its name
     *
     * @param name - The plugin name
     * @return Object
     */
    Object getPlugin(String name);

    /**
     * This will attempt to shut down all plugins if they implement {@link IPluginEvents}
     */
    void shutDownPlugins();

    /**
     * This is the toString of the class will show the classes contents.
     *
     * @return String
     */
    @Override
    String toString();

    /**
     * Get a plugin settings from a namespace
     *
     * @param namespace - the namespace
     * @return {@link IPluginSettings}
     */
    Optional<IPluginSettings> getPluginSettingsFromNamespace(String namespace);
}
