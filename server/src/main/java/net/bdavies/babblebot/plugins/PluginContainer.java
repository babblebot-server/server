/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdavies.babblebot.plugins;

import de.skuzzle.semantic.Version;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.config.EPluginPermission;
import net.bdavies.babblebot.api.plugins.*;
import net.bdavies.babblebot.command.CommandDispatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Component
public class PluginContainer implements IPluginContainer
{
    private final Map<String, Object> plugins;
    private final Map<String, String> pluginNamespaces;
    private final ApplicationContext applicationContext;
    private final Map<String, IPluginSettings> settings;
    private final Map<Object, PluginPermissionContainer> pluginPermissionsMap;

    public PluginContainer(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        plugins = new HashMap<>();
        settings = new HashMap<>();
        pluginNamespaces = new HashMap<>();
        pluginPermissionsMap = new HashMap<>();
    }

    @Override
    public void addPlugin(Object obj, IPluginModel model)
    {
        IApplication application = applicationContext.getBean(IApplication.class);
        if (plugins.containsKey(model.getName()) || plugins.containsValue(obj))
        {
            log.error("The key or plugin is already in the container.");
        } else
        {
            String pName, author, minServerVersion, maxServerVersion;
            pluginPermissionsMap.put(obj, model.getPluginPermissions());
            String namespace = calculateNamespace(model);
            pluginNamespaces.put(model.getName(), namespace);

            if (obj instanceof IPlugin)
            {
                IPlugin plugin = (IPlugin) obj;
                pName = plugin.getName();
                author = plugin.getAuthor();
                minServerVersion = plugin.getMinimumServerVersion();
                maxServerVersion = plugin.getMaximumServerVersion();
            } else if (obj.getClass().isAnnotationPresent(Plugin.class))
            {
                Plugin plugin = obj.getClass().getAnnotation(Plugin.class);

                pName = plugin.value().equals("")
                        ? obj.getClass().getSimpleName().toLowerCase().replace("plugin", "")
                        : plugin.value();

                author = plugin.author();
                minServerVersion = plugin.minServerVersion().equals("0")
                        ? application.getServerVersion()
                        : plugin.minServerVersion();
                maxServerVersion = plugin.maxServerVersion();
            } else
            {
                log.error(
                        "Cannot add Plugin because it does have the Plugin annotation or implement IPlugin");
                return;
            }

            IPluginSettings settings = getPluginSettings(pName, author, namespace, minServerVersion,
                    maxServerVersion);
            if (settings != null)
            {
                PluginConfigParser.parsePlugin(application, settings, obj);
                if (obj instanceof IPluginEvents)
                {
                    try
                    {
                        ((IPluginEvents) obj).onBoot(settings);
                    }
                    catch (AbstractMethodError e)
                    {
                        log.warn(
                                "Old Plugin version, please consider upgrading. onBoot will not run. " +
                                        "Plugin: " +
                                        model.getName());
                    }
                }
                PluginCommandParser commandParser = new PluginCommandParser(application, settings, obj);
                application.getCommandDispatcher().addNamespace(settings.getNamespace(),
                        commandParser.parseCommands(), application);
                log.info("Added plugin: " + settings.getName() + " (by {}), using namespace: \"" +
                        settings.getNamespace() + "\"", settings.getAuthor());
                this.settings.put(model.getName(), settings);
                plugins.put(model.getName(), obj);
            }
        }
    }

    private String calculateNamespace(IPluginModel model)
    {
        if (model.getNamespace().equals(""))
        {
            if (model.getPluginPermissions().hasPermission(EPluginPermission.EMPTY_NAMESPACE))
            {
                return model.getNamespace();
            } else
            {
                log.warn(
                        "Plugin {} tried to use an empty namespace and doesn't have the EMPTY_NAMESPACE " +
                                "permission",
                        model.getName());
                return model.getName().toLowerCase() + "-";
            }
        }
        return model.getNamespace() + "-";
    }

    private IPluginSettings getPluginSettings(String name,
                                              String author,
                                              String namespace,
                                              String minimumServerVersion,
                                              String maximumServerVersion)
    {
        IApplication application = applicationContext.getBean(IApplication.class);
        Version serverVersion = Version.parseVersion(application.getServerVersion());
        Version minVersion = Version.parseVersion(minimumServerVersion);

        if (serverVersion.isGreaterThanOrEqualTo(minVersion))
        {
            if (maximumServerVersion.equals("0"))
            {
                return new PluginSettings(name, author, namespace,
                        minimumServerVersion, maximumServerVersion);
            } else
            {
                Version maxVersion = Version.parseVersion(maximumServerVersion);
                if (serverVersion.isLowerThanOrEqualTo(maxVersion))
                {
                    return new PluginSettings(name, author, namespace,
                            minimumServerVersion, maximumServerVersion);
                } else
                {
                    log.error(
                            "Plugin not supported for this server version please downgrade server version " +
                                    "is too high, please refer to the documentation" +
                                    " for further help.");
                    return null;
                }
            }
        } else
        {
            log.error(
                    "Plugin not supported for this server version please update, please refer to the " +
                            "documentation" +
                            " for further help.");
            return null;
        }

    }

    @Override
    public void removePlugin(String name)
    {
        if (!plugins.containsKey(name))
        {
            log.error("The module name entered does not exist inside this container.");
        } else
        {
            Object o = plugins.get(name);
            if (o instanceof IPluginEvents)
            {
                ((IPluginEvents) o).onShutdown();
            }
            CommandDispatcher commandDispatcher = applicationContext.getBean(CommandDispatcher.class);
            commandDispatcher.removeNamespace(pluginNamespaces.get(name));
            pluginPermissionsMap.remove(o);
            settings.remove(name);
            plugins.remove(name);
            pluginNamespaces.remove(name);
            log.info("Removed plugin {} from the system", name);
        }
    }

    @Override
    public boolean doesPluginExist(String name)
    {
        return plugins.keySet().stream().anyMatch(e -> e.equalsIgnoreCase(name));
    }

    @Override
    public Object getPlugin(String name)
    {
        return plugins.get(name);
    }

    @Override
    public PluginPermissionContainer getPluginPermissions(Object pluginObj)
    {
        return pluginPermissionsMap.get(pluginObj);
    }

    @Override
    public PluginPermissionContainer getPluginPermissions(String name)
    {
        return getPluginPermissions(plugins.get(name));
    }


    @Override
    public void shutDownPlugins()
    {
        this.plugins.forEach((k, v) -> {
            if (v instanceof IPluginEvents)
            {
                ((IPluginEvents) v).onShutdown();
            }
        });
    }

    @Override
    public String toString()
    {
        return "PluginContainer{" +
                "plugins=" + plugins +
                '}';
    }

    @Override
    public Optional<IPluginSettings> getPluginSettingsFromNamespace(String namespace)
    {
        return settings.values()
                .stream()
                .filter(pluginSettings -> pluginSettings.getNamespace()
                        .equals(namespace))
                .findFirst();
    }
}
