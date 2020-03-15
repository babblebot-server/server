package uk.co.bjdavies.api;

import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IConfig;
import uk.co.bjdavies.api.plugins.IPluginContainer;
import uk.co.bjdavies.api.variables.IVariableContainer;

/**
 * This is a interface for an application
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IApplication {

    /**
     * This will return of a instance of a class and inject any dependencies that are inside the dependency injector.
     *
     * @param clazz - this is the class to create
     * @return {@link Object<T>} this is an object of type T
     */
    <T> T get(Class<T> clazz);

    /**
     * This will return an instance of the application config from config.json file in your root directory.
     *
     * @return {@link IConfig}
     */
    IConfig getConfig();

    /**
     * This will return an instance of the application command dispatcher.
     *
     * @return {@link ICommandDispatcher}
     */
    ICommandDispatcher getCommandDispatcher();

    /**
     * This will return an instance of the application variable container
     *
     * @return {@link IVariableContainer}
     */
    IVariableContainer getVariableContainer();

    /**
     * This will return an instance of the application plugin container
     *
     * @return {@link IVariableContainer}
     */
    IPluginContainer getPluginContainer();

    /**
     * This will return the version of the server.
     *
     * @return String
     */
    String getServerVersion();
}
