package uk.co.bjdavies.discord.services;

import com.google.inject.Inject;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.command.CommandDispatcher;
import uk.co.bjdavies.command.parser.DiscordMessageParser;
import uk.co.bjdavies.variables.VariableParser;

import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean hasSentMessage = new AtomicBoolean(false);
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(e ->
                        Mono.justOrEmpty(e.getMessage().getContent())
                                .filter(m -> m.startsWith(config.getCommandPrefix()))
                                .filterWhen(m ->
                                        e.getMessage()
                                                .getAuthorAsMember()
                                                .map(User::isBot)
                                                .map(isBot -> !isBot)
                                ).subscribe(m -> {
                            CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                            cd.execute(new DiscordMessageParser(e.getMessage()), m.replace(config.getCommandPrefix(), ""), application)
                                    .subscribe(s -> {
                                        if (s.isStringResponse()) {
                                            e.getMessage().getChannel().subscribe(c ->
                                                    c.createMessage(new VariableParser(s.getStringResponse(), application).toString())
                                                            .subscribe());
                                            hasSentMessage.set(true);
                                        } else if (s.getEmbedCreateSpecResponse() != null) {
                                            e.getMessage().getChannel().subscribe(c ->
                                                    c.createEmbed(s.getEmbedCreateSpecResponse())
                                                            .subscribe());
                                            hasSentMessage.set(true);
                                        }
                                    }, throwable -> {
                                        hasSentMessage.set(true);
                                    }, () -> {
                                        if (!hasSentMessage.get()) {
                                            e.getMessage().getChannel().subscribe(c ->
                                                    c.createMessage("Babblebot command could'nt be found.").subscribe());
                                        }
                                    });
                        }));
    }
}
