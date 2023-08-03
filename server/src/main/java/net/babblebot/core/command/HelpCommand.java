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
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.embed.EmbedField;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.obj.message.discord.interactions.Row;
import net.babblebot.api.obj.message.discord.interactions.button.Button;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownOption;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownView;
import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.api.plugins.IPluginSettings;
import net.babblebot.discord.services.DiscordMessagingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Listen Command for the Core Plugin
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.30
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

    public DropdownView exec(DiscordMessage message, ICommandContext ctx)
    {
        DiscordMessageSendSpec defaultView = DiscordMessageSendSpec.fromEmbed(
                handleHelpNoCommand(message, ctx));
        DropdownMenu menu = getCommandsDropdownMenu(ctx);
        DropdownView view = DropdownView.builder()
                .menu(menu)
                .onSelectionView((s, msg) -> {
                    if (s == null)
                    {
                        return defaultView;
                    }

                    return handleHelpForCommand(message, ctx, s);
                })
                .build();
        return view;
    }

    private DropdownMenu getCommandsDropdownMenu(ICommandContext ctx)
    {
        DropdownMenu.DropdownMenuBuilder builder = DropdownMenu.builder();
        if (ctx.hasNonEmptyParameter("cmd") || !ctx.getValue().isEmpty())
        {
            String command = ctx.hasNonEmptyParameter("cmd")
                    ? ctx.getParameter("cmd") : ctx.getValue();
            builder.addDefaultValue(command);
        }

        commandRegistry.getRegisteredNamespaces().forEach(namespace -> {
            commandRegistry.getCommandsFromNamespace(namespace).forEach(cmd -> {
                String fullCommandName = namespace + cmd.getAliases()[0];
                builder.addOption(
                        DropdownOption.from(stripLabel(fullCommandName + " " + cmd.getDescription()),
                                fullCommandName));
            });
        });

        return builder.build();
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

                String fullCommandName = namespace + cmd.getAliases()[0];
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

    private String stripLabel(String s)
    {
        if (s.length() >= 100)
        {
            return s.substring(0, 96) + "...";
        }
        return s;
    }

    private DiscordMessageSendSpec handleHelpForCommand(DiscordMessage message, ICommandContext ctx,
                                                        String cmd)
    {
        String namespace = commandRegistry.getNamespaceFromCommandName(cmd);
        String commandName = cmd.replace(namespace, "");

        Optional<ICommand> commandOpt = commandRegistry.getCommandByAlias(namespace, commandName,
                ctx.getType());

        if (commandOpt.isEmpty())
        {
            return DiscordMessageSendSpec.fromEmbed(EmbedMessage.builder()
                    .title("Command not found")
                    .description("Command: " + cmd + " was not found in the Command Registry")
                    .build());
        }

        ICommand command = commandOpt.get();

        EmbedField key = getKeyField();
        EmbedField usage = getUsageField(command);
        EmbedField examples = getExamplesField(command);
        DiscordMessagingService service = application.get(DiscordMessagingService.class);

        Row row = Row.from(
                Button.primary("Try this command", msg -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            throw new RuntimeException(e);
                        }
                        service.send(message.getGuild(), message.getChannel(), DiscordMessageSendSpec
                                .fromString("$botAction--{}", command.getExamples()[0]));
                    });
                    return DiscordMessageSendSpec.fromString("Running command in 1 second...");
                })
        );

        return DiscordMessageSendSpec.fromEmbed(EmbedMessage.builder()
                        .title(StringUtils.capitalize(commandName) + " Command Help")
                        .description(command.getDescription())
                        .addField(key)
                        .addField(usage)
                        .addField(examples)
                        .build()).toBuilder()
                .addRow(row)
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
