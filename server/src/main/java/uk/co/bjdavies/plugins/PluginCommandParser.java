package uk.co.bjdavies.plugins;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.Command;
import uk.co.bjdavies.api.command.ICommand;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.plugins.IPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public final class PluginCommandParser {

    private final IPlugin plugin;


    public PluginCommandParser(IPlugin plugin) {
        this.plugin = plugin;
    }

    public List<ICommand> parseCommands() {

        List<ICommand> commands = new ArrayList<>();

        for (final Method method : plugin.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                if (Arrays.asList(method.getParameterTypes()).contains(ICommandContext.class) && method.getParameterTypes().length == 1) {
                    Command command = method.getAnnotation(Command.class);
                    List<String> newAliases = new ArrayList<>(Arrays.asList(command.aliases()));
                    if (command.aliases().length == 0) {
                        newAliases.add(method.getName());
                    }

                    commands.add(new ICommand() {
                        @Override
                        public String[] getAliases() {
                            return newAliases.toArray(new String[0]);
                        }

                        @Override
                        public String getDescription() {
                            return command.description();
                        }

                        @Override
                        public String getUsage() {
                            return command.usage().equals("") ? newAliases.get(0) : command.usage();
                        }

                        @Override
                        public String getType() {
                            return command.type();
                        }

                        @Override
                        public String run(IApplication application, ICommandContext commandContext) {
                            return "";
                        }


                        @Override
                        public void exec(IApplication application, ICommandContext commandContext) {
                            PluginCommandDefinition cd = new PluginCommandExecutionBuilder(method.getName(),
                                    plugin.getClass()).setParameterTypes(ICommandContext.class)
                                    .setArgs(commandContext).build();
                            if (method.getReturnType().equals(Void.class)) {
                                executePluginCommand(cd);
                            } else {
                                if (!commandContext.getCommandResponse().send(method.getGenericReturnType(), executePluginCommand(cd))) {
                                    log.error("Plugin command: " + plugin.getName() + "#" + method.getName() +
                                            " is not supported, command will not run please return a valid response type or use void and use commandContext.getCommandResponse()" +
                                            ".send(Data)");
                                }
                            }
                        }

                        @Override
                        public boolean validateUsage(ICommandContext commandContext) {
                            boolean[] isValid = new boolean[]{true};

                            Arrays.stream(command.requiredParams()).forEach(e -> {
                                if (!commandContext.hasParameter(e) && !e.isEmpty()) {
                                    if (isValid[0]) {
                                        isValid[0] = false;
                                    }
                                }
                            });

                            return isValid[0];
                        }
                    });
                }
            }
        }

        return commands;
    }

    /**
     * This will execute a plugin command that are placed in the plugin the command has to be
     * annotated with {@link Command}
     * <p>
     * e.g. play()
     *
     * @param pluginCommandDefinition - This is the set of data that will be used to run the command.
     * @return Object
     */
    public Object executePluginCommand(PluginCommandDefinition pluginCommandDefinition) {
        Class<? extends IPlugin> pluginClass = pluginCommandDefinition.getPluginClass();
        try {

            Method method = pluginClass.getDeclaredMethod(pluginCommandDefinition.getName(), pluginCommandDefinition.getParameterTypes());
            method.setAccessible(true);


            if (method.isAnnotationPresent(Command.class)) {
                return method.invoke(plugin, pluginCommandDefinition.getArgs());
            }

        } catch (NoSuchMethodException e) {
            log.error("The module command entered does not exist inside this module.", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("The module command did not execute correctly.", e);
        }
        return null;
    }


}
