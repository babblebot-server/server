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

package net.babblebot.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.ICommand;
import net.babblebot.api.command.ICommandContext;
import net.babblebot.api.command.ICommandMiddleware;
import net.babblebot.api.command.ICommandRegistry;
import net.babblebot.api.config.EPluginPermission;
import net.babblebot.api.plugins.IPlugin;
import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.api.plugins.IPluginSettings;
import net.babblebot.api.plugins.Plugin;
import net.babblebot.discord.DiscordFacade;
import net.babblebot.discord.obj.factories.DiscordObjectFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Component
public class CommandRegistry implements ICommandRegistry
{
    public static final String NAMESPACE_STR = "Namespace: ";
    /**
     * This is the list of command that can be executed by the dispatcher.
     * Key: Namespace - e.g. "" which won't require a command prefix
     * e.g. "bb" which would require a bb prefix;
     */
    private final Map<String, List<ICommand>> commands;

    @Getter
    private final Map<IPluginSettings, List<ICommandMiddleware>> middlewareList;

    /**
     * This will initialize the commands list to an ArrayList.
     */
    public CommandRegistry()
    {
        this.commands = new HashMap<>();
        this.middlewareList = new HashMap<>();
        this.middlewareList.put(null, new ArrayList<>());
    }


    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param command - The command you wish to add.
     */
    public void addCommand(String namespace, ICommand command, IApplication application)
    {
        if (!commands.containsKey(namespace))
        {
            log.info(NAMESPACE_STR + namespace + " has not been created yet, creating now...");
            commands.put(namespace, new ArrayList<>());
        }
        List<ICommand> namespaceCommands = commands.get(namespace);
        if (commandExists(namespaceCommands, command))
        {
            log.warn("Command: " + command.getAliases()[0] + ", already exists inside namespace: " +
                    namespace);
        } else
        {
            namespaceCommands.add(command);
            registerDiscordSlashCommands(namespace, List.of(command), application);
        }
    }

    @Override
    public void addNamespace(String namespace, List<ICommand> commandsToAdd, IApplication application)
    {
        if (commands.containsKey(namespace))
        {
            log.info(NAMESPACE_STR + namespace + " has already been created.");
            log.warn("Plugin with this namespace already exists please consider changing it.");
            commands.get(namespace).addAll(commandsToAdd);
        } else
        {
            commands.put(namespace, commandsToAdd);
        }
        registerDiscordSlashCommands(namespace, commandsToAdd, application);
    }

    public void registerDiscordSlashCommands(String namespace, List<ICommand> commands,
                                             IApplication application)
    {
        DiscordFacade facade = application.get(DiscordFacade.class);
        DiscordObjectFactory factory = application.get(DiscordObjectFactory.class);
        long applicationId = facade.getClient().getRestClient().getApplicationId().blockOptional()
                .orElseThrow();

        var service = facade.getClient().getRestClient().getApplicationService();

        facade.getClient().getGuilds().subscribe(guild -> commands.forEach(command ->
                service.createGuildApplicationCommand(applicationId, guild.getId().asLong(),
                        factory.createSlashCommand(namespace, command)).subscribe()));
    }

    public void removeDiscordSlashCommands(String namespace, List<ICommand> commands,
                                           IApplication application)
    {
        DiscordFacade facade = application.get(DiscordFacade.class);
        long applicationId = facade.getClient().getRestClient().getApplicationId().blockOptional()
                .orElseThrow();

        var service = facade.getClient().getRestClient().getApplicationService();

        facade.getClient().getGuilds().subscribe(guild ->
                service.getGuildApplicationCommands(applicationId, guild.getId().asLong())
                        .subscribe(applicationCommand -> commands.forEach(command -> {
                            if (applicationCommand.name()
                                    .equalsIgnoreCase(namespace + command.getAliases()[0]))
                            {
                                service.deleteGuildApplicationCommand(applicationId, guild.getId().asLong(),
                                        applicationCommand.id().asLong()).subscribe();
                            }
                        })));
    }

    @Override
    public void removeNamespace(String namespace, IApplication application)
    {
        if (!commands.containsKey(namespace))
        {
            log.info(NAMESPACE_STR + namespace + " has not been created so you cannot remove it.");
        } else
        {
            List<ICommand> commandsRemoved = commands.get(namespace);
            removeDiscordSlashCommands(namespace, commandsRemoved, application);
            commands.remove(namespace);
        }
    }

    private boolean commandExists(List<ICommand> namespaceCommands, ICommand command)
    {
        boolean aliases = checkAliases(namespaceCommands, command);
        return !aliases && !namespaceCommands.contains(command);
    }

    private boolean checkAliases(List<ICommand> namespaceCommands, ICommand command)
    {
        val okay = new AtomicBoolean(true);
        List<String> commandAliases = Arrays.asList(command.getAliases());
        namespaceCommands.forEach(c -> {
            if (okay.get())
            {
                List<String> namespaceCommandAliases = Arrays.asList(c.getAliases());
                namespaceCommandAliases.forEach(a -> {
                    if (okay.get() && (commandAliases.contains(a)))
                    {
                        okay.set(false);
                    }
                });
            }
        });

        return okay.get();
    }

