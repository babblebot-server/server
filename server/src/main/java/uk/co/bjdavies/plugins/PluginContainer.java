package uk.co.bjdavies.plugins;

import de.skuzzle.semantic.Version;
import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.plugins.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class PluginContainer implements IPluginContainer {

    private final IApplication application;

    private final Map<String, Object> plugins;
    private final Map<String, IPluginSettings> settings;

    public PluginContainer(IApplication application) {
        this.application = application;
        plugins = new HashMap<>();
        settings = new HashMap<>();
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
                    try {
                        ((IPluginEvents) obj).onBoot(settings);
                    } catch (AbstractMethodError e) {
                        log.warn("Old Plugin version, please consider upgrading. onBoot will not run. Plugin: " + name);
                    }
                }
                PluginCommandParser commandParser = new PluginCommandParser(application, settings, obj);
                application.getCommandDispatcher().addNamespace(settings.getNamespace(),
                        commandParser.parseCommands());
                log.info("Added plugin: " + settings.getName() + ", using namespace: \"" + settings.getNamespace() + "\"");
                this.settings.put(name, settings);
                plugins.put(name, obj);
            }
        }
    }

    @Override
    public void addPlugin(Object plugin) {
        addPlugin(plugin.getClass().getSimpleName().toLowerCase().replace("plugin", ""), plugin);
    }

    private IPluginSettings getPluginSettings(String name,
                                              String author,
                                              String namespace,
                                              String minimumServerVersion,
                                              String maximumServerVersion) {

        Version serverVersion = Version.parseVersion(application.getServerVersion());
        Version minVersion = Version.parseVersion(minimumServerVersion);

        if (serverVersion.isGreaterThanOrEqualTo(minVersion)) {
            if (maximumServerVersion.equals("0")) {
                return new PluginSettings(name, author, namespace,
                        minimumServerVersion, maximumServerVersion);
            } else {
                Version maxVersion = Version.parseVersion(maximumServerVersion);
                if (serverVersion.isLowerThanOrEqualTo(maxVersion)) {
                    return new PluginSettings(name, author, namespace,
                            minimumServerVersion, maximumServerVersion);
                } else {
                    log.error("Plugin not supported for this server version please downgrade server version is too high, please refer to the documentation" +
                            " for further help.");
                    return null;
                }
            }
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
    @Nullable
    public Object getPlugin(String name) {
        return plugins.get(name);
    }

    @Override
    public void shutDownPlugins() {
        this.plugins.forEach((k, v) -> {
            if (v instanceof IPluginEvents) {
                ((IPluginEvents) v).onShutdown();
            }
        });
    }

    @Override
    public String toString() {
        return "PluginContainer{" +
                "plugins=" + plugins +
                '}';
    }

    @Override
    public Optional<IPluginSettings> getPluginSettingsFromNamespace(String namespace) {
        return settings.values()
                .stream()
                .filter(pluginSettings -> pluginSettings.getNamespace()
                        .equals(namespace))
                .findFirst();
    }
}
