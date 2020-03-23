package uk.co.bjdavies.core;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.Command;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.plugins.IPlugin;

import java.util.Optional;

/**
 * This will implement the core Terminal and Discord Commands such as Exit, Help, Ignore, Listen.
 * If your using this as your plugin template when making commands it is wise to name your commands with a prefix so if doesnt
 * clash with other plugins e.g. !bbhelp, !bbignore, !bblisten
 * <p>
 * As this is the core plugin these commands are staying on the default namespace.
 * If you try to override these commands you will get an error from the {@link ICommandDispatcher}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class CorePlugin implements IPlugin {

    private final ICommandDispatcher commandDispatcher;

    private final IApplication application;

    private final IDiscordConfig config;

    @Inject
    public CorePlugin(ICommandDispatcher commandDispatcher, IApplication application, IDiscordConfig config) {
        this.commandDispatcher = commandDispatcher;
        this.application = application;
        this.config = config;
    }

    @Override
    public String getName() {
        return "core";
    }

    @Override
    public String getVersion() {
        return application.getServerVersion();
    }

    @Override
    public String getAuthor() {
        return "Ben Davies (ben.davies99@outlook.com)";
    }

    @Override
    public String getMinimumServerVersion() {
        return application.getServerVersion();
    }

    @Override
    public String getMaximumServerVersion() {
        return "0";
    }

    /**
     * //Should be the only plugin that runs on the default namespace.
     * //Please use prefix e.g. bb;
     * //Common convention to use is to a shorthand name for your plugin
     * // e.g. for a Moderation Plugin do mod-
     * // !mod-ban @User#3434
     * // !mod-kick @User#3434
     * <p>
     * //e.g. for a Colour picking plugin do col-
     * // !col-set #ffffff
     * // !col-update @User#3434 #ggfgfg
     * <p>
     * //etc..
     * //You can add this prefix to the config.json under your extras slot.
     * //like "namespace": "",
     * //Then load load on boot and reload.
     * //Store it in a local variable an reference it here.
     * //Please refer to the documentation for using your own custom plugin configuration.
     *
     * @return String
     */
    @Override
    public String getNamespace() {
        return "";
    }

    @Override
    public void onReload() {
        log.info("Plugin reload");
    }

    @Override
    public void onBoot() {
        log.info("Booting Core Plugin");
        commandDispatcher.registerGlobalMiddleware(context ->
                Ignore.where("channelId", context.getMessage().getChannelId().asString()).doesntExist()
                        || context.getCommandName().equals("listen"));

        commandDispatcher.registerPluginMiddleware(this, context -> false);
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

    @Command(description = "This will help you to discover all the commands and their features.",
            usage = "help (-cmd=CommandName)",
            type = "All")
    public String help(ICommandContext commandContext) {

        //TODO: Separate Namespaces
        //TODO: Fix not being to see !help bbtest
        //TODO: add namespace as a field in the help menu
        return "Yay its help time";
//        if (commandContext.getValue().equals("") && !commandContext.hasParameter("cmd")) {
//            StringBuilder sb = new StringBuilder(commandContext.getType().equals("Discord") ? "```" : "");
//
//            for (ICommand command : commandDispatcher.getCommands(commandContext.getType())) {
//                sb.append(config.getCommandPrefix()).append(command.getAliases()[0]).append(" - ").append(command.getDescription()).append("\n\n");
//            }
//
//            sb.append(commandContext.getType().equals("Discord") ? "```" : "");
//
//            if (commandContext.getType().equals("Discord")) {
//                commandContext.getCommandUtils().sendPrivateMessage("List of all commands: use !help {command} for more\n" + sb.toString());
//                return "Check your DMs I would have sent you a message :)";
//            } else {
//                return "List of all commands: use !help {command} for more\n" + sb.toString();
//            }
//
//
//        } else {
//
//            String commandName;
//
//            if (commandContext.hasParameter("cmd")) {
//
//                commandName = commandContext.getParameter("cmd");
//
//            } else {
//                commandName = commandContext.getValue();
//            }
//
//            Optional<ICommand> command = commandDispatcher
//                    .getCommandByAlias(commandDispatcher.getNamespaceFromCommandName(commandName), commandName,
//                            commandContext.getType());
//            if (command.isPresent()) {
//
//                ICommand commandFound = command.get();
//
//                StringBuilder stringBuilder = new StringBuilder();
//
//                stringBuilder.append("Help for Command: ").append(commandName).append("\n");
//
//                stringBuilder.append("\nAlias(es):\n").append(commandContext.getType().equals("Discord") ? "```" : "");
//
//                List<String> aliases = Arrays.asList(commandFound.getAliases());
//
//                aliases.forEach(e -> {
//                    stringBuilder.append(e);
//                    if (!(aliases.indexOf(e) == aliases.size() - 1)) {
//                        stringBuilder.append("/");
//                    }
//                });
//
//                stringBuilder.append(commandContext.getType().equals("Discord") ? "```" : "")
//                        .append("\nDescription: \n").append(commandContext.getType().equals("Discord") ? "```" : "")
//                        .append(commandFound.getDescription());
//
//                stringBuilder.append(commandContext.getType().equals("Discord") ? "```" : "").append("\nUsage: \n")
//                        .append(commandContext.getType().equals("Discord") ? "```" : "").append(commandFound.getUsage())
//                        .append(commandContext.getType().equals("Discord") ? "```" : "");
//
//                return stringBuilder.toString();
//
//            } else {
//                return "The command entered does not exist.";
//            }
//        }
    }
}