    /**
     * This is where you can remove the command from the dispatcher.
     *
     * @param command - The command you wish to remove.
     */
    public void removeCommand(String namespace, ICommand command, IApplication application)
    {
        if (!commands.containsKey(namespace))
        {
            log.error(NAMESPACE_STR + namespace + " has not been created yet, cannot remove command");
            return;
        }
        List<ICommand> namespaceCommands = commands.get(namespace);
        if (!namespaceCommands.contains(command))
        {
            log.error("Command is not part of the dispatcher so it cannot be removed.");
        } else
        {
            namespaceCommands.remove(command);
            removeDiscordSlashCommands(namespace, List.of(command), application);
        }

    }

    public List<ICommand> getCommandsFromNamespace(String namespace)
    {
        if (!commands.containsKey(namespace))
        {
            log.error(NAMESPACE_STR + namespace + " has not been created yet, cannot get namepsace");
            return new LinkedList<>();
        }
        return commands.get(namespace);
    }

    @Override
    public List<String> getRegisteredNamespaces()
    {
        return new ArrayList<>(commands.keySet());
    }

    /**
     * This will try and return a command based on a name and type of command.
     *
     * @param alias - The alias of command.
     * @param type  - The type of command.
     * @return Optional
     */
    public Optional<ICommand> getCommandByAlias(String namespace, String alias, String type)
    {
        return getCommandsFromNamespace(namespace)
                .stream()
                .filter(e -> Arrays.asList(e.getAliases()).contains(alias) && checkType(e.getType(), type))
                .findFirst();
    }

    public List<ICommand> getCommandsLike(String namespace, String commandName, String type)
    {
        return getCommandsFromNamespace(namespace)
                .stream()
                .filter(c -> checkType(c.getType(), type))
                .filter(c -> Arrays.stream(c.getAliases()).anyMatch(a -> a.contains(commandName)))
                .collect(Collectors.toList());
    }

    private String getNamespaceForCommand(ICommand c)
    {
        AtomicReference<String> namespace = new AtomicReference<>("");
        this.commands.forEach((key, value) -> {
            if (!checkAliases(value, c) && "".equals(namespace.get()))
            {
                namespace.set(key);
            }
        });
        return namespace.get();
    }

    public String getNamespaceFromCommandName(String commandName)
    {
        AtomicReference<String> namespace = new AtomicReference<>("");
        log.info(commandName);
        this.commands.keySet().forEach(i -> {
            if (commandName.startsWith(i))
            {
                namespace.set(i);
            }
        });

        return namespace.get();
    }

    @Override
    public void registerGlobalMiddleware(ICommandMiddleware middleware, Object registrar,
                                         IApplication application)
    {
        if (registrar.getClass().isAnnotationPresent(Plugin.class) || registrar instanceof IPlugin)
        {
            IPluginContainer container = application.getPluginContainer();
            if (container.getPluginPermissions(registrar)
                    .hasPermission(EPluginPermission.REGISTER_GLOBAL_MIDDLEWARE))
            {
                log.info("Plugin {} has registered global middleware", registrar.getClass().getSimpleName());
                this.middlewareList.get(null).add(middleware);
            } else
            {
                log.warn("Plugin {} has attempted to register global middleware when it doesn't have " +
                        "permission to", registrar.getClass().getSimpleName());
            }
        } else
        {
            this.middlewareList.get(null).add(middleware);
        }
    }


    @Override
    public void registerPluginMiddleware(IPluginSettings plugin, ICommandMiddleware middleware)
    {
        middlewareList.putIfAbsent(plugin, new ArrayList<>());
        middlewareList.get(plugin).add(middleware);
    }

    public Flux<ICommandMiddleware> getMiddlewareForNamespace(String namespace)
    {

        return Flux.create(sink -> {
            middlewareList.forEach((key, value) -> {
                if (key != null && (key.getNamespace().equals(namespace)))
                {
                    value.forEach(sink::next);
                }
            });
            sink.complete();
        });
    }


    /**
     * This will check of the command against the command context.
     *
     * @param commandType - The type of the command.
     * @param contextType - The command context.
     * @return boolean
     */
    private boolean checkType(String commandType, String contextType)
    {
        if ("All".equalsIgnoreCase(commandType))
        {
            return true;
        } else if (commandType.equalsIgnoreCase(contextType))
        {
            return true;
        } else if (commandType.contains("|"))
        {
            String[] types = commandType.split("\\|");
            var foundType = false;

            for (String type : types)
            {
                if (type.equalsIgnoreCase(contextType))
                {
                    foundType = true;
                    break;
                }
            }

            return foundType;
        }


        return false;
    }

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    public List<ICommand> getCommands(String namespace, String type)
    {
        return getCommandsFromNamespace(namespace)
                .stream()
                .filter(c -> checkType(c.getType(), type))
                .collect(Collectors.toList());
    }

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    public List<ICommand> getCommands(String type)
    {
        return commands.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Find a command in the command registry
     *
     * @param ctx         the command execution context {@link ICommandContext}
     * @param namespace   the command namespace
     * @param commandName the alias of the command
     * @return {@link Optional} of a {@link ICommand} if a command is found
 * @since 3.0.0-rc.26
     */
    public Optional<ICommand> findCommand(ICommandContext ctx, String namespace, String commandName)
    {
        return getCommandByAlias(namespace, commandName, ctx.getType());
    }
}