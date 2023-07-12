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

package net.babblebot.api.command;

import net.babblebot.api.IApplication;
import net.babblebot.api.plugins.IPluginSettings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Component
public interface ICommandRegistry
{

    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param command - The command you wish to add.
     */
    void addCommand(String namespace, ICommand command, IApplication application);

    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param commands - The commands you wish to add.
     */
    void addNamespace(String namespace, List<ICommand> commands, IApplication application);


    /**
     * This is where you can a command to the command dispatcher.
     */
    void removeNamespace(String namespace, IApplication application);


    /**
     * This is where you can remove the command from the dispatcher.
     *
     * @param command - The command you wish to remove.
     */
    void removeCommand(String namespace, ICommand command, IApplication application);

    /**
     * This will try and return a command based on a name and type of command.
     *
     * @param alias - The alias of command.
     * @param type  - The type of command.
     * @return Optional
     */
    Optional<ICommand> getCommandByAlias(String namespace, String alias, String type);

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    List<ICommand> getCommands(String namespace, String type);

    /**
     * This will return the list of commands in the dispatcher from all namespaces.
     *
     * @param type - THe type of command.
     * @return List
     */
    List<ICommand> getCommands(String type);

    /**
     * Get all the commands for a certain namespace
     *
     * @param namespace the namespace to lookup
     * @return {@link Flux} of {@link ICommand}s
     */
    List<ICommand> getCommandsFromNamespace(String namespace);

    /**
     * Get all the namespaces that are registered inside the command dispatcher
     *
     * @return {@link Flux} of {@link String}s
     */
    List<String> getRegisteredNamespaces();

    /**
     * Get the namespace from a command
     *
     * @param commandName the whole command name
     * @return {@link String}
     */
    String getNamespaceFromCommandName(String commandName);

    /**
     * Register Global middleware that will run before the execution of the command implementation for all
     * commands regardless of the namespace it can stop execution of the command at this point
     *
     * @param middleware  {@link ICommandMiddleware} the middleware object that will be run
     * @param registrar   the object that is registering the middleware
     * @param application {@link IApplication} instance of the application
     */
    void registerGlobalMiddleware(ICommandMiddleware middleware, Object registrar, IApplication application);

    /**
     * Register Plugin middleware that will run before the execution of the command implementation for all
     * commands of the namespace of the plugin it can stop execution of the command at this point
     *
     * @param plugin     the plugin object
     * @param middleware {@link ICommandMiddleware} the middleware object that will be run
     */
    void registerPluginMiddleware(IPluginSettings plugin, ICommandMiddleware middleware);
}
