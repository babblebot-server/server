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

package net.bdavies.core;

import com.google.inject.Inject;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.command.Command;
import net.bdavies.api.command.CommandParam;
import net.bdavies.api.command.ICommandContext;
import net.bdavies.api.command.ICommandDispatcher;
import net.bdavies.api.config.IDiscordConfig;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.api.plugins.IPluginEvents;
import net.bdavies.api.plugins.IPluginSettings;
import net.bdavies.api.plugins.Plugin;
import net.bdavies.db.InjectRepository;
import net.bdavies.db.Repository;
import net.bdavies.db.model.Model;
import net.bdavies.db.model.serialization.util.Utilities;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This will implement the core Terminal and Discord Commands such as Exit, Help, Ignore, Listen.
 * If your using this as your plugin template when making commands it is wise to name your commands with a
 * prefix so
 * if doesnt
 * clash with other plugins e.g. !bbhelp, !bbignore, !bblisten
 * <p>
 * As this is the core plugin these commands are staying on the default namespace.
 * If you try to override these commands you will get an error from the {@link ICommandDispatcher}
 * <p>
 * * //Should be the only plugin that runs on the default namespace.
 * * //Please use prefix e.g. bb;
 * * //Common convention to use is to a shorthand name for your plugin
 * * // e.g. for a Moderation Plugin do mod-
 * * // !mod-ban @User#3434
 * * // !mod-kick @User#3434
 * * <p>
 * * //e.g. for a Colour picking plugin do col-
 * * // !col-set #ffffff
 * * // !col-update @User#3434 #ggfgfg
 * * <p>
 * * //etc..
 * * //You can add this prefix to the config.json under your extras slot.
 * * //like "namespace": "",
 * * //Then load load on boot and reload.
 * * //Store it in a local variable an reference it here.
 * * //Please refer to the documentation for using your own custom plugin configuration.
 * *
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Plugin(author = "Ben Davies <me@bdavies.net>", namespace = "")
public class CorePlugin implements IPluginEvents
{

    private final ICommandDispatcher commandDispatcher;

    private final IApplication application;

    private final IDiscordConfig config;

    private final AnnouncementService announcementService;

    @InjectRepository(Ignore.class)
    private Repository<Ignore> ignoreRepo;

    @InjectRepository(AnnouncementChannel.class)
    private Repository<AnnouncementChannel> announcementChannelRepo;

    @Inject
    public CorePlugin(ICommandDispatcher commandDispatcher, IApplication application, IDiscordConfig config)
    {
        this.commandDispatcher = commandDispatcher;
        this.application = application;
        this.config = config;
        announcementService = application.get(AnnouncementService.class);
    }

    @Override
    public void onReload()
    {
        log.info("Plugin reload");
    }

    @Override
    public void onBoot(IPluginSettings settings)
    {
        log.info("Booting Core Plugin");
        log.info("All Ignores: {}", ignoreRepo.getAll());
        log.info("All Announcement Channels: {}", announcementChannelRepo.getAll());
        commandDispatcher.registerGlobalMiddleware(context ->
                ignoreRepo.find(b -> b.where(Ignore::getChannel, context.getMessage().getChannel()))
                        .isEmpty()
                        || "listen".equals(context.getCommandName()));

        announcementService.start();
        IDiscordFacade discordFacade = application.get(IDiscordFacade.class);
        discordFacade.registerEventHandler(ReadyEvent.class, r -> {
            if (application.hasArgument("-restart"))
            {
                announcementService.sendMessage("The bot has now restarted");
            }
        });

    }

    @Override
    public void onShutdown()
    {
        announcementService.stop();
    }

    @Command(description = "Start listening again to a channel the bot will then start responding again.")
    public void listen(ICommandContext commandContext)
    {

        Optional<Ignore> ignore = ignoreRepo.findFirst(b ->
                b.where(Ignore::getChannel, commandContext.getMessage().getChannel()));
        if (ignore.isEmpty())
        {
            commandContext.getCommandResponse().sendString("Channel is not ignored, so cancelling command.");
        } else
        {
            ignore.get().delete();
            commandContext.getCommandResponse()
                    .sendString("You can now use BabbleBot in this channel again.");
        }
    }

