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

package net.bdavies.babblebot.api.plugins;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Component
public interface IPluginContainer
{

    /**
     * This method will allow you to add a plugin to the container.
     *
     * @param plugin - The plugin itself.
     * @param model  - The plugin model data
     */
    void addPlugin(Object plugin, IPluginModel model);

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

    PluginPermissionContainer getPluginPermissions(Object pluginObj);

    PluginPermissionContainer getPluginPermissions(String name);

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
