package uk.co.bjdavies.plugins;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.plugins.IPlugin;
import uk.co.bjdavies.api.plugins.IPluginContainer;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class PluginContainer implements IPluginContainer {

    private final IApplication application;

    private final Map<String, IPlugin> plugins;

    public PluginContainer(IApplication application) {
        this.application = application;
        plugins = new Hashtable<>();
    }

    @Override
    public void addPlugin(String name, IPlugin plugin) {
        if (plugins.containsKey(name) || plugins.containsValue(plugin)) {
            log.error("The key or plugin is already in the container.");
        } else {
            //TODO: Use SemVer library
            String serverVersion = application.getServerVersion();
            int major = Integer.parseInt(serverVersion.split("\\.")[0]);
            int minor = Integer.parseInt(serverVersion.split("\\.")[1]);
            int patch = Integer.parseInt(serverVersion.split("\\.")[2]);

            String minServerVersion = plugin.getMinimumServerVersion();
            int minMajor = Integer.parseInt(minServerVersion.split("\\.")[0]);
            int minMinor = Integer.parseInt(minServerVersion.split("\\.")[1]);
            int minPatch = Integer.parseInt(minServerVersion.split("\\.")[2]);


            if (major >= minMajor && minor >= minMinor && patch >= minPatch) {
                plugin.onBoot();
                PluginCommandParser commandParser = new PluginCommandParser(plugin);
                application.getCommandDispatcher().addNamespace(plugin.getNamespace(), commandParser.parseCommands());
                plugins.put(name, plugin);
            } else {
                log.error("Plugin not supported for this server version please update, please refer to the documentation" +
                        " for further help.");
            }
        }
    }

    @Override
    public void removePlugin(String name) {
        if (!plugins.containsKey(name)) {
            log.error("The module name entered does not exist inside this container.");
        } else {
            plugins.get(name).onShutdown();
            plugins.remove(name);
        }
    }

    @Override
    public IPlugin getPlugin(String name) {
        if (!plugins.containsKey(name)) {
            log.error("The module name entered does not exist inside this container.");
        } else {
            return plugins.get(name);
        }
        return null;
    }

    @Override
    public boolean doesPluginExist(String name) {
        return plugins.keySet().stream().anyMatch(e -> e.toLowerCase().equals(name.toLowerCase()));
    }

    @Override
    public String toString() {
        return "PluginContainer{" +
                "plugins=" + plugins +
                '}';
    }
}
