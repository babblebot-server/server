package uk.co.bjdavies.plugins;

import uk.co.bjdavies.api.plugins.IPlugin;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface PluginCommandDefinition {
    String getName();

    Object[] getArgs();

    Class<?>[] getParameterTypes();

    Class<? extends IPlugin> getPluginClass();
}