    @Command(description = "Ignore a channel so the bot wont respond")
    public void ignore(ICommandContext commandContext)
    {
        Optional<Ignore> ignore = ignoreRepo.findFirst(b ->
                b.where(Ignore::getChannel, commandContext.getMessage().getChannel()));

        if (ignore.isPresent())
        {
            commandContext.getCommandResponse()
                    .sendString("Channel is already ignored, so cancelling command.");
        } else
        {
            Ignore newIgnore = ignoreRepo.create();
            Message message = commandContext.getMessage();
            newIgnore.setChannel(Utilities.monoBlock(message.getChannel().cast(TextChannel.class)));
            newIgnore.setIgnoredBy(message.getAuthor().orElseThrow());
            newIgnore.setGuild(Utilities.monoBlock(message.getGuild()));
            newIgnore.save();
        }

        commandContext.getCommandResponse().sendString("BabbleBot is now ignoring this channel");
    }

    @Command(aliases = {"register-announcement-channel",
            "register-ac"},
            description = "Register the channel where the command is ran as a announcement channel")
    public Mono<String> register(ICommandContext commandContext)
    {
        return commandContext.getMessage().getGuild().flatMap(g -> {
            Optional<AnnouncementChannel> model =
                    announcementChannelRepo
                            .findFirst(b -> b.where(AnnouncementChannel::getGuild, g));
            if (model.isPresent())
            {
                AnnouncementChannel channel = model.get();
                return commandContext.getMessage()
                        .getChannel()
                        .cast(TextChannel.class)
                        .flatMap(c ->
                                c.equals(channel.getChannel())
                                        ? Mono.just("Already registered within this server and channel")
                                        : Mono.just("Already registered within this server, on channel: " +
                                        channel.getChannel().getName() + ". You can remove it by doing " +
                                        config.getCommandPrefix() + "remove-ac on that channel"));
            } else
            {
                AnnouncementChannel channel = announcementChannelRepo.create();
                channel.setGuild(g);
                return commandContext.getMessage()
                        .getChannel()
                        .cast(TextChannel.class)
                        .flatMap(c -> {
                            channel.setChannel(c);
                            channel.save();
                            return Mono.just("Registered " + c.getName() +
                                    ", as a announcement channel.");
                        });
            }
        });
    }

    @Command(aliases = {"remove-announcement-channel",
            "remove-ac"},
            description = "Remove the channel where the command is ran as a announcement channel")
    public Mono<String> remove(ICommandContext commandContext)
    {
        return commandContext.getMessage().getGuild().flatMap(g -> {
            Optional<AnnouncementChannel> model = announcementChannelRepo.findFirst(b ->
                    b.where(AnnouncementChannel::getGuild, g)
                            .and(AnnouncementChannel::getChannel,
                                    commandContext.getMessage().getChannel()));
            model.ifPresent(Model::delete);
            if (model.isEmpty())
            {
                return Mono.just("Unable to remove this channel as a announcement channel " +
                        "as it is not registered");
            }
            return commandContext.getMessage().getChannel()
                    .flatMap(c -> Mono.just("Removed " + ((TextChannel) c).getName()
                            + ", as a announcement channel."));
        });
    }

    @Command(description = "Restart bot...")
    @CommandParam(value = "password", optional = false, canBeEmpty = false, exampleValue = "password123")
    public Mono<String> restart(ICommandContext commandContext)
    {
        if (commandContext.hasParameter("password"))
        {
            String password = commandContext.getParameter("password");
            String serverPassword = config.getShutdownPassword();
            if (password.equals(serverPassword))
            {
                announcementService.sendMessage("Restarting bot...");
                return Mono.create(sink -> {
                    application.restart();
                    sink.success();
                });
            } else
            {
                return Mono.just("Password incorrect...");
            }
        } else
        {
            return Mono.just("You require a password to ru this command.");
        }
    }

