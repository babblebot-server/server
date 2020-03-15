package uk.co.bjdavies.discord;

import com.google.inject.Inject;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.discord.services.Discord4JBotMessageService;
import uk.co.bjdavies.discord.services.Discord4JBotService;

/**
 * This class will setup Discord4J by making a {@link discord4j.core.DiscordClient}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class Discord4JSetup {

    private final IApplication application;
    private final IDiscordConfig config;
    @Getter
    private DiscordClient client;

    @Inject
    public Discord4JSetup(IApplication application, IDiscordConfig config) {
        this.application = application;
        this.config = config;
        this.setupClient();
    }

    private void setupClient() {
        try {
            log.info("Setting up Discord Client");

            client = new DiscordClientBuilder(config.getToken()).build();
            client.getEventDispatcher().on(ReadyEvent.class).subscribe(e -> {
                client.updatePresence(Presence.online(Activity.playing(config.getPlayingText()))).block();
                log.info("Started Discord Client, waiting for messages...");
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void startServices() {
        Discord4JBotService loginService = application.get(Discord4JBotService.class);
        loginService.register();

        Discord4JBotMessageService messageService = application.get(Discord4JBotMessageService.class);
        messageService.register();

    }
}
