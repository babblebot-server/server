package uk.co.bjdavies.discord.services;

import com.google.inject.Inject;
import discord4j.core.DiscordClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * This is a simple class to create a thread and login a discord bot using {@link discord4j.core.DiscordClient}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class Discord4JBotService {
    private final DiscordClient client;

    @Inject
    public Discord4JBotService(DiscordClient client) {
        this.client = client;
    }

    public void register() {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(new FutureTask<>(() -> {
            client.login().block();
            service.shutdown();
            return 0;
        }));
    }
}
