package uk.co.bjdavies.plugins;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.plugins.*;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class PluginContainer implements IPluginContainer {

    private final IApplication application;

    private final Map<String, Object> plugins;

    public PluginContainer(IApplication application) {
        this.application = application;
        plugins = new Hashtable<>();
    }

    @Override
    public void addPlugin(String name, Object obj) {
        if (plugins.containsKey(name) || plugins.containsValue(obj)) {
            log.error("The key or plugin is already in the container.");
        } else {

            String pName, author, namespace, minServerVersion, maxServerVersion;

            if (obj instanceof IPlugin) {
                IPlugin plugin = (IPlugin) obj;
                pName = plugin.getName();
                author = plugin.getAuthor();
                namespace = plugin.getNamespace();
                minServerVersion = plugin.getMinimumServerVersion();
                maxServerVersion = plugin.getMaximumServerVersion();
            } else if (obj.getClass().isAnnotationPresent(Plugin.class)) {
                Plugin plugin = obj.getClass().getAnnotation(Plugin.class);

                pName = plugin.value().equals("")
                        ? obj.getClass().getSimpleName().toLowerCase().replace("plugin", "")
                        : plugin.value();

                author = plugin.author();
                minServerVersion = plugin.minServerVersion().equals("0")
                        ? application.getServerVersion()
                        : plugin.minServerVersion();
                maxServerVersion = plugin.maxServerVersion();
                namespace = plugin.namespace().equals("<bb-def-uniq>")
                        ? pName + "-"
                        : plugin.namespace();

            } else {
                log.error("Cannot add Plugin because it does have the Plugin annotation or implement IPlugin");
                return;
            }

            IPluginSettings settings = getPluginSettings(pName, author, namespace, minServerVersion, maxServerVersion);
            if (settings != null) {
                PluginConfigParser.parsePlugin(application, settings, obj);
                if (obj instanceof IPluginEvents) {
                    ((IPluginEvents) obj).onBoot(settings);
                }
                PluginCommandParser commandParser = new PluginCommandParser(settings, obj);
                application.getCommandDispatcher().addNamespace(settings.getNamespace(),
                        commandParser.parseCommands());
                log.info("Added plugin: " + settings.getName() + ", using namespace: \"" + settings.getNamespace() + "\"");
                plugins.put(name, obj);
            }
        }
    }

    private IPluginSettings getPluginSettings(String name,
                                              String author,
                                              String namespace,
                                              String minimumServerVersion,
                                              String maximumServerVersion) {

        //TODO: Use SemVer library
        String serverVersion = application.getServerVersion();
        String[] serverTokens = serverVersion.split("\\.");

        int major = Integer.parseInt(serverTokens[0]);
        int minor = Integer.parseInt(serverTokens[1]);
        int patch = Integer.parseInt(serverTokens[2]);

        String[] minTokens = minimumServerVersion.split("\\.");

        int minMajor = Integer.parseInt(minTokens[0]);
        int minMinor = Integer.parseInt(minTokens[1]);
        int minPatch = Integer.parseInt(minTokens[2]);


        if (major >= minMajor && minor >= minMinor && patch >= minPatch) {
            return new PluginSettings(name, author, namespace,
                    minimumServerVersion, maximumServerVersion);
        } else {
            log.error("Plugin not supported for this server version please update, please refer to the documentation" +
                    " for further help.");
            return null;
        }

    }

    @Override
    public void removePlugin(String name) {
        if (!plugins.containsKey(name)) {
            log.error("The module name entered does not exist inside this container.");
        } else {
            Object o = plugins.get(name);
            if (o instanceof IPluginEvents) {
                ((IPluginEvents) o).onShutdown();
            }
            plugins.remove(name);
        }
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
