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

package net.babblebot.core.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.ICommand;
import net.babblebot.api.command.ICommandContext;
import net.babblebot.api.command.ICommandRegistry;
import net.babblebot.api.config.IConfig;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.embed.EmbedField;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.api.plugins.IPluginSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Listen Command for the Core Plugin
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.26
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HelpCommand
{
    private final ICommandRegistry commandRegistry;
    private final IConfig config;
    private final IApplication application;
    private final IPluginContainer pluginContainer;

    public EmbedMessage exec(DiscordMessage message, ICommandContext ctx)
    {
        if (ctx.hasNonEmptyParameter("cmd") || !ctx.getValue().isEmpty())
        {
            String command = ctx.hasNonEmptyParameter("cmd")
                    ? ctx.getParameter("cmd") : ctx.getValue();
            return handleHelpForCommand(message, ctx, command);
        } else
        {
            return handleHelpNoCommand(message, ctx);
        }
    }

    private EmbedMessage handleHelpNoCommand(DiscordMessage message, ICommandContext ctx)
    {
        List<EmbedField> embedFields = new LinkedList<>();
        commandRegistry.getRegisteredNamespaces().forEach(namespace -> {
            StringBuilder sb = new StringBuilder("```\n");
            commandRegistry.getCommandsFromNamespace(namespace).forEach(cmd -> {
                sb.append("[")
                        .append(config.getDiscordConfig().getCommandPrefix())
                        .append(namespace)
                        .append(cmd.getAliases()[0])
                        .append("]: ")
                        .append(cmd.getDescription())
                        .append("\n");
            });

            sb.append("```");

            Optional<IPluginSettings> settings = pluginContainer
                    .getPluginSettingsFromNamespace(namespace);
            String name = namespace + " Commands";
            if (settings.isPresent())
            {
                name = settings.get().getName();
                name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring
                        (1);
                name = name + " Commands";
            }

            embedFields.add(EmbedField.builder()
                    .inline(false)
                    .name(name)
                    .value(sb.toString())
                    .build());
        });

        return EmbedMessage.builder()
                .title("Commands")
                .fields(embedFields)
                .description("This is all the commands available to babblebot. Please use **!help** " +
                        "{command-name} for more information")
                .build();
    }

    private EmbedMessage handleHelpForCommand(DiscordMessage message, ICommandContext ctx, String cmd)
    {
        String namespace = commandRegistry.getNamespaceFromCommandName(cmd);
        String commandName = cmd.replace(namespace, "");

        Optional<ICommand> commandOpt = commandRegistry.getCommandByAlias(namespace, commandName,
                ctx.getType());

        if (commandOpt.isEmpty())
        {
            return EmbedMessage.builder()
                    .title("Command not found")
                    .description("Command: " + cmd + " was not found in the Command Registry")
                    .build();
        }

        ICommand command = commandOpt.get();

        EmbedField key = getKeyField();
        EmbedField usage = getUsageField(command);
        EmbedField examples = getExamplesField(command);

        return EmbedMessage.builder()
                .title(StringUtils.capitalize(commandName) + " Command Help")
                .description(command.getDescription())
                .addField(key)
                .addField(usage)
                .addField(examples)
                .build();
    }

    private EmbedField getExamplesField(ICommand command)
    {
        StringBuilder examples = new StringBuilder("```md\n");

        for (int i = 0; i < command.getExamples().length; i++)
        {
            examples.append("[")
                    .append(i)
                    .append("]: ")
                    .append(command.getExamples()[i])
                    .append("\n");
        }
        examples.append("```");

        return EmbedField.builder()
                .name("Examples")
                .value(examples.toString())
                .inline(false)
                .build();
    }

    private EmbedField getUsageField(ICommand command)
    {
        return EmbedField
                .builder()
                .inline(false)
                .name("Usage")
                .value("```\n" + command.getUsage() + "\n```")
                .build();
    }

    private EmbedField getKeyField()
    {
        String value = "```\n" +
                "[" + config.getDiscordConfig().getCommandPrefix() +
                "(alias|alias2....)]: This is displaying the commands aliases.\n" +
                "[-(param?)]: This is an optional empty parameter\n" +
                "[-(param*)]: Required empty parameter\n" +
                "[-(param?)=*]: Optional value based parameter\n" +
                "[-(param*)=*]: Required value based parameter\n" +
                "[(value?)]: Optional value\n" +
                "[(value*)]: Required value\n" +
                "```";

        return EmbedField
                .builder()
                .name("Key")
                .value(value)
                .inline(false)
                .build();
    }
}
