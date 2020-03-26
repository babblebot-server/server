package uk.co.bjdavies.core;

import com.google.inject.Inject;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.Command;
import uk.co.bjdavies.api.command.CommandParam;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.plugins.IPluginEvents;
import uk.co.bjdavies.api.plugins.IPluginSettings;
import uk.co.bjdavies.api.plugins.Plugin;
import uk.co.bjdavies.api.plugins.PluginConfig;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This will implement the core Terminal and Discord Commands such as Exit, Help, Ignore, Listen.
 * If your using this as your plugin template when making commands it is wise to name your commands with a prefix so if doesnt
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
@Plugin(author = "Ben Davies <ben.davies99@outlook.com>", namespace = "")
public class CorePlugin implements IPluginEvents {

    private final ICommandDispatcher commandDispatcher;

    private final IApplication application;

    private final IDiscordConfig config;

    @PluginConfig
    private CorePluginConfig pluginConfig;

    @Inject
    public CorePlugin(ICommandDispatcher commandDispatcher, IApplication application, IDiscordConfig config) {
        this.commandDispatcher = commandDispatcher;
        this.application = application;
        this.config = config;
    }

    @Override
    public void onReload() {
        log.info("Plugin reload");
    }

    @Override
    public void onBoot(IPluginSettings settings) {
        log.info("Booting Core Plugin");
        commandDispatcher.registerGlobalMiddleware(context ->
                Ignore.where("channelId", context.getMessage().getChannelId().asString()).doesntExist()
                        || context.getCommandName().equals("listen"));

        log.info("Plugin config: " + pluginConfig);
    }

    @Override
    public void onShutdown() {
    }

    @Command(description = "Start listening again to a channel the bot will then start responding again.")
    public void listen(ICommandContext commandContext) {
        Optional<Ignore> model = Ignore.where("channelId", commandContext.getMessage().getChannelId().asString())
                .first();
        model.ifPresent(Model::delete);
        if (model.isEmpty()) {
            commandContext.getCommandResponse().sendString("Channel is not ignored, so cancelling command.");
        }
        commandContext.getCommandResponse().sendString("You can now use BabbleBot in this channel again.");
    }

    @Command(description = "Ignore a channel so the bot wont respond")
    public void ignore(ICommandContext commandContext) {
        Optional<Ignore> model = Ignore.where("channelId", commandContext.getMessage().getChannelId().asString())
                .first();
        if (model.isPresent()) {
            commandContext.getCommandResponse().sendString("Channel is already ignored, so cancelling command.");
        } else {
            Ignore newIgnore = new Ignore();
            newIgnore.setChannelId(commandContext.getMessage().getChannelId().asString());
            newIgnore.setGuildId(commandContext.getMessage().getGuild().block().getId().asString());
            newIgnore.setIgnoredBy(commandContext.getMessage().getAuthor().get().getId().asString());
            newIgnore.save();
        }

        commandContext.getCommandResponse().sendString("BabbleBot is now ignoring this channel");
    }

    @Command(description = "This will help you to discover all the commands and their features.", type = "All",
            exampleValue = "ignore")
    @CommandParam(value = "cmd", canBeEmpty = false,
            exampleValue = "ignore")
    public Mono<Consumer<EmbedCreateSpec>> help(ICommandContext commandContext) {

        if (commandContext.hasNonEmptyParameter("cmd") || !commandContext.getValue().equals("")) {
            String command = commandContext.hasNonEmptyParameter("cmd")
                    ? commandContext.getParameter("cmd")
                    : commandContext.getValue();

            return Mono.just(spec -> {
                spec.setTimestamp(Instant.now());
                AtomicBoolean hasFoundCommand = new AtomicBoolean(false);
                String namespace = commandDispatcher.getNamespaceFromCommandName(command);
                String alias = command.replace(namespace, "");
                commandDispatcher.getCommandByAlias(namespace, alias, commandContext.getType())
                        .subscribe(cmd -> {
                            hasFoundCommand.set(true);
                            Optional<IPluginSettings> settings = application.getPluginContainer()
                                    .getPluginSettingsFromNamespace(namespace);
                            String author = command;
                            if (settings.isPresent()) {
                                author = settings.get().getName();
                                author = author.substring(0, 1).toUpperCase() + author.substring(1);
                                author = author + " Plugin";
                                spec.setFooter("Please refer to plugin documentation for further information",
                                        null);
                            }
                            spec.setAuthor(author, null, null);
                            spec.setTitle(alias.substring(0, 1).toUpperCase() + alias.substring(1) + " Command");
                            spec.addField("Key", "```css\n" +
                                    "[!(alias|alias2....)] : This is displaying the commands aliases.\n" +
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
                            Arrays.stream(cmd.getExamples()).forEach(e -> {
                                examples.append("[")
                                        .append(index.getAndIncrement())
                                        .append("]: ")
                                        .append(e)
                                        .append("\n");
                            });
                            examples.append("```");
                            spec.addField("Examples", examples.toString(), false);
                        }, null, () -> {
                            if (!hasFoundCommand.get()) {
                                spec.setTitle("Command Not found");
                            }
                        });
            });
        } else {
            return Mono.just(spec -> {
                spec.setTitle("Commands");
                spec.setDescription("This is all the commands available to babblebot. Please use **!help** {command-name} for more information");
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
                                if (settings.isPresent()) {
                                    name = settings.get().getName();
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                    name = name + " Commands";
                                }
                                spec.addField(name, sb.get().toString(), false);
                            });
                });

            });
        }
    }
}
