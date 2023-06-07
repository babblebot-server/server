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

package net.bdavies.babblebot.command;

import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.command.ICommand;
import net.bdavies.babblebot.api.command.ICommandContext;
import net.bdavies.babblebot.api.command.ICommandDispatcher;
import net.bdavies.babblebot.api.command.ICommandMiddleware;
import net.bdavies.babblebot.api.config.EPluginPermission;
import net.bdavies.babblebot.api.plugins.IPlugin;
import net.bdavies.babblebot.api.plugins.IPluginContainer;
import net.bdavies.babblebot.api.plugins.IPluginSettings;
import net.bdavies.babblebot.api.plugins.Plugin;
import net.bdavies.babblebot.command.errors.UsageException;
import net.bdavies.babblebot.command.parser.MessageParser;
import net.bdavies.babblebot.command.renderer.CommandRenderer;
import net.bdavies.babblebot.command.response.BaseResponse;
import net.bdavies.babblebot.discord.DiscordFacade;
import net.bdavies.babblebot.variables.VariableParser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Component
public class CommandDispatcher implements ICommandDispatcher
{
    /**
     * This is the list of command that can be executed by the dispatcher.
     * Key: Namespace - e.g. "" which wont require a command prefix
     * e.g. "bb" which would require a bb prefix;
     */
    private final Map<String, List<ICommand>> commands;

    private final Map<IPluginSettings, List<ICommandMiddleware>> middlewareList;

