package uk.co.bjdavies.api.command;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.plugins.IPlugin;

import java.util.List;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ICommandDispatcher {

    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param command - The command you wish to add.
     */
    void addCommand(String namespace, ICommand command);

    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param commands - The commands you wish to add.
     */
    void addNamespace(String namespace, List<ICommand> commands);


    /**
     * This is where you can a command to the command dispatcher.
     */
    void removeNamespace(String namespace);


    /**
     * This is where you can remove the command from the dispatcher.
     *
     * @param command - The command you wish to remove.
     */
    void removeCommand(String namespace, ICommand command);

    /**
     * This will try and return a command based on a name and type of command.
     *
     * @param alias - The alias of command.
     * @param type  - The type of command.
     * @return Optional
     */
    Mono<ICommand> getCommandByAlias(String namespace, String alias, String type);

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    Flux<ICommand> getCommands(String namespace, String type);

    /**
     * This will return the list of commands in the dispatcher from all namespaces.
     *
     * @param type - THe type of command.
     * @return List
     */
    Flux<ICommand> getCommands(String type);

    Flux<ICommand> getCommandsFromNamespace(String namespace);

    String getNamespaceFromCommandName(String commandName);

    void registerGlobalMiddleware(ICommandMiddleware middleware);

    void registerPluginMiddleware(IPlugin plugin, ICommandMiddleware middleware);
}
