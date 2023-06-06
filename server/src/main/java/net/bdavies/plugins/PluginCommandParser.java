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

package net.bdavies.plugins;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.DiscordCommandContext;
import net.bdavies.api.IApplication;
import net.bdavies.api.command.*;
import net.bdavies.api.config.IConfig;
import net.bdavies.api.discord.IDiscordCommandUtil;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.api.obj.message.discord.DiscordMessage;
import net.bdavies.api.plugins.IPluginSettings;
import net.bdavies.discord.DiscordCommandUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public final class PluginCommandParser
{
    private final IPluginSettings pluginSettings;
    private final Object pluginObj;
    private final IApplication application;


    public PluginCommandParser(IApplication application, IPluginSettings pluginSettings,
                               Object pluginObj)
    {
        this.pluginSettings = pluginSettings;
        this.pluginObj = pluginObj;
        this.application = application;
    }

    public List<ICommand> parseCommands()
    {

        List<ICommand> commands = new ArrayList<>();

        for (final Method method : pluginObj.getClass().getDeclaredMethods())
        {
            if (method.isAnnotationPresent(Command.class))
            {
                if (Arrays.asList(method.getParameterTypes()).contains(ICommandContext.class))
                {
                    Command command = method.getAnnotation(Command.class);
                    List<String> newAliases = new ArrayList<>(Arrays.asList(command.aliases()));
                    if (command.aliases().length == 0)
                    {
                        newAliases.add(method.getName());
                    }

                    commands.add(new ICommand()
                    {
                        @Override
                        public String[] getAliases()
                        {
                            return newAliases.toArray(new String[0]);
                        }

                        @Override
                        public String[] getExamples()
                        {
                            return generateExamples(method);
                        }

                        @Override
                        public String getDescription()
                        {
                            return command.description();
                        }

                        @Override
                        public String getUsage()
                        {
                            return generateUsage(method);
                        }

                        @Override
                        public String getType()
                        {
                            return command.type();
                        }

                        @Override
                        public void exec(IApplication application, ICommandContext commandContext)
                        {
                            PluginCommandDefinition cd = new PluginCommandExecutionBuilder(method.getName(),
                                    pluginObj.getClass())
                                    .setParameterTypes(method.getParameterTypes())
                                    .setArgs(setupArgs(method, commandContext))
                                    .build();
                            if (method.getReturnType().equals(Void.TYPE))
                            {
                                executePluginCommand(cd);
                            } else
                            {
                                if (!commandContext.getCommandResponse()
                                        .send(method.getGenericReturnType(), executePluginCommand(cd)))
                                {
                                    log.error("Plugin command: " + pluginSettings.getName() + "#" +
                                            method.getName() +
                                            " is not supported, command will not run please return a valid " +
                                            "response type or use void and use commandContext" +
                                            ".getCommandResponse()" +
                                            ".send(Data)");
                                }
                            }
                        }

                        @Override
                        public boolean validateUsage(ICommandContext commandContext)
                        {
                            return (!command.requiresValue() || !commandContext.getValue().equals("")) &&
                                    hasRequiredParams(commandContext, method);
                        }
                    });
                }
            }
        }

        return commands;
    }

    private Object[] setupArgs(Method method, ICommandContext commandContext)
    {
        //TODO: Move this into a discord handler
        List<Object> objs = new LinkedList<>();
        Arrays.stream(method.getParameterTypes()).forEach(t -> {
            if (t.equals(ICommandContext.class))
            {
                objs.add(commandContext);
            } else if (t.equals(DiscordMessage.class))
            {
                if (isDiscordCommandContext(commandContext))
                {
                    objs.add(((DiscordCommandContext) commandContext).getMessage());
                }
            } else if (t.equals(IDiscordCommandUtil.class))
            {
                if (isDiscordCommandContext(commandContext))
                {
                    objs.add(new DiscordCommandUtil((DiscordCommandContext)
                            commandContext, application.get(IDiscordFacade.class)));
                }
            } else
            {
                log.error("Cannot handle parameter type: {}", t);
            }
        });
        return objs.toArray(new Object[0]);
    }

    private boolean isDiscordCommandContext(ICommandContext commandContext)
    {
        if (commandContext instanceof DiscordCommandContext)
        {
            return true;
        } else
        {
            log.error(
                    "Unable to pass DiscordMessage as the context of the command is not Discord it " +
                            "is {}",
                    commandContext.getClass().getSimpleName());

            return false;
        }
    }

    private boolean hasRequiredParams(ICommandContext commandContext, Method method)
    {
        AtomicBoolean isValid = new AtomicBoolean(true);
        Arrays.stream(method.getAnnotationsByType(CommandParam.class))
                .filter(cp -> !cp.optional())
                .forEach(cp -> {
                    if (isValid.get())
                    {
                        if (cp.canBeEmpty())
                        {
                            isValid.set(commandContext.hasParameter(cp.value()));
                        } else
                        {
                            isValid.set(commandContext.hasNonEmptyParameter(cp.value()));
                        }
                    }
                });
        return isValid.get();
    }

    private String generateUsage(Method method)
    {
        StringBuilder stringBuilder = new StringBuilder(
                application.get(IConfig.class).getDiscordConfig().getCommandPrefix());
        Command command = method.getAnnotation(Command.class);
        if (command.aliases().length == 0)
        {
            stringBuilder.append(method.getName()).append(" ");
        } else
        {
            stringBuilder.append("(");
            for (int i = 0; i < command.aliases().length; i++)
            {
                if (i != command.aliases().length - 1)
                {
                    stringBuilder.append(command.aliases()[i]).append("|");
                } else
                {
                    stringBuilder.append(command.aliases()[i]);
                }
            }
            stringBuilder.append(") ");
        }

        Arrays.stream(method.getAnnotationsByType(CommandParam.class)).forEach(cp -> {
            stringBuilder.append("-(").append(cp.value());
            if (cp.optional())
            {
                stringBuilder.append("?)");
            } else
            {
                stringBuilder.append("*)");
            }

            if (!cp.canBeEmpty())
            {
                stringBuilder.append("=*");
            }
            stringBuilder.append(" ");
        });
        stringBuilder.append("(value");
        if (command.requiresValue())
        {
            stringBuilder.append("*)");
        } else
        {
            stringBuilder.append("?)");
        }

        return stringBuilder.toString();
    }

    private String[] generateExamples(Method method)
    {
        List<String> examples = new ArrayList<>();
        List<String> requiredParams = new ArrayList<>();
        List<String> optionalParams = new ArrayList<>();

        Arrays.stream(method.getAnnotationsByType(CommandParam.class))
                .forEach(cp -> {
                    if (!cp.optional())
                    {
                        addToExamplesParamList(requiredParams, cp);
                    } else
                    {
                        addToExamplesParamList(optionalParams, cp);
                    }
                });

        Command command = method.getAnnotation(Command.class);
        StringBuilder sb = new StringBuilder(
                application.get(IConfig.class).getDiscordConfig().getCommandPrefix());
        String defaultCommand = "";

        if (command.aliases().length == 0)
        {
            sb.append(method.getName()).append(" ");
        } else
        {
            sb.append(command.aliases()[0]).append(" ");
        }

        requiredParams.forEach(sb::append);
        defaultCommand = sb.toString();

        String finalDefaultCommand = defaultCommand;
        String exampleValue = !command.exampleValue().equals("") ? command.exampleValue() : "example-value";
        exampleValue = command.requiresValue() ? exampleValue : "";
        String finalExampleValue = exampleValue;
        optionalParams.forEach(op -> examples.add(finalDefaultCommand + op + " " + finalExampleValue));
        examples.add(finalDefaultCommand + exampleValue);

        Arrays.stream(method.getAnnotationsByType(CommandExample.class))
                .map(CommandExample::value)
                .forEach(examples::add);

        return examples.toArray(new String[0]);
    }

    private void addToExamplesParamList(List<String> params, CommandParam cp)
    {
        if (cp.canBeEmpty())
        {
            params.add("-" + cp.value() + " ");
        } else
        {
            if (!cp.exampleValue().equals(""))
            {
                String exampleValue = cp.exampleValue().split(" ").length == 0
                        ? cp.exampleValue()
                        : "\"" + cp.exampleValue() + "\"";
                params.add("-" + cp.value() + "=" + exampleValue + " ");
            } else
            {
                params.add("-" + cp.value() + "=value ");
            }
        }
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
    public Object executePluginCommand(PluginCommandDefinition pluginCommandDefinition)
    {
        Class<?> pluginClass = pluginCommandDefinition.getPluginClass();
        try
        {

            Method method = pluginClass.getDeclaredMethod(pluginCommandDefinition.getName(),
                    pluginCommandDefinition.getParameterTypes());
            method.setAccessible(true);


            if (method.isAnnotationPresent(Command.class))
            {
                return method.invoke(pluginObj, pluginCommandDefinition.getArgs());
            }

        }
        catch (NoSuchMethodException e)
        {
            log.error("The module command entered does not exist inside this module.", e);
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            log.error("The module command did not execute correctly.", e);
        }
        return null;
    }


}