    /**
     * This will initialize the commands list to an ArrayList.
     */
    public CommandDispatcher()
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
            log.info("Namespace: " + namespace + " has not been created yet, creating now...");
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
            //Todo: Move to renderer
            DiscordFacade facade = application.get(DiscordFacade.class);
            long applicationId = facade.getClient().getRestClient().getApplicationId().block();
            facade.getClient().getGuilds().subscribe(g -> {
                facade.getClient().getRestClient().getApplicationService()
                        .createGuildApplicationCommand(applicationId, g.getId().asLong(),
                                ApplicationCommandRequest.builder().name(command.getAliases()[0])
                                        .description(command.getDescription()).build()).subscribe();
            });
        }
    }

    @Override
    public void addNamespace(String namespace, List<ICommand> commandsToAdd, IApplication application)
    {
        if (commands.containsKey(namespace))
        {
            log.info("Namespace: " + namespace + " has already been created.");
            log.warn("Plugin with this namespace already exists please consider changing it.");
            commands.get(namespace).addAll(commandsToAdd);
        } else
        {
            commands.put(namespace, commandsToAdd);
        }

        //TODO: Make this better
        DiscordFacade facade = application.get(DiscordFacade.class);
        long applicationId = facade.getClient().getRestClient().getApplicationId().block();
        facade.getClient().getGuilds().subscribe(g -> {
            commandsToAdd.forEach(c -> facade.getClient().getRestClient().getApplicationService()
                    .createGuildApplicationCommand(applicationId, g.getId().asLong(),
                            ApplicationCommandRequest.builder()
                                    .name(namespace + c.getAliases()[0].toLowerCase())
                                    .description(c.getDescription().equals("") ? c.getAliases()[0]
                                            : c.getDescription()).build()).subscribe());
        });
    }

    @Override
    public void removeNamespace(String namespace)
    {
        if (!commands.containsKey(namespace))
        {
            log.info("Namespace: " + namespace + " has not been created so you cannot remove it.");
        } else
        {
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
        AtomicBoolean okay = new AtomicBoolean(true);
        List<String> commandAliases = Arrays.asList(command.getAliases());
        namespaceCommands.forEach(c -> {
            if (okay.get())
            {
                List<String> namespaceCommandAliases = Arrays.asList(c.getAliases());
                namespaceCommandAliases.forEach(a -> {
                    if (okay.get())
                    {
                        if (commandAliases.contains(a))
                        {
                            okay.set(false);
                        }
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
    public void removeCommand(String namespace, ICommand command)
    {
        if (!commands.containsKey(namespace))
        {
            log.error("Namespace: " + namespace + " has not been created yet, cannot remove command");
            return;
        }
        List<ICommand> namespaceCommands = commands.get(namespace);
        if (!namespaceCommands.contains(command))
        {
            log.error("Command is not part of the dispatcher so it cannot be removed.");
        } else
        {
            namespaceCommands.remove(command);
        }

    }

    public Flux<ICommand> getCommandsFromNamespace(String namespace)
    {
        if (!commands.containsKey(namespace))
        {
            log.error("Namespace: " + namespace + " has not been created yet, cannot get namepsace");
            return Flux.empty();
        }
        return Flux.create(sink -> {
            commands.get(namespace).forEach(sink::next);
            sink.complete();
        });
    }

    @Override
    public Flux<String> getRegisteredNamespaces()
    {
        return Flux.create(sink -> {
            commands.keySet().forEach(sink::next);
            sink.complete();
        });
    }

    /**
     * This will try and return a command based on a name and type of command.
     *
     * @param alias - The alias of command.
     * @param type  - The type of command.
     * @return Optional
     */
    public Mono<ICommand> getCommandByAlias(String namespace, String alias, String type)
    {
        return Mono.from(getCommandsFromNamespace(namespace).filter(
                e -> Arrays.asList(e.getAliases()).contains(alias) && e.getType().equals(type)).take(1));
    }


    /**
     * This will execute the command that user has entered is valid.
     *
     * @param parser      - The way the message is interpreted.
     * @param message     - This is the user's input or any other type of input.
     * @param application - The application instance
     * @param renderer    - The command renderer that will process the command responses
     */
    public void execute(MessageParser parser, String message, IApplication application,
                        CommandRenderer renderer)
    {
        ICommandContext commandContext = parser.parseString(message);

        if (commandContext != null)
        {
            log.info("Handling command: " + commandContext.getCommandName());
            AtomicBoolean canRun = new AtomicBoolean(true);

            this.middlewareList.get(null).forEach(m -> {
                if (canRun.get())
                {
                    canRun.set(m.onExecute(commandContext));
                }
            });

            if (!canRun.get())
            {
                log.info("Cannot run command due to failing middleware");
                return;
            }


            String namespace = getNamespaceFromCommandName(commandContext.getCommandName());

            this.getMiddlewareForNamespace(namespace).doOnNext(middleware -> {
                log.info("Running middleware for: " + namespace);
                if (canRun.get())
                {
                    canRun.set(middleware.onExecute(commandContext));
                }
            }).subscribe();

            if (!canRun.get())
            {
                log.info("Cannot run command due to failing plugin middleware");
                return;
            }

            String commandName = commandContext.getCommandName().replace(namespace, "");
            AtomicBoolean hasSentMessage = new AtomicBoolean(false);
            AtomicBoolean hasFoundOne = new AtomicBoolean(false);

            getCommandsFromNamespace(namespace).filter(e -> checkType(e.getType(), commandContext.getType()))
                    .filter(e -> Arrays.stream(e.getAliases())
                            .anyMatch(alias -> alias.equalsIgnoreCase(commandName)))
                    .doOnError(e -> log.error("Error in the command dispatcher.", e)).doOnComplete(() -> {
                        if (!hasFoundOne.get())
                        {
                            StringBuilder sb = new StringBuilder(
                                    "```markdown\n# Command Not Found\n\nDid You mean?\n");
                            getCommandsLike(commandName, commandContext.getType()).subscribe(c -> {
                                sb.append(getNamespaceForCommand(c)).append(c.getAliases()[0]).append("? - ")
                                        .append(c.getDescription()).append("\n");
                                hasSentMessage.set(true);
                            }, null, () -> {
                                if (!hasSentMessage.get())
                                {
                                    renderer.render(ResponseFactory.createStringResponse(
                                            "Babblebot command couldn't be found."));
                                } else
                                {
                                    sb.append("```");
                                    renderer.render(ResponseFactory.createStringResponse(sb.toString()));
                                }
                            });
                        }
                    }).flatMap(c -> {
                        hasFoundOne.set(true);
                        if (!c.validateUsage(commandContext))
                        {
                            return Flux.error(new UsageException(c.getUsage()));
                        }

                        c.exec(application, commandContext);
                        return commandContext.getCommandResponse().getResponses()
                                .asFlux()
                                .log(Loggers.getLogger("CommandResponses"));
                    })
                    .cast(BaseResponse.class)
                    .map(br -> {
                        if (br.isStringResponse())
                        {
                            return br.toBuilder()
                                    .stringResponse(new VariableParser(br.getStringResponse(),
                                            application).toString())
                                    .build();
                        }
                        return br;
                    })
                    .subscribe(r -> {
                        renderer.render(r);
                        hasSentMessage.set(true);
                    }, e -> {
                        boolean handled = false;
                        if (e instanceof Exception)
                        {
                            handled = renderer.onError((Exception) e);
                        }

                        if (!handled)
                        {
                            log.error("Unable to render command {}", commandName, e);
                        }
                    });

        } else
        {
            log.error("Command could not be parsed: " + message);
        }

    }

    private Flux<ICommand> getCommandsLike(String commandName, String type)
    {
        return Flux.create(sink -> getCommands(type).doOnComplete(sink::complete)
                .filter(c -> Arrays.stream(c.getAliases())
                        .anyMatch(a -> a.contains(commandName)))
                .doOnNext(sink::next).subscribe());
    }

    private String getNamespaceForCommand(ICommand c)
    {
        AtomicReference<String> namespace = new AtomicReference<>("");
        this.commands.forEach((key, value) -> {
            if (!checkAliases(value, c) && namespace.get().equals(""))
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
            if (container.doesPluginHavePermission(registrar, EPluginPermission.REGISTER_GLOBAL_MIDDLEWARE))
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
        if (!middlewareList.containsKey(plugin))
        {
            middlewareList.put(plugin, new ArrayList<>());
        }

        middlewareList.get(plugin).add(middleware);
    }

    private Flux<ICommandMiddleware> getMiddlewareForNamespace(String namespace)
    {

        return Flux.create(sink -> {
            middlewareList.forEach((key, value) -> {
                if (key != null)
                {
                    if (key.getNamespace().equals(namespace))
                    {
                        value.forEach(sink::next);
                    }
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
        if (commandType.equals("All"))
        {
            return true;
        } else if (commandType.toLowerCase().equals(contextType.toLowerCase()))
        {
            return true;
        } else if (commandType.contains("|"))
        {
            String[] types = commandType.split("|");
            boolean foundType = false;

            for (String type : types)
            {
                if (type.toLowerCase().equals(contextType.toLowerCase()))
                {
                    foundType = true;
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
    public Flux<ICommand> getCommands(String namespace, String type)
    {
        return getCommandsFromNamespace(namespace).filter(c -> c.getType().equals(type));
    }

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    public Flux<ICommand> getCommands(String type)
    {

        AtomicReference<Flux<ICommand>> commandFlux = new AtomicReference<>(Flux.empty());

        commands.keySet().forEach(k -> {
            commandFlux.set(commandFlux.get().concatWith(getCommands(k, type)));
        });

        return commandFlux.get();
    }
}