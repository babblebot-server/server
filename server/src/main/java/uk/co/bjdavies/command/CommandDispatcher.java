package uk.co.bjdavies.command;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.ICommand;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.command.ICommandMiddleware;
import uk.co.bjdavies.command.parser.MessageParser;
import uk.co.bjdavies.variables.VariableParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class CommandDispatcher implements ICommandDispatcher {

    /**
     * This is the list of command that can be executed by the dispatcher.
     * Key: Namespace - e.g. "" which wont require a command prefix
     * e.g. "bb" which would require a bb prefix;
     */
    private final Map<String, List<ICommand>> commands;

    private final List<ICommandMiddleware> middlewareList;

    /**
     * This will initialize the commands list to an ArrayList.
     */
    public CommandDispatcher() {
        this.commands = new HashMap<>();
        this.middlewareList = new ArrayList<>();
    }


    /**
     * This is where you can a command to the command dispatcher.
     *
     * @param command - The command you wish to add.
     */
    public void addCommand(String namespace, ICommand command) {
        if (!commands.containsKey(namespace)) {
            log.info("Namespace: " + namespace + " has not been created yet, creating now...");
            commands.put(namespace, new ArrayList<>());
        }
        List<ICommand> namespaceCommands = commands.get(namespace);
        if (commandExists(namespaceCommands, command)) {
            log.warn("Command: " + command.getAliases()[0] + ", already exists inside namespace: " + namespace);
        } else {
            namespaceCommands.add(command);
        }
    }

    @Override
    public void addNamespace(String namespace, List<ICommand> commandsToAdd) {
        if (commands.containsKey(namespace)) {
            log.info("Namespace: " + namespace + " has already been created.");
        } else {
            commands.put(namespace, commandsToAdd);
        }
    }

    @Override
    public void removeNamespace(String namespace) {
        if (!commands.containsKey(namespace)) {
            log.info("Namespace: " + namespace + " has not been created so you cannot remove it.");
        } else {
            commands.remove(namespace);
        }
    }

    private boolean commandExists(List<ICommand> namespaceCommands, ICommand command) {
        boolean aliases = checkAliases(namespaceCommands, command);
        return !aliases && !namespaceCommands.contains(command);
    }

    private boolean checkAliases(List<ICommand> namespaceCommands, ICommand command) {
        AtomicBoolean okay = new AtomicBoolean(true);
        List<String> commandAliases = Arrays.asList(command.getAliases());
        namespaceCommands.forEach(c -> {
            if (okay.get()) {
                List<String> namespaceCommandAliases = Arrays.asList(c.getAliases());
                namespaceCommandAliases.forEach(a -> {
                    if (okay.get()) {
                        if (commandAliases.contains(a)) {
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
    public void removeCommand(String namespace, ICommand command) {
        if (!commands.containsKey(namespace)) {
            log.error("Namespace: " + namespace + " has not been created yet, cannot remove command");
            return;
        }
        List<ICommand> namespaceCommands = commands.get(namespace);
        if (!namespaceCommands.contains(command)) {
            log.error("Command is not part of the dispatcher so it cannot be removed.");
        } else {
            namespaceCommands.remove(command);
        }

    }

    public List<ICommand> getCommandsFromNamespace(String namespace) {
        if (!commands.containsKey(namespace)) {
            log.error("Namespace: " + namespace + " has not been created yet, cannot get namepsace");
            return new ArrayList<>();
        }

        return commands.get(namespace);
    }

    /**
     * This will try and return a command based on a name and type of command.
     *
     * @param alias - The alias of command.
     * @param type  - The type of command.
     * @return Optional
     */
    public Optional<ICommand> getCommandByAlias(String namespace, String alias, String type) {
        return getCommandsFromNamespace(namespace).stream()
                .filter(e -> Arrays.asList(e.getAliases()).contains(alias) && e.getType().equals(type))
                .findAny();
    }


    /**
     * This will execute the command that user has entered is valid.
     *
     * @param parser      - The way the message is interpreted.
     * @param message     - This is the user's input or any other type of input.
     * @param application - The application instance.
     * @return String - The command's response
     */
    public String execute(MessageParser parser, String message, IApplication application) {

        ICommandContext commandContext = parser.parseString(message);

        if (commandContext != null) {
            AtomicBoolean canRun = new AtomicBoolean(true);

            this.middlewareList.forEach(m -> {
                if (canRun.get()) {
                    canRun.set(m.onExecute(commandContext));
                }
            });

            if (!canRun.get()) {
                log.info("Cannot run command due to failing middleware");
                return "";
            }

            final ICommand[] command = new ICommand[1];

            String namespace = getNamespaceFromCommandName(commandContext.getCommandName());
            String commandName = commandContext.getCommandName().replace(namespace, "");

            getCommandsFromNamespace(namespace)
                    .stream()
                    .filter(e -> checkType(e.getType(), commandContext.getType())).forEach(e -> {
                Optional<String> findCommand = Arrays.stream(e.getAliases())
                        .filter(alias -> alias.toLowerCase().equals(commandName.toLowerCase()))
                        .findFirst();
                if (findCommand.isPresent()) {
                    command[0] = e;
                }
            });

            if (command[0] != null) {
                boolean isValid = command[0].validateUsage(commandContext);
                if (isValid) {
                    log.debug("Running Command:" + commandContext.getCommandName() + " In Guild: " +
                            Objects.requireNonNull(commandContext.getMessage().getGuild().block()).getName());
                    return new VariableParser(command[0].run(application, commandContext), application).toString();
                } else {
                    return command[0].getUsage();
                }
            } else {

                List<ICommand> commandsFilter = getCommandsLike(commandName,
                        commandContext.getType());

                if (commandsFilter.size() == 0) {
                    return "The command couldn't be found.";
                } else {

                    StringBuilder sb = new StringBuilder("```markdown\n# Command Not Found\n\nDid You mean?\n");

                    commandsFilter.forEach(c -> {
                        sb.append(getNamespaceForCommand(c)).append(c.getAliases()[0]).append("? - ").append(c.getDescription()).append("\n");
                    });

                    sb.append("```");

                    Objects.requireNonNull(commandContext.getMessage().getChannel().block())
                            .createMessage(sb.toString()).block();
                }

                return "";
            }

        } else {
            return "Command could not be parsed.";
        }

    }

    private List<ICommand> getCommandsLike(String commandName, String type) {

        List<ICommand> allCommands = getCommands(type);

        List<ICommand> resultingCommands = new ArrayList<>();

        allCommands.forEach(c -> Arrays.asList(c.getAliases()).forEach(a -> {
            if (a.contains(commandName)) {
                resultingCommands.add(c);
            }
        }));

        return resultingCommands;
    }

    private String getNamespaceForCommand(ICommand c) {
        AtomicReference<String> namespace = new AtomicReference<>("");
        this.commands.forEach((key, value) -> {
            if (!checkAliases(value, c) && namespace.get().equals("")) {
                namespace.set(key);
            }
        });
        return namespace.get();
    }

    public String getNamespaceFromCommandName(String commandName) {
        AtomicReference<String> namespace = new AtomicReference<>("");
        this.commands.keySet().forEach(i -> {
            if (commandName.startsWith(i)) {
                namespace.set(i);
            }
        });

        return namespace.get();
    }

    @Override
    public void registerGlobalMiddleware(ICommandMiddleware middleware) {
        this.middlewareList.add(middleware);
    }


    /**
     * This will check of the command against the command context.
     *
     * @param commandType - The type of the command.
     * @param contextType - The command context.
     * @return boolean
     */
    private boolean checkType(String commandType, String contextType) {
        if (commandType.equals("All")) {
            return true;
        } else if (commandType.toLowerCase().equals(contextType.toLowerCase())) {
            return true;
        } else if (commandType.contains("|")) {
            String[] types = commandType.split("|");
            boolean foundType = false;

            for (String type : types) {
                if (type.toLowerCase().equals(contextType.toLowerCase())) {
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
    public List<ICommand> getCommands(String namespace, String type) {
        return getCommandsFromNamespace(namespace)
                .stream().filter(e -> e.getType().equals(type)).collect(Collectors.toList());
    }

    /**
     * This will return the list of commands in the dispatcher.
     *
     * @param type - THe type of command.
     * @return List
     */
    public List<ICommand> getCommands(String type) {
        List<ICommand> commandsToReturn = new ArrayList<>();
        commands.keySet().forEach(k -> commandsToReturn.addAll(getCommands(k, type)));
        return commandsToReturn;
    }
}