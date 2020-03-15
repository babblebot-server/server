package uk.co.bjdavies.api.plugins;

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
    void addPlugin(String name, IPlugin plugin);


    /**
     * This method allows you to remove a plugin from the container.
     *
     * @param name - This is the module that you want to remove.
     */
    void removePlugin(String name);


    /**
     * This method allows you to get a plugin from the container.
     *
     * @param name - This is the plugin that you want to get from the container.
     * @return {@link IPlugin}
     */
    IPlugin getPlugin(String name);


    /**
     * This method checks whether the plugin specified exists in the container.
     *
     * @param name - The name of the plugin.
     * @return boolean
     */
    boolean doesPluginExist(String name);

    /**
     * This is the toString of the class will show the classes contents.
     *
     * @return String
     */
    @Override
    String toString();
}
