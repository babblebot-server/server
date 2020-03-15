package uk.co.bjdavies.discord.services;

import com.google.inject.Inject;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.command.CommandDispatcher;
import uk.co.bjdavies.command.parser.DiscordMessageParser;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class Discord4JBotMessageService {

    private final DiscordClient client;

    private final IDiscordConfig config;

    private final IApplication application;

    private final ICommandDispatcher commandDispatcher;

    @Inject
    public Discord4JBotMessageService(DiscordClient client, IDiscordConfig config, IApplication application, ICommandDispatcher commandDispatcher) {
        this.client = client;
        this.config = config;
        this.application = application;
        this.commandDispatcher = commandDispatcher;
    }

    public void register() {
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(e -> Mono.justOrEmpty(e.getMessage().getContent()).log()
                        .flatMap(c -> Mono.just(c.startsWith(config.getCommandPrefix()))
                                .filter(r -> r)
                                .filterWhen(a -> e.getMessage()
                                        .getAuthorAsMember()
                                        .map(User::isBot)
                                        .map(isBot -> !isBot))
                                .flatMap(r -> Mono.just((CommandDispatcher) commandDispatcher)
                                        .flatMap(cd -> Mono.justOrEmpty(
                                                cd.execute(new DiscordMessageParser(e.getMessage()), c.replace(config.getCommandPrefix(), ""), application))
                                                .filter(cr -> !cr.isEmpty())
                                                .flatMap(cr -> e.getMessage().getChannel()
                                                        .flatMap(chan -> chan.createMessage(cr))
                                                        .then()))
                                ))).log(Loggers.getLogger(Discord4JBotMessageService.class)).subscribe();
    }
}