    @Command(description = "This will help you to discover all the commands and their features.",
            type = "All",
            exampleValue = "ignore")
    @CommandParam(value = "cmd", canBeEmpty = false,
            exampleValue = "ignore")
    public Mono<Consumer<EmbedCreateSpec>> help(ICommandContext commandContext)
    {

        if (commandContext.hasNonEmptyParameter("cmd") || !commandContext.getValue().isEmpty())
        {
            String command = commandContext.hasNonEmptyParameter("cmd")
                    ? commandContext.getParameter("cmd")
                    : commandContext.getValue();


            return Mono.just(spec -> {
                spec.setTimestamp(Instant.now());
                AtomicBoolean hasFoundCommand = new AtomicBoolean(false);
                String namespace = commandDispatcher.getNamespaceFromCommandName(command);
                String alias = command.replace(namespace, "");
                log.info(command);
                commandDispatcher.getCommandByAlias(namespace, alias, commandContext.getType())
                        .subscribe(cmd -> {
                            hasFoundCommand.set(true);
                            Optional<IPluginSettings> settings = application.getPluginContainer()
                                    .getPluginSettingsFromNamespace(namespace);
                            String author = command;
                            if (settings.isPresent())
                            {
                                author = settings.get().getName();
                                author =
                                        author.substring(0, 1).toUpperCase(Locale.ROOT) + author.substring(1);
                                author = author + " Plugin";
                                spec.setFooter("Please refer to plugin documentation for further information",
                                        null);
                            }
                            spec.setAuthor(author, null, null);
                            spec.setTitle(
                                    alias.substring(0, 1).toUpperCase(Locale.ROOT) + alias.substring(1) +
                                            " Command");
                            spec.addField("Key", "```css\n" +
                                    "[" + config.getCommandPrefix() +
                                    "(alias|alias2....)] : This is displaying the commands aliases.\n" +
                                    "[-(param?)]: This is an optional empty parameter\n" +
                                    "[-(param*)]: Required empty parameter\n" +
                                    "[-(param?)=*]: Optional value based parameter\n" +
                                    "[-(param*)=*]: Required value based parameter\n" +
                                    "[(value?)]: Optional value\n" +
                                    "[(value*)]: Required value\n" +
                                    "```", false);

                            spec.addField("Usage", "```\n" + cmd.getUsage() + "\n```", false);
                            StringBuilder examples = new StringBuilder("```md\n");
                            AtomicInteger index = new AtomicInteger(1);
                            Arrays.stream(cmd.getExamples()).forEach(e -> examples.append("[")
                                    .append(index.getAndIncrement())
                                    .append("]: ")
                                    .append(e)
                                    .append("\n"));
                            examples.append("```");
                            spec.addField("Examples", examples.toString(), false);
                        }, null, () -> {
                            if (!hasFoundCommand.get())
                            {
                                spec.setTitle("Command Not found");
                            }
                        });
            });
        } else
        {
            return Mono.just(spec -> {
                spec.setTitle("Commands");
                spec.setDescription(
                        "This is all the commands available to babblebot. Please use **!help** " +
                                "{command-name} for more " +
                                "information");
                spec.setTimestamp(Instant.now());

                commandDispatcher.getRegisteredNamespaces().subscribe(namespace -> {
                    AtomicReference<StringBuilder> sb = new AtomicReference<>(new StringBuilder("```css\n"));
                    commandDispatcher.getCommandsFromNamespace(namespace)
                            .subscribe(cmd -> sb.get().append("[")
                                    .append(config.getCommandPrefix())
                                    .append(namespace)
                                    .append(cmd.getAliases()[0])
                                    .append("]: ")
                                    .append(cmd.getDescription())
                                    .append("\n"), null, () -> {
                                sb.get().append("```");
                                Optional<IPluginSettings> settings = application.getPluginContainer()
                                        .getPluginSettingsFromNamespace(namespace);
                                String name = namespace + " Commands";
                                if (settings.isPresent())
                                {
                                    name = settings.get().getName();
                                    name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
                                    name = name + " Commands";
                                }
                                spec.addField(name, sb.get().toString(), false);
                            });
                });

            });
        }
    }
}
